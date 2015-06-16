/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.comgroup.tzmedia.server.authentication.filter;

/**
 *
 * @author pcnsh197
 */
public interface AuthenticationConstants {

    //Used to add the userid in SecurityFilter to be used in the login resource.
    public String USERIDADDEDINSECURITYFILTER = "USERIDADDEDINSECURITYFILTER";
    public String AUTHENTICATIONTOKEN = "AUTHENTICATIONTOKEN";
    //Header should contains this if the request is for user registration.
    public String USERREGISTERIDENTIFIER = "USERREGISTERIDENTIFIER";

    //Used to decide if the request is coming from web administration site.
    public String AUTHENTICATIONWEB = "AUTHENTICATIONWEB";

    public String MISSINGIDORPASSWORD = "1001";
    public String INVALIDIDORPASSWORD = "1002";
    //userid and password are not separated with ":"
    public String INVALIDSYNTAXFORIDANDPASSWORD = "1003";
    //Authentication header should start with \"Basic \
    public String AUTHENTICATIONHEADERSHOULDSTARTWITHBASIC = "1004";
    //Authentication credentials are required
    public String REQUIRECREDENTIALS = "1005";

    //Register success
    public String REGISTERSUCCESS = "1006";
    public String UPDATESUCCESS = "1007";
    
    public String LOGINSUCCESS="1008";
    public String LOGINFAILURE="1009";
    
    
    public String DUPLICATEUSERID = "1010";
    public String UPDATEFAILURE = "1011";
    
    //Used for forget password service
    public String EMAILPROVIDEDISNOTCORRECT="1012";
    public String EMAILSUCCESSFULLYSEND="1013";
    
    //Verify phone numer of user
    
    public String VERIFYSUCCESS = "1014";
    public String VERIFYFAILURE = "1015";
    
    public String MESSAGESENDSUCCESS = "1016";
    public String MESSAGESENDFAILURE = "1017";
    
    public String RESETPASSWORDSUCCESS="1018";
    public String RESETPASSWORDFAILURE="1019";
    
    public String USERTYPEWRONG = "1020";

}
