/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.comgroup.tzmedia.server.shop.entity;

import cn.comgroup.tzmedia.server.admin.entity.User;
import cn.comgroup.tzmedia.server.orderprocessing.entity.CustomerOrder;
import cn.comgroup.tzmedia.server.orderprocessing.entity.CustomerOrderLine;
import java.util.Calendar;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author pcnsh197
 */
public class UserActionUtil {
    public static UserAction buildPayUserAction(Playbill playbill, 
            CustomerOrder order,EntityManager em) {
        int currentOrders = playbill.getCurrentOrders();
        playbill.setCurrentOrders(currentOrders + 1);
        UserAction userAction = new UserAction();
        userAction.setActionType(ActionType.PAY);
        userAction.setPlaybill(playbill);
        userAction.setUser(em.find(User.class,order.getUserId()));
        userAction.setActionDateTime(Calendar.getInstance());
        userAction.setActionOrderTime(Calendar.getInstance());
        userAction.setReplyComment(order.getOrderNumber());
        userAction.setPlaybill(playbill);
        List<CustomerOrderLine> lines = order.getCustomerOrderLines();
        if (lines != null && !lines.isEmpty()) {
            CustomerOrderLine line = lines.get(0);
            userAction.setComment(line.getSongName());
            userAction.setGrabComment(line.getGrabComment());
        }
        return userAction;
    }
}
