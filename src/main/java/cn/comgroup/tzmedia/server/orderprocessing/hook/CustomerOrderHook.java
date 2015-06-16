/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.comgroup.tzmedia.server.orderprocessing.hook;

import cn.comgroup.tzmedia.server.admin.entity.CouponStatus;
import cn.comgroup.tzmedia.server.admin.entity.UserCoupon;
import cn.comgroup.tzmedia.server.orderprocessing.entity.CustomerOrder;
import cn.comgroup.tzmedia.server.orderprocessing.entity.CustomerOrderStatus;
import cn.comgroup.tzmedia.server.shop.entity.Playbill;
import cn.comgroup.tzmedia.server.shop.entity.UserAction;
import cn.comgroup.tzmedia.server.shop.entity.UserActionUtil;
import cn.comgroup.tzmedia.server.util.tx.TransactionManager;
import cn.comgroup.tzmedia.server.util.tx.Transactional;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * Business logic of Order put here.
 *
 * @author pcnsh197
 */
public class CustomerOrderHook {

    private final CustomerOrder order;
    private final EntityManager em;

    public CustomerOrderHook(CustomerOrder order, EntityManager em) {
        this.order = order;
        this.em = em;
    }

    public void updateOrder() {
        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                if (order.getPlaybillId() > 0) {
                    Playbill playbill = em.find(Playbill.class, order.getPlaybillId());
                    if (playbill != null) {
                        if (order.getOrderStatus().equals(CustomerOrderStatus.PAID)) {
//                                || order.getOrderStatus().equals(CustomerOrderStatus.PROCESSED)) {
                            Query queryUA = em.createNamedQuery("UserAction.findByOrderNumber");
                            queryUA.setParameter("replyComment", order.getOrderNumber());
                            List<UserAction> userActions = queryUA.getResultList();
                            UserAction userAction = null;
                            if (userActions.isEmpty()) {
                                    userAction = UserActionUtil.
                                    buildPayUserAction(playbill, order, em);
                            } else {
                                userAction = userActions.get(0);
                            }


                            if (playbill.paySong(userAction,playbill)) {
                                em.merge(playbill);
                                em.persist(userAction);
                                List<CustomerOrder> orders = getOrdersShouldBeSetClosed(em, playbill);
                                for (CustomerOrder co : orders) {
                                    if (!co.getOrderNumber().equals(order.getOrderNumber())) {
                                        co.setOrderStatus(CustomerOrderStatus.CLOSED);
                                        co.setComment(co.getComment()
                                                + " Closed as another order is paid,order number: "
                                                + order.getOrderNumber());
                                        em.merge(co);
                                    }
                                }
                            }
//                            }
                        } else if (order.getOrderStatus().equals(CustomerOrderStatus.TOBEPAID)
                                || order.getOrderStatus().equals(CustomerOrderStatus.TOBECOLLECTED)) {
                            order.setSingerId(playbill.getSingerId());
                            order.setPlaybillName(playbill.getPlaybillName());
                            order.setSingerName(playbill.getSinger().getSingerName());
                        }
                    }
                }
                if (order.getOrderStatus().equals(CustomerOrderStatus.OVERTIME)
                        || order.getOrderStatus().equals(CustomerOrderStatus.CLOSED)
                        || order.getOrderStatus().equals(CustomerOrderStatus.REFUNDED)) {
                    closeOrderFollowupOperation();
                }
                em.merge(order);
            }
        });
    }
    
    private List<CustomerOrder> getOrdersShouldBeSetClosed(EntityManager em, Playbill playbill) {
        Query query = em.createNamedQuery("CustomerOrder.findByPlaybillId");
        query.setParameter("playbillId", playbill.getPlaybillId());
        List<CustomerOrder> customerOrders = query.getResultList();
        List<CustomerOrder> ordersFound = new LinkedList<>();
        for (CustomerOrder co : customerOrders) {
            if (co.getOrderStatus().equals(CustomerOrderStatus.TOBEPAID)
                    || co.getOrderStatus().equals(CustomerOrderStatus.TOBECOLLECTED)) {
                ordersFound.add(co);
            }
        }
        return ordersFound;

    }

    public void deleteOrder() {
        TransactionManager.manage(new Transactional(em) {
            @Override
            public void transact() {
                closeOrderFollowupOperation();
                em.remove(order);
            }
        });
    }

    private void closeOrderFollowupOperation() {
        if (order.getCouponNumber() > 0) {
            UserCoupon userCoupon = em.find(UserCoupon.class, order.getCouponNumber());
            if (userCoupon != null) {
                Query queryCO = em.createNamedQuery("CustomerOrder.findByCouponNumber");
                queryCO.setParameter("couponNumber", order.getCouponNumber());
                List<CustomerOrder> cos = queryCO.getResultList();
                boolean shouldResetUserCoupon = true;
                if (cos.size() > 1) {
                    for (CustomerOrder orderFound : cos) {
                        if (orderFound.getOrderStatus().equals(CustomerOrderStatus.PROCESSED)
                                || orderFound.getOrderStatus().equals(CustomerOrderStatus.PAID)) {
                            shouldResetUserCoupon = false;
                            break;
                        }
                    }
                }
                if (shouldResetUserCoupon) {
                    userCoupon.setCouponStatus(CouponStatus.NEW);
                    em.merge(userCoupon);
                }
            }
        }
//        final Playbill playbill = em.find(Playbill.class,
//                order.getPlaybillId());
//        if (playbill != null && playbill.reactiveSong()) {
//            em.merge(playbill);
//        }

    }

}
