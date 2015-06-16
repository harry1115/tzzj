/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.comgroup.tzmedia.server.util.query;

/**
 * QueryUtil
 *
 * @author pcnsh197
 */
public class QueryUtil {

    public static String WILDCARDS = "*";
    public static String PERCENTAGE = "%";
    
    public static boolean queryParameterProvided(String queryParam) {
        return (queryParam != null && !queryParam.trim().equals(""));
    }
}
