/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.comgroup.tzmedia.server.util.calculator;

import java.math.BigDecimal;

/**
 * DoubleUtil
 *
 * @author pcnsh197
 */
public class DoubleUtil {

    public static double round(double amount) {
        BigDecimal bigOrderAmount = new BigDecimal(amount);
        return bigOrderAmount.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

}
