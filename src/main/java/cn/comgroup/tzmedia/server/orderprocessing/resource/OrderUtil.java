/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.comgroup.tzmedia.server.orderprocessing.resource;

import cn.comgroup.tzmedia.server.common.entity.PaymentTerm;
import cn.comgroup.tzmedia.server.orderprocessing.entity.CustomerOrder;
import cn.comgroup.tzmedia.server.orderprocessing.entity.CustomerOrderStatus;
import cn.comgroup.tzmedia.server.orderprocessing.entity.OrderType;
import cn.comgroup.tzmedia.server.util.query.QueryUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 *
 * @author pcnsh197
 */
public class OrderUtil {
     public static List<CustomerOrder> getOrdersUsingCriteria(String orderNumber,
            String fromOrderDate,
            String toOrderDate,
            String userId,
            CustomerOrderStatus orderStatus,
            int playbillId,
            Boolean hasToBeProcessed,
            int tableNumber,
            OrderType orderType,
            PaymentTerm paymentTerm,
            int shopId,
            EntityManager em) throws ParseException {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        
        CriteriaQuery<CustomerOrder> c = cb.createQuery(CustomerOrder.class);
        Root<CustomerOrder> customerOrder = c.from(CustomerOrder.class);
        c.select(customerOrder);
        c.orderBy(cb.desc(customerOrder.get("orderNumber")));
        c.distinct(true);
        List<Predicate> criteria = new ArrayList<>();
        if (QueryUtil.queryParameterProvided(orderNumber)) {
            ParameterExpression<String> p = cb.parameter(String.class, "orderNumber");
            if (orderNumber.contains(QueryUtil.WILDCARDS)) {
                criteria.add(cb.like(customerOrder.<String>get("orderNumber"), p));
            } else {
                criteria.add(cb.equal(customerOrder.get("orderNumber"), p));
                
            }
        }
        
        if (QueryUtil.queryParameterProvided(fromOrderDate)) {
            ParameterExpression<Calendar> p = cb.parameter(Calendar.class, "fromOrderDate");
            criteria.add(cb.greaterThanOrEqualTo(customerOrder.<Calendar>get("orderDate"), p));
        }
        
        if (QueryUtil.queryParameterProvided(toOrderDate)) {
            ParameterExpression<Calendar> p = cb.parameter(Calendar.class, "toOrderDate");
            criteria.add(cb.lessThanOrEqualTo(customerOrder.<Calendar>get("orderDate"), p));
        }
        
        if (QueryUtil.queryParameterProvided(userId)) {
             ParameterExpression<String> p = cb.parameter(String.class, "userId");
            if (userId.contains(QueryUtil.WILDCARDS)) {
                criteria.add(cb.like(customerOrder.<String>get("userId"), p));
            } else {
                criteria.add(cb.equal(customerOrder.get("userId"), p));
            }
        }
        
        if (orderStatus!=null) {
            ParameterExpression<CustomerOrderStatus> p = cb
                    .parameter(CustomerOrderStatus.class, "orderStatus");
            criteria.add(cb.equal(customerOrder.get("orderStatus"), p));
            //if order status is provided, hasToBeProcessed should be removed from query clause.
            hasToBeProcessed=null;
        }
        if(orderType!=null){
             ParameterExpression<OrderType> p = cb
                    .parameter(OrderType.class, "orderType");
            criteria.add(cb.equal(customerOrder.get("orderType"), p));
        }
        
        if(paymentTerm!=null){
             ParameterExpression<PaymentTerm> p = cb
                    .parameter(PaymentTerm.class, "paymentTerm");
            criteria.add(cb.equal(customerOrder.get("paymentTerm"), p));
        }
        
        if (playbillId > 0) {
            ParameterExpression<Integer> p = cb.parameter(Integer.class, "playbillId");
            criteria.add(cb.equal(customerOrder.get("playbillId"), p));
        }
        
        if (shopId > 0) {
            ParameterExpression<Integer> p = cb.parameter(Integer.class, "shopId");
            criteria.add(cb.equal(customerOrder.get("shopId"), p));
        }
        
        if (hasToBeProcessed != null&&hasToBeProcessed.equals(true)) {
            criteria.add(cb.or(cb.equal(customerOrder.get("orderStatus"),
                    CustomerOrderStatus.TOBECOLLECTED),
                    cb.equal(customerOrder.get("orderStatus"),
                            CustomerOrderStatus.PAID)));
        }
        
        if (tableNumber > 0) {
            ParameterExpression<Integer> p = cb.parameter(Integer.class, "tableNumber");
            criteria.add(cb.equal(customerOrder.get("tableNumber"), p));
        }
        
        if (criteria.size() == 1) {
            c.where(criteria.get(0));
        } else if (criteria.size() > 1) {
            c.where(cb.and(criteria.toArray(new Predicate[0])));
        }
        TypedQuery<CustomerOrder> q = em.createQuery(c);

        if (QueryUtil.queryParameterProvided(orderNumber)) {
            q.setParameter("orderNumber", orderNumber
                    .replace(QueryUtil.WILDCARDS, QueryUtil.PERCENTAGE));
        }

        if (QueryUtil.queryParameterProvided(fromOrderDate)) {
            Calendar fromDate = Calendar.getInstance();
            fromDate.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(fromOrderDate));
            q.setParameter("fromOrderDate", fromDate);
        }

        if (QueryUtil.queryParameterProvided(toOrderDate)) {
            Calendar toDate = Calendar.getInstance();
            toDate.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(toOrderDate));

            q.setParameter("toOrderDate", toDate);
        }

        if (QueryUtil.queryParameterProvided(userId)) {
            q.setParameter("userId", userId
                    .replace(QueryUtil.WILDCARDS, QueryUtil.PERCENTAGE));
        }
        
        if (orderStatus !=null) {
            q.setParameter("orderStatus", orderStatus);
        }
        if(orderType!=null){
            q.setParameter("orderType", orderType);
        }
        if(paymentTerm!=null){
            q.setParameter("paymentTerm", paymentTerm);
        }
        
        if (playbillId >0) {
            q.setParameter("playbillId", playbillId);
        }
        if (shopId >0) {
            q.setParameter("shopId", shopId);
        }
        
        if (tableNumber > 0) {
             q.setParameter("tableNumber", tableNumber);
        }
       
        return q.getResultList();
    }
    
}
