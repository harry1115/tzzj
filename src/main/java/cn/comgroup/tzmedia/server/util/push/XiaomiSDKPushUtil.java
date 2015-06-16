/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.comgroup.tzmedia.server.util.push;

import com.xiaomi.xmpush.server.Constants;
import com.xiaomi.xmpush.server.Message;
import com.xiaomi.xmpush.server.Sender;
import com.xiaomi.xmpush.server.TargetedMessage;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pcnsh222
 */
public class XiaomiSDKPushUtil {
    private final static String APPSECRET = "qtV3m19xlqwpvHOmp0BtOA==";
    private final static String MY_PACKAGE_NAME = "cn.comgroup.tzmedia";

    public static void sendMessage(String msgPayLoad, String title, String desc, String regId) throws Exception {
        Constants.useOfficial();
        Sender sender = new Sender(APPSECRET);
        Message message = new Message.Builder()
                .title(title)
                .description(desc).payload(msgPayLoad)
                .restrictedPackageName(MY_PACKAGE_NAME)
                .notifyType(1)     // 使用默认提示音提示
                .build();
        sender.send(message, regId, 0); //根据regID，发送消息到指定设备上，不重试。
    }

    public static void sendMessages(String alias,String msgPayLoad, String title, String desc) throws Exception {
        Constants.useOfficial();
        Sender sender = new Sender(APPSECRET);
        List<TargetedMessage> messages = new ArrayList<TargetedMessage>();
        String [] aliasArray = alias.split(",");
        for (int i = 0; i < aliasArray.length; i++) {
            TargetedMessage targetedMessage1 = new TargetedMessage();
            targetedMessage1.setTarget(TargetedMessage.TARGET_TYPE_ALIAS, aliasArray[i]);
            Message message = new Message.Builder()
                .title(title)
                .description(desc).payload(msgPayLoad)
                .restrictedPackageName(MY_PACKAGE_NAME)
                .notifyType(1)     // 使用默认提示音提示
                .build();
            targetedMessage1.setMessage(message);
            messages.add(targetedMessage1);
        }
        sender.send(messages, 0); //根据alias，发送消息到指定设备上，不重试。
    }

    public static void sendMessageToAlias(String alias,String msgPayLoad, String title, String desc) throws Exception {
        Constants.useOfficial();
        Sender sender = new Sender(APPSECRET);
        Message message = new Message.Builder()
                .title(title)
                .description(desc).payload(msgPayLoad)
                .restrictedPackageName(MY_PACKAGE_NAME)
                .notifyType(1)     // 使用默认提示音提示
                .build();
        sender.sendToAlias(message, alias, 0); //根据alias，发送消息到指定设备上，不重试。
    }

    public static void sendMessageToAliases(String alias,String msgPayLoad, String title, String desc, Long messageId) throws Exception {
        System.out.println("alias " + alias + "  title " + title);
        Constants.useOfficial();
        Sender sender = new Sender(APPSECRET);
        String [] aliasArray = alias.split(",");
        List<String> aliasList = new ArrayList<String>();
        for (int i = 0; i < aliasArray.length; i++) {
            aliasList.add(aliasArray[i]);
        }
        Message message = new Message.Builder()
                .title(title)
                .description(desc).payload(msgPayLoad)
                .restrictedPackageName(MY_PACKAGE_NAME)
                .notifyType(1)     // 使用默认提示音提示
                .notifyId(messageId.intValue())
                .build();
        sender.sendToAlias(message, aliasList, 0); //根据aliasList，发送消息到指定设备上，不重试。
    }

    public static void sendBroadcast(String msgPayLoad, String title, String desc, String topic) throws Exception {
        Constants.useOfficial();
        Sender sender = new Sender(APPSECRET);
        Message message = new Message.Builder()
                .title(title)
                .description(desc).payload(msgPayLoad)
                .restrictedPackageName(MY_PACKAGE_NAME)
                .notifyType(1)     // 使用默认提示音提示
                .build();
        sender.broadcast(message, topic, 0); //根据topic，发送消息到指定一组设备上，不重试。
    }
}
