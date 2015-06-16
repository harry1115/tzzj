/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.comgroup.tzmedia.server.util.mail;

import cn.comgroup.tzmedia.server.util.property.PropertiesUtils;
import com.sun.mail.smtp.SMTPTransport;
import java.io.File;
import java.security.Security;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletContext;

/**
 * TZMediaMail
 *
 * @author pcnsh197
 */
public class TZMediaMail {

    private TZMediaMail() {
    }

    /**
     * Send email using SMTP server.
     *
     * @param recipientEmail TO recipient
     * @param title title of the message
     * @param message message to be sent
     * @param fileList
     * @param context
     * @throws AddressException if the email address parse failed
     * @throws MessagingException if the connection is dead or not in the
     * connected state or if the message is not a MimeMessage
     */
    public static void send(String recipientEmail,
            String title, String message,List<File> fileList,ServletContext context)
            throws AddressException, MessagingException {
        //Use normal smtp to send the mail
        TZMediaMail.send(recipientEmail, "", title, message,fileList, context);        
//        TZMediaMail.sendSSL(recipientEmail, "", title, message,fileList, context);
    }

    /**
     * Send email using SMTP server.
     *
     * @param recipientEmail TO recipient
     * @param ccEmail CC recipient. Can be empty if there is no CC recipient
     * @param title title of the message
     * @param message message to be sent
     * @param fileList
     * @param context
     * @throws AddressException if the email address parse failed
     * @throws MessagingException if the connection is dead or not in the
     * connected state or if the message is not a MimeMessage
     */
     public static void send(String recipientEmail,
            String ccEmail, String title, String message,List<File> fileList, ServletContext context
    ) throws AddressException, MessagingException {
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        Properties tzMediaProperties = PropertiesUtils.getProperties(context);
        // Get a Properties object
        Properties props = System.getProperties();
        props.setProperty("mail.smtp.host", tzMediaProperties.getProperty("mail.smtp.host"));
        props.setProperty("mail.smtp.auth", tzMediaProperties.getProperty("mail.smtp.auth"));
        Session session = Session.getInstance(props, null);

        // -- Create a new message --
        final MimeMessage msg = new MimeMessage(session);

        // -- Set the FROM and TO fields --
        msg.setFrom(new InternetAddress(tzMediaProperties.getProperty("mail.smtp.default.from")));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail, false));

        if (ccEmail.length() > 0) {
            msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccEmail, false));
        }

        msg.setSubject(title);
        Multipart mm = new MimeMultipart();
        // Mail Body
        BodyPart mailText = new MimeBodyPart();
        mailText.setText(message);
        mm.addBodyPart(mailText);
        // Attachment
        if (fileList != null && fileList.size() > 0) {
            for (File file : fileList) {
                BodyPart mailAttachment = new MimeBodyPart();
                FileDataSource fds = new FileDataSource(file);
                DataHandler handler = new DataHandler(fds);
                mailAttachment.setDataHandler(handler);
                mailAttachment.setFileName(file.getName());
                mm.addBodyPart(mailAttachment);
            }
        }

        msg.setContent(mm);
        msg.setSentDate(new Date());

        SMTPTransport t = (SMTPTransport) session.getTransport("smtp");
        t.connect(tzMediaProperties.getProperty("mail.smtp.host"),
                tzMediaProperties.getProperty("mail.smtp.default.from"),
                tzMediaProperties.getProperty("mail.smtp.default.from.pass"));
        t.sendMessage(msg, msg.getAllRecipients());
        t.close();
    }
    
    
    
     /**
     * Send email using SMTPS server.
     *
     * @param recipientEmail TO recipient
     * @param ccEmail CC recipient. Can be empty if there is no CC recipient
     * @param title title of the message
     * @param message message to be sent
     * @param fileList
     * @param context
     * @throws AddressException if the email address parse failed
     * @throws MessagingException if the connection is dead or not in the
     * connected state or if the message is not a MimeMessage
     */
    public static void sendSSL(String recipientEmail,
            String ccEmail, String title, String message,List<File> fileList, ServletContext context
    ) throws AddressException, MessagingException {
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
        Properties tzMediaProperties = PropertiesUtils.getProperties(context);
        // Get a Properties object
        Properties props = System.getProperties();
        props.setProperty("mail.smtps.host", tzMediaProperties.getProperty("mail.smtps.host"));
        props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        props.setProperty("mail.smtps.auth", tzMediaProperties.getProperty("mail.smtps.auth"));

        /*
         If set to false, the QUIT command is sent and the connection is immediately closed. If set 
         to true (the default), causes the transport to wait for the response to the QUIT command.

         ref :   http://java.sun.com/products/javamail/javadocs/com/sun/mail/smtp/package-summary.html
         http://forum.java.sun.com/thread.jspa?threadID=5205249
         smtpsend.java - demo program from javamail
         */
        props.put("mail.smtp.quitwait", "false");
        Session session = Session.getInstance(props, null);
//        session.setDebug(true);

        // -- Create a new message --
        final MimeMessage msg = new MimeMessage(session);

        // -- Set the FROM and TO fields --
        msg.setFrom(new InternetAddress(tzMediaProperties.getProperty("mail.smtps.default.from")));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail, false));

        if (ccEmail.length() > 0) {
            msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccEmail, false));
        }

        msg.setSubject(title);
//        msg.setText(message, "utf-8");
        
        Multipart mm = new MimeMultipart();
        // Mail Body
        BodyPart mailText = new MimeBodyPart();
        mailText.setText(message);
        mm.addBodyPart(mailText);
        // Attachment
        if (fileList != null && fileList.size() > 0) {
            for (File file : fileList) {
                BodyPart mailAttachment = new MimeBodyPart();
                FileDataSource fds = new FileDataSource(file);
                DataHandler handler = new DataHandler(fds);
                mailAttachment.setDataHandler(handler);
                mailAttachment.setFileName(file.getName());
                mm.addBodyPart(mailAttachment);
            }
        }

        msg.setContent(mm);
        msg.setSentDate(new Date());

        SMTPTransport t = (SMTPTransport) session.getTransport("smtps");
        t.connect(tzMediaProperties.getProperty("mail.smtps.host"),
                tzMediaProperties.getProperty("mail.smtps.default.from"),
                tzMediaProperties.getProperty("mail.smtps.default.from.pass"));
        t.sendMessage(msg, msg.getAllRecipients());
        t.close();
    }
}
