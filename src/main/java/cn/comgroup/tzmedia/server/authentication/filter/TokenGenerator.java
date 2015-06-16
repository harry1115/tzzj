/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.comgroup.tzmedia.server.authentication.filter;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 *
 * @author pcnsh197
 */
public class TokenGenerator {

    private final static SecureRandom random = new SecureRandom();

    public static String nextToken() {
        return new BigInteger(130, random).toString(32);
    }
}
