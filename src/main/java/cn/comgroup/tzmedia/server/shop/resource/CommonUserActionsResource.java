/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.comgroup.tzmedia.server.shop.resource;

import cn.comgroup.tzmedia.server.admin.entity.User;
import cn.comgroup.tzmedia.server.shop.entity.ActionType;
import cn.comgroup.tzmedia.server.shop.entity.Activity;
import cn.comgroup.tzmedia.server.shop.entity.Playbill;
import cn.comgroup.tzmedia.server.shop.entity.Shop;
import cn.comgroup.tzmedia.server.shop.entity.UserAction;
import cn.comgroup.tzmedia.server.util.query.QueryUtil;
import cn.comgroup.tzmedia.server.util.tx.TransactionManager;
import cn.comgroup.tzmedia.server.util.tx.Transactional;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.ServletConfig;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * CommonUserActionsResource
 *
 * @author pcnsh197
 */
@Path("/commonuseractions/")
public class CommonUserActionsResource {
    private final int USER_NOT_SET = 3;
    
    @Context
    private ServletConfig servletConfig;
    @PersistenceUnit(unitName = "TZMediaPU")
    EntityManagerFactory emf;

    public CommonUserActionsResource() {
    }
    
    private List<UserAction> getUserActionsUsingCriteria(
            Boolean excludePraise,
            long actionId,
            String userId,
            ActionType actionType,
            int playbillId,
            int shopId,
            int activityName,
            final EntityManager em) 
            throws ParseException {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        
        //excludePraise and actionType only one parameter can be not null, 
        //because one use equal another use not equal
        if (excludePraise != null) {
            actionType = null;
        }
        
        CriteriaQuery<UserAction> c = cb.createQuery(UserAction.class);
        Root<UserAction> userAction = c.from(UserAction.class);
        c.select(userAction);
        c.orderBy(cb.desc(userAction.get("actionOrderTime")));
        c.distinct(true);
        List<Predicate> criteria = new ArrayList<>();

        if (playbillId > 0) {
            ParameterExpression<Integer> p1 = cb.parameter(Integer.class, "playbillId");
            criteria.add(cb.equal(userAction.get("playbillId"), p1));
        }
        if (shopId > 0) {
            ParameterExpression<Integer> p1 = cb.parameter(Integer.class, "shopId");
            criteria.add(cb.equal(userAction.get("shopId"), p1));
        }
        if (activityName > 0) {
            ParameterExpression<Integer> p1 = cb.parameter(Integer.class, "activityName");
            criteria.add(cb.equal(userAction.get("activityName"), p1));
        }
        
        if (excludePraise != null) {
            ParameterExpression<ActionType> p1 = cb.parameter(ActionType.class, "actionType");
            criteria.add(cb.notEqual(userAction.get("actionType"), p1));
        }
        
        if (QueryUtil.queryParameterProvided(userId)) {
            ParameterExpression<String> p1 = cb.parameter(String.class, "userId");
            if (userId.contains(QueryUtil.WILDCARDS)) {
                criteria.add(cb.like(userAction.<String>get("userId"), p1));
            } else {
                criteria.add(cb.equal(userAction.get("userId"), p1));
            }
        }

        if (actionType != null) {
            ParameterExpression<ActionType> p1 = cb
                    .parameter(ActionType.class, "actionType");
            criteria.add(cb.equal(userAction.get("actionType"), p1));
        }
        
        if (actionId > 0) {
            ParameterExpression<Long> p1= cb.parameter(Long.class, "actionId");
            criteria.add(cb.equal(userAction.get("actionId"), p1));
        }
        
        
        if (criteria.size() == 1) {
            c.where(criteria.get(0));
        } else if (criteria.size() > 1) {
            c.where(cb.and(criteria.toArray(new Predicate[0])));
        }
        TypedQuery<UserAction> q = em.createQuery(c); 
        
        if (excludePraise !=null) {
            q.setParameter("actionType", ActionType.PRAISE);
        }
        
        if (QueryUtil.queryParameterProvided(userId)) {
            q.setParameter("userId", userId
                    .replace(QueryUtil.WILDCARDS, QueryUtil.PERCENTAGE));
        }
        
        if(actionType!=null){
            q.setParameter("actionType", actionType);
        }
        
        if (actionId >0) {
            q.setParameter("actionId", actionId);
        }
        
        if (playbillId >0) {
            q.setParameter("playbillId", playbillId);
        }
        if (shopId >0) {
            q.setParameter("shopId", shopId);
        }
        if (activityName >0) {
            q.setParameter("activityName", activityName);
        }
        
        List<UserAction> userActions = q.getResultList();
        return userActions;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public UserAction[] getUserActions(
            @QueryParam("checked") String checked,
            @QueryParam("excludePraise") Boolean excludePraise,
            @QueryParam("actionId") long actionId,
            @QueryParam("userId") String userId,
            @QueryParam("actionType") ActionType actionType,
            @QueryParam("playbillId") int playbillId,
            @QueryParam("shopId") int shopId,
            @QueryParam("activityName") int activityName) throws ParseException {
        final EntityManager em=emf.createEntityManager();
        List<UserAction> userActions = getUserActionsUsingCriteria(
                excludePraise, 
                actionId,
                userId,
                actionType,
                playbillId,
                shopId,
                activityName,
                em);
        return userActions.toArray(
                new UserAction[userActions.size()]);
    }
    
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postUserAction(final UserAction userAction) {
        final EntityManager em=emf.createEntityManager();
        User user = em.find(User.class, userAction.getUserId());
        if (user == null) {
            Logger.getLogger(CommonUserActionsResource.class.getName())
                    .log(Level.WARNING, "######### User action has no user set.");
            return Response.ok(USER_NOT_SET).build();
        }
        userAction.setUser(user);
        if (userAction.getReplyUserId() != null) {
            userAction.setReplyUser(em.find(User.class, userAction.getReplyUserId()));
        }

        if (userAction.getPlaybillId() > 0) {
            userAction.setPlaybill(em.find(Playbill.class, userAction.getPlaybillId()));
        }

        if (userAction.getShopId() > 0) {
            userAction.setShop(em.find(Shop.class, userAction.getShopId()));
        }

        if (userAction.getActivityName() > 0) {
            userAction.setActivity(em.find(Activity.class, userAction.getActivityName()));
        }

        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                em.persist(userAction);
            }
        });
        return Response.ok().build();
    }
    
    
    @DELETE
    public void deleteUserAction(
            @QueryParam("actionId") long actionId,
            @QueryParam("userId") String userId,
            @QueryParam("actionType") ActionType actionType) throws ParseException {
        final EntityManager em=emf.createEntityManager();
        final List<UserAction> userActions = getUserActionsUsingCriteria(
                null,
                actionId,
                userId,
                actionType,
                0, 0, 0,em);
        if (!userActions.isEmpty()) {
            TransactionManager.manage(new Transactional(em) {
                @Override
                public void transact() {
                    for (UserAction userAction : userActions) {
                        em.remove(userAction);
                    }
                }
            });
        }
    }
    
}
