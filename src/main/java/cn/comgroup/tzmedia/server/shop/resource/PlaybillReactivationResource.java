/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.comgroup.tzmedia.server.shop.resource;

import cn.comgroup.tzmedia.server.orderprocessing.entity.CustomerOrder;
import cn.comgroup.tzmedia.server.orderprocessing.entity.CustomerOrderStatus;
import cn.comgroup.tzmedia.server.shop.entity.ActionType;
import cn.comgroup.tzmedia.server.shop.entity.Playbill;
import cn.comgroup.tzmedia.server.shop.entity.UserAction;
import cn.comgroup.tzmedia.server.util.tx.TransactionManager;
import cn.comgroup.tzmedia.server.util.tx.Transactional;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletConfig;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * PlaybillImagesResource
 *
 * @author pcnsh197
 */
public class PlaybillReactivationResource {

    private final Playbill playbillEntity; // appropriate jpa Playbill entity
    private final EntityManager em; // entity manager provided by parent resource
    private final ServletConfig servletConfig;

    public PlaybillReactivationResource(EntityManager em, Playbill playbillEntity,
            ServletConfig servletConfig) {
        this.em = em;
        this.playbillEntity = playbillEntity;
        this.servletConfig = servletConfig;
    }

    /**
     * reactivate
     *
     * @return Response
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response reactivate() {
        if (playbillEntity == null) {
            throw new WebApplicationException(404);
        }
        Logger.getLogger(PlaybillReactivationResource.class.getName())
                .log(Level.INFO,
                        "Reactive playbill {0} is called",
                        playbillEntity.getPlaybillId());
        if (playbillEntity.reactiveSong()) {
            TransactionManager.manage(new Transactional(em) {
                @Override
                public void transact() {
                    em.merge(playbillEntity);
                    Query query = em.createNamedQuery("UserAction.findByPIDAndActionType");
                    query.setParameter("playbillId", playbillEntity.getPlaybillId());
                    query.setParameter("actionType", ActionType.GRABSONG);
                    List<UserAction> userActions = query.getResultList();
                    for (UserAction ua : userActions) {
                        em.remove(ua);
                    }
                    Query queryCO = em.createNamedQuery("CustomerOrder.findByPlaybillId");
                    queryCO.setParameter("playbillId", playbillEntity.getPlaybillId());
                    List<CustomerOrder> cos = queryCO.getResultList();
                    for (CustomerOrder order : cos) {
                        if (order.getOrderStatus().equals(CustomerOrderStatus.TOBECOLLECTED)
                                || order.getOrderStatus().equals(CustomerOrderStatus.TOBEPAID)) {
                            order.setOrderStatus(CustomerOrderStatus.CLOSED);
                            em.merge(order);
                        }
                    }
                }
            });
        }
        return Response.ok().build();
    }
}
