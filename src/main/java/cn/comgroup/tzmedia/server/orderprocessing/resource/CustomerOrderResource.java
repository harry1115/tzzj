package cn.comgroup.tzmedia.server.orderprocessing.resource;

import cn.comgroup.tzmedia.server.admin.entity.User;
import cn.comgroup.tzmedia.server.authentication.filter.AuthenticationConstants;
import cn.comgroup.tzmedia.server.factory.TZZJHookFactory;
import cn.comgroup.tzmedia.server.orderprocessing.entity.CustomerOrder;
import cn.comgroup.tzmedia.server.orderprocessing.entity.CustomerOrderLine;
import cn.comgroup.tzmedia.server.orderprocessing.entity.CustomerOrderStatus;
import cn.comgroup.tzmedia.server.orderprocessing.hook.CustomerOrderHook;
import cn.comgroup.tzmedia.server.product.entity.Product;
import cn.comgroup.tzmedia.server.shop.entity.Playbill;
import cn.comgroup.tzmedia.server.shop.entity.Shop;
import cn.comgroup.tzmedia.server.singer.entity.Song;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import javax.persistence.EntityManager;
import javax.servlet.ServletConfig;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

/**
 * CustomerOrderResource
 * peter.liu@comgroup.cn
 */
public class CustomerOrderResource {

    private final String orderNumber; // productNumber from url
    private final CustomerOrder orderEntity; // appropriate jpa user entity

//    UriInfo uriInfo; // actual uri info provided by parent resource
    private final EntityManager em; // entity manager provided by parent resource
    private final ServletConfig servletConfig;
    
    private final int ORDER_DOES_NOT_DELETE= 2;
    private final int ORDER_ALREADY_PROCESSED = 3;

    /**
     * Creates a new instance of ProductResource
     *
     * @param em
     * @param orderNumber
     * @param servletConfig
     */
    public CustomerOrderResource(EntityManager em, String orderNumber,ServletConfig servletConfig) {
        this.orderNumber = orderNumber;
        this.em = em;
        this.orderEntity = em.find(CustomerOrder.class, orderNumber);
        this.servletConfig=servletConfig;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public CustomerOrder getCustomerOrder() {
        if (null == orderEntity) {
            throw new WebApplicationException(404);
        }
        return orderEntity;
    }

    /**
     * Method for update order
     *
     * @param customerOrder
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putCustomerOrder(final CustomerOrder customerOrder) {
        if (!orderEntity.changeStatusValid(customerOrder)) {
            return Response.status(Response.Status.CONFLICT).build();
        }
        if (customerOrder.getShopId()>0) {
            customerOrder.setShop(em.find(Shop.class, customerOrder.getShopId()));
        }
        
        if (customerOrder.getUserId() != null) {
            customerOrder.setUser(em.find(User.class, customerOrder.getUserId()));
        }
        
        List<CustomerOrderLine> orderLines=customerOrder.getCustomerOrderLines();
        customerOrder.setCustomerOrderLines(new ArrayList<CustomerOrderLine>());
        for (CustomerOrderLine orderLine : orderLines) {
            if (orderLine.getProductNumber() != null) {
                orderLine.setProductName(em.find(Product.class,
                        orderLine.getProductNumber()).getProductName());
            }
            if (orderLine.getPlaybillId() >0) {
                orderLine.setPlaybillName(em.find(Playbill.class,
                        orderLine.getPlaybillId()).getPlaybillName());
            }
            if (orderLine.getSongId()>0) {
                orderLine.setSongName(em.find(Song.class,
                        orderLine.getSongId()).getSongName());
            }
            customerOrder.addCustomerOrderLine(orderLine);
        }
        
        CustomerOrderHook coHook = TZZJHookFactory.
                getCustomerOrderHook(customerOrder, em);
        coHook.updateOrder();
        return Response.ok(AuthenticationConstants.UPDATESUCCESS).build();
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("changestatus")
    public Response changeOrderStatus(final CustomerOrder customerOrder) {
        if (!orderEntity.changeStatusValid(customerOrder)) {
            return Response.status(Response.Status.CONFLICT).build();
        }
        orderEntity.setOrderStatus(customerOrder.getOrderStatus());
        CustomerOrderHook coHook = TZZJHookFactory.
                getCustomerOrderHook(orderEntity, em);
        coHook.updateOrder();
        return Response.ok(AuthenticationConstants.UPDATESUCCESS).build();
    }

    @DELETE
    public Response deleteOrder() {
//        CustomerOrder customerOrder = new CustomerOrder();
//        customerOrder.setOrderStatus(CustomerOrderStatus.CLOSED);
//        if (!orderEntity.changeStatusValid(customerOrder)) {
//            return Response.status(Response.Status.CONFLICT).build();
//        }
//        orderEntity.setOrderStatus(customerOrder.getOrderStatus());
//        CustomerOrderHook coHook = TZZJHookFactory.
//                getCustomerOrderHook(orderEntity, em);
//        coHook.updateOrder();
//        return Response.ok(AuthenticationConstants.UPDATESUCCESS).build();
        
        
        //Will be change back when client use logic 
        if (null == orderEntity) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(ORDER_DOES_NOT_DELETE).build();
        }
        if (orderEntity.getOrderStatus().equals(CustomerOrderStatus.PROCESSED)
                ||orderEntity.getOrderStatus().equals(CustomerOrderStatus.PAID)) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(ORDER_ALREADY_PROCESSED).build();
        }
        CustomerOrderHook coHook = TZZJHookFactory.
                getCustomerOrderHook(orderEntity, em);
        coHook.deleteOrder();
        return Response.ok(AuthenticationConstants.UPDATESUCCESS).build();
    }
}
