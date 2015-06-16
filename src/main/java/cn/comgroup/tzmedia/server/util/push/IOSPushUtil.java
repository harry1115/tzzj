/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.comgroup.tzmedia.server.util.push;

import cn.comgroup.tzmedia.server.admin.entity.UserMessage;
import cn.comgroup.tzmedia.server.shop.entity.UserAction;
import java.util.ArrayList;
import java.util.List;
import javapns.Push;
import javapns.notification.PushNotificationPayload;

/**
 *
 * @author pcnsh222
 */
public class IOSPushUtil {

    public static void sendMessage(String source, String title, String content, String deviceToken, UserAction uc, String shopId) {
        try {
            PushNotificationPayload payload = new PushNotificationPayload();
            System.out.println("deviceToken = " + deviceToken);
            
//            ArrayList<UserMessage> params = new ArrayList<UserMessage>();
//            if(um !=null) {
//                params.add(um);
//            }
            
            ArrayList<String> parameters = new ArrayList<String>();
            parameters.add(uc.getUserId());
            parameters.add(uc.getUserName());
            parameters.add(uc.getComment());
            parameters.add(uc.getUserRole().name());
            parameters.add(uc.getPlaybillId() + "");
            parameters.add(uc.getActionId() +"");
            parameters.add(shopId +"");

            payload.addCustomAlertActionLocKey(source);
            payload.addCustomAlertLocKey(title);
            payload.addCustomAlertBody(content);
            payload.addCustomAlertLocArgs(parameters);
            payload.addCustomDictionary("type", "0");

            payload.addBadge(1);

            payload.addSound("default");

            List<String> tokens = new ArrayList<String>();
            tokens.add(deviceToken.substring(1,deviceToken.length() - 1));
//            Push.payload(payload, "PushD.p12", "abc123", true, tokens);
            Push.payload(payload, "PushTest.p12", "abc123", false, tokens);

        } catch (Exception e) {
            e.printStackTrace();                          
        }

    }
}
