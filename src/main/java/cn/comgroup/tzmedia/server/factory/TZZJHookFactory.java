/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.comgroup.tzmedia.server.factory;

import cn.comgroup.tzmedia.server.orderprocessing.entity.CustomerOrder;
import cn.comgroup.tzmedia.server.orderprocessing.hook.CustomerOrderHook;
import javax.persistence.EntityManager;

/**
 * TZZJHookFactory
 *
 * @author pcnsh197
 */
public class TZZJHookFactory {

    public static CustomerOrderHook getCustomerOrderHook(CustomerOrder orer, 
            EntityManager em) {
        return new CustomerOrderHook(orer, em);
    }
}
