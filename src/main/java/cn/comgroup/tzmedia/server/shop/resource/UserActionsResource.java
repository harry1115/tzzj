/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.comgroup.tzmedia.server.shop.resource;

import cn.comgroup.tzmedia.server.admin.entity.User;
import cn.comgroup.tzmedia.server.admin.entity.UserDevice;
import cn.comgroup.tzmedia.server.admin.entity.UserMessage;
import cn.comgroup.tzmedia.server.admin.entity.UserMessageType;
import cn.comgroup.tzmedia.server.shop.entity.ActionType;
import cn.comgroup.tzmedia.server.shop.entity.Playbill;
import cn.comgroup.tzmedia.server.shop.entity.UserAction;
import cn.comgroup.tzmedia.server.util.push.IOSPushUtil;
import cn.comgroup.tzmedia.server.util.push.ThreadUtility;
import cn.comgroup.tzmedia.server.util.push.XiaomiSDKPushUtil;
import cn.comgroup.tzmedia.server.util.query.QueryUtil;
import cn.comgroup.tzmedia.server.util.tx.TransactionManager;
import cn.comgroup.tzmedia.server.util.tx.Transactional;
import com.xiaomi.xmpush.server.Constants;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * UserActionsResource
 *
 * @author pcnsh197
 */
public class UserActionsResource {

    private final Playbill playbillEntity; // appropriate jpa Playbill entity
    

    private final EntityManagerFactory emf;
    private final EntityManager em; // entity manager provided by parent resource
    private final ServletConfig servletConfig;
//    private final int minutesToReactivate;

//    private final int GRAB_SONG_SUCCESS = 1;
    private final int GRAB_SONG_FAILURE = 2;
    private final int USER_NOT_SET = 3;
    private final int PAY_SONG_FAILURE = 4;
    private final String actionType;

    public UserActionsResource(EntityManagerFactory emf, Playbill playbillEntity, String actionType,
            ServletConfig servletConfig) {
        this.emf = emf;
        this.em = emf.createEntityManager();
        this.playbillEntity = em.find(Playbill.class, playbillEntity.getPlaybillId());
        this.servletConfig = servletConfig;
        this.actionType = actionType;
    }

    private List<UserAction> getUserActionsUsingCriteria(
            Boolean excludePraise,
            long actionId,
            String userId,
            ActionType actionType)
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

        ParameterExpression<Integer> p = cb.parameter(Integer.class, "playbillId");
        criteria.add(cb.equal(userAction.get("playbillId"), p));
        if (this.actionType != "") {
            if (null != this.actionType) {
                switch (this.actionType) {
                    case "GRABSONG":
                        criteria.add(cb.equal(userAction.get("actionType"), ActionType.GRABSONG));
                        break;
                    case "PAY":
                        criteria.add(cb.equal(userAction.get("actionType"), ActionType.PAY));
                        break;
                    case "COMMENT":
                        criteria.add(cb.equal(userAction.get("actionType"), ActionType.COMMENT));
                        break;
                    default:
                        criteria.add(cb.equal(userAction.get("actionType"), ActionType.PRAISE));
                        break;
                }
            }
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
            //   ParameterExpression<ActionType> p1 = cb
            //   .parameter(ActionType.class, "actionType");
            //  criteria.add(cb.equal(userAction.get("actionType"), p1));
        }

        if (actionId > 0) {
            ParameterExpression<Long> p1 = cb.parameter(Long.class, "actionId");
            criteria.add(cb.equal(userAction.get("actionId"), p1));
        }

        if (criteria.size() == 1) {
            c.where(criteria.get(0));
        } else if (criteria.size() > 1) {
            c.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        }
        TypedQuery<UserAction> q = em.createQuery(c);
        q.setParameter("playbillId", playbillEntity.getPlaybillId());

        if (excludePraise != null) {
            q.setParameter("actionType", ActionType.PRAISE);
        }

        if (QueryUtil.queryParameterProvided(userId)) {
            q.setParameter("userId", userId
                    .replace(QueryUtil.WILDCARDS, QueryUtil.PERCENTAGE));
        }

        if (actionType != null) {
            q.setParameter("actionType", actionType);
        }

        if (actionId > 0) {
            q.setParameter("actionId", actionId);
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
            @QueryParam("actionType") ActionType actionType) throws ParseException {
        List<UserAction> userActions = getUserActionsUsingCriteria(
                excludePraise,
                actionId,
                userId,
                actionType);
        return userActions.toArray(
                new UserAction[userActions.size()]);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postUserAction(final UserAction userAction) {
//        int returnStatus = 0;
        em.refresh(playbillEntity);
        User user = em.find(User.class, userAction.getUserId());
        if (user == null) {
            Logger.getLogger(UserActionsResource.class.getName())
                    .log(Level.WARNING, "######### User action has no user set {0}, playbill is:",
                            userAction.getPlaybillId());
            return Response.ok(USER_NOT_SET).build();
        }
        //Forbidden old client version to grab song.
        if (ActionType.GRABSONG.equals(userAction.getActionType())) {
            return Response.ok(GRAB_SONG_FAILURE).build();
        }
        userAction.setActionDateTime(Calendar.getInstance());
        userAction.setActionOrderTime(Calendar.getInstance());
        userAction.setUser(user);
        if (userAction.getReplyUserId() != null) {
            userAction.setReplyUser(em.find(User.class, userAction.getReplyUserId()));
        }

        userAction.setPlaybill(playbillEntity);
        final UserAction userActionExist = getExistUserAction(em, userAction);
        final boolean shouldCreateUserAction = (userActionExist == null);

        if (userAction.getActionType().equals(ActionType.COMMENT)
                && shouldCreateUserAction) {
            playbillEntity.increaseComment();
        } else if (userAction.getActionType().equals(ActionType.PRAISE)) {
            if (shouldCreateUserAction) {
                playbillEntity.increasePraise();
            }
        }
        final String[] deviceToken = new String[1];
        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {

                UserAction ua = em.find(UserAction.class, userAction.getActionId());
                if (ua != null) {
                    ua.setChecked(userAction.isChecked());
                    em.persist(ua);
                }



                em.merge(playbillEntity);
                if (shouldCreateUserAction) {
                    em.persist(userAction);
                    System.out.println("To finish persisting useraction" + new Date().toLocaleString());
                    if (userAction.getActionType().equals(ActionType.COMMENT)) {
                        if (userAction.getReplyUserId() != null) {
                            try {
                                UserDevice ud = em.find(UserDevice.class, userAction.getReplyUserId());
                                if (ud != null) {
                                    deviceToken[0] = ud.getDevicetoken();
                                }
                                System.out.println("To create message" + new Date().toLocaleString());
                                UserMessage userMessage = new UserMessage();
                                userMessage.setFromUserName(userAction.getUserName());
                                userMessage.setFromUserRole(userAction.getUserRole());
                                userMessage.setUserId(userAction.getReplyUserId());
                                userMessage.setFromUserId(userAction.getUserId());
                                userMessage.setPlaybillId(userAction.getPlaybillId());
                                userMessage.setActionId(userAction.getActionId());
                                userMessage.setShopId(playbillEntity.getShopId());
                                userAction.getShopId();
                                Calendar ca = Calendar.getInstance();
                                ca.setTime(new Date());
                                userMessage.setCreationDate(ca);
                                userMessage.setContent(userAction.getComment());
                                userMessage.setTitle(userAction.getUserName().length() > 6
                                        ? userAction.getUserName().substring(0, 6) + "...回复了您：" : userAction.getUserName() + "回复了您：");
                                userMessage.setUserMessageType(UserMessageType.USER);

                                em.persist(userMessage);
                                System.out.println("To finish persisting message" + new Date().toLocaleString());
                            } catch (Exception ex) {
                                Logger.getLogger(UserActionsResource.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                    }
                }
            }
        });

        ThreadUtility.execute(new Runnable() {

            @Override
            public void run() {
                System.out.println("To push message" + new Date().toLocaleString());

                if (userAction.getActionType().equals(ActionType.COMMENT)) {
                    if (userAction.getReplyUserId() != null) {
                        String title = userAction.getUserName().length() > 6
                                ? userAction.getUserName().substring(0, 6) + "...回复了您：" : userAction.getUserName() + "回复了您：";
                        String desc = userAction.getComment().length() > 10 ? userAction.getComment().substring(0, 10) + "..." : userAction.getComment();
                        String iosTitle = title + desc;
                        try {
                            //XiaomiSDKPushUtil.sendMessageToAliases("10003", "REPLY#"+userMessage.getMessageId()+"#"+userMessage.getPlaybillId()+"#"+userMessage.getActionId(), userMessage.getTitle(), desc,userMessage.getMessageId());
                            XiaomiSDKPushUtil.sendMessageToAliases(userAction.getReplyUserId(), "REPLY#" + "0#" + userAction.getPlaybillId() + "#" + userAction.getActionId() + "#" + playbillEntity.getShopId(), title, desc, 0L);
                        } catch (Exception ex) {
                            Logger.getLogger(UserActionsResource.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        if (deviceToken[0] != null) {
                            IOSPushUtil.sendMessage("REPLY", iosTitle, "0", deviceToken[0], userAction, playbillEntity.getShopId() + "");
                        }
                    }
                }
            }

        });

        System.out.println("To return  response" + new Date().toLocaleString());
        return Response.ok().build();
    }

    private UserAction getExistUserAction(EntityManager em, UserAction userAction) {
        if (userAction.getActionId() > 0) {
            UserAction ua = em.find(UserAction.class, userAction.getActionId());
            if (ua != null) {
                return ua;
            }
        }
        if (userAction.getActionType().equals(ActionType.PRAISE)) {
            Query query = em.createNamedQuery("UserAction.findByPIdUIdActionType");
            query.setParameter("playbillId", playbillEntity.getPlaybillId());
            query.setParameter("actionType", userAction.getActionType());
            query.setParameter("userId", userAction.getUserId());
            List<UserAction> userActions = query.getResultList();
            if (!userActions.isEmpty()) {
                UserAction actionfound = userActions.get(0);
                return actionfound;
            }
        }
        return null;
    }

    @DELETE
    public void deleteUserAction(
            @QueryParam("actionId") long actionId,
            @QueryParam("userId") String userId,
            @QueryParam("actionType") final ActionType actionType) throws ParseException {
        final List<UserAction> userActions = getUserActionsUsingCriteria(
                null,
                actionId,
                userId,
                actionType);
        if (!userActions.isEmpty()) {
            TransactionManager.manage(new Transactional(em) {
                @Override
                public void transact() {
                    for (UserAction userAction : userActions) {
                        System.out.println("useraction --- >" + userAction.getActionId());
                        if (userAction.getActionType().equals(ActionType.PRAISE) && userAction.getActionType().equals(actionType)) {
                            if (playbillEntity.getNumberOfPraises() > 0) {
                                playbillEntity
                                        .setNumberOfPraises(playbillEntity.getNumberOfPraises() - 1);
                            }
                            em.remove(userAction);
                        }
                        if (userAction.getActionType().equals(ActionType.COMMENT) && userAction.getActionType().equals(actionType)) {
                            if (playbillEntity.getNumberOfComments() > 0) {
                                playbillEntity
                                        .setNumberOfComments(playbillEntity.getNumberOfComments() - 1);
                            }
                            em.remove(userAction);
                        }

                    }
                    em.merge(playbillEntity);

                }
            });
        }
    }

    @GET
    @Path("praise/")
    public boolean isUserPraise(@QueryParam("userId") String userId) {
        Query query = em.createNamedQuery("UserAction.findByPIdUIdActionType");
        query.setParameter("playbillId", playbillEntity.getPlaybillId());
        query.setParameter("actionType", ActionType.PRAISE);
        query.setParameter("userId", userId);
        List<UserAction> userActions = query.getResultList();
        return userActions.size() > 0;
    }
}
