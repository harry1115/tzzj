/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.comgroup.tzmedia.server.authentication.filter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author pcnsh197
 */
public class TokenHolder {

    public final static Map<String, String> tokenUserMap = new ConcurrentHashMap<>(300);
    public final static Map<String, String> userTokenMap = new ConcurrentHashMap<>(300);
    
    public final static Map<String, String> tokenAdministratorMap = new ConcurrentHashMap<>(30);
    public final static Map<String, String> administratorTokenMap = new ConcurrentHashMap<>(30);
    
    
    public final static Map<String, String> userPasswordTokenMap = new ConcurrentHashMap<>(300);
    
    //phoneNumber:verficationCode
    public final static Map<String, String> verificationCodeMap = new ConcurrentHashMap<>(30);
    

}
