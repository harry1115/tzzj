/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.comgroup.tzmedia.server.util.mail;


import cn.comgroup.tzmedia.server.admin.resource.UsersResource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author pcnsh222
 */
public class SendCloudMail {
        /**
     * Send email using SMTP server.
     *
     * @param recipientEmail TO recipient
     * @param title title of the message
     * @param message message to be sent
     * connected state or if the message is not a MimeMessage
     */
    public static void send(String recipientEmail,
            String title, String message) throws UnsupportedEncodingException, IOException {
        String url = "http://sendcloud.sohu.com/webapi/mail.send.json";
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpost = new HttpPost(url);

        List nvps = new ArrayList();
        // 不同于登录SendCloud站点的帐号，您需要登录后台创建发信子帐号，使用子帐号和密码才可以进行邮件的发送。
        nvps.add(new BasicNameValuePair("api_user", "commobile_test_IxiZE1"));
        nvps.add(new BasicNameValuePair("api_key", "0tkHZ5vDdScYzRbn"));
        nvps.add(new BasicNameValuePair("from", "suport@tzzjmedia.net"));
        nvps.add(new BasicNameValuePair("to", recipientEmail));
        nvps.add(new BasicNameValuePair("subject", title));
        nvps.add(new BasicNameValuePair("html", message));

        httpost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        // 请求
        HttpResponse response = httpclient.execute(httpost);
        // 处理响应
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) { // 正常返回
          // 读取xml文档
          String result = EntityUtils.toString(response.getEntity());
          Logger.getLogger(SendCloudMail.class.getName())
                    .log(Level.INFO,
                            result);
        } else {
          System.err.println("error");
        }
    }
    
}
