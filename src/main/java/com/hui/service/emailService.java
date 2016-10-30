package com.hui.service;

import org.apache.commons.mail.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by hui on 2016/10/30.
 */
@Service
public class emailService {

    public void send(){

        boolean isSSL = true;
        String host = "smtp.163.com";
        int port = 465;
        String from = "jack@163.com";
        String to = "rose@163.com";
        String username = "jack@163.com";
        String password = "jack";

        try {
            Email email = new SimpleEmail();
            email.setSSLOnConnect(isSSL);
            email.setHostName(host);
            email.setSmtpPort(port);
            email.setAuthentication(username, password);
            email.setFrom(from);
            email.addTo(to);
            email.setSubject("主题");
            email.setMsg("内容");
            email.send();
        } catch (EmailException e) {
            e.printStackTrace();
        }

        System.out.println("发送完毕！");
    }

    /**
     * send  with attachment
     * @param file
     */
    public void send(File file){

        try {
            // Create the attachment
            EmailAttachment attachment = new EmailAttachment();
            attachment.setPath("mypictures/john.jpg");
            attachment.setDisposition(EmailAttachment.ATTACHMENT);
            attachment.setDescription("Picture of John");
            attachment.setName("John");

            // Create the email message
            MultiPartEmail email = new MultiPartEmail();
            email.setHostName("mail.myserver.com");
            email.addTo("jdoe@somewhere.org", "John Doe");
            email.setFrom("me@apache.org", "Me");
            email.setSubject("The picture");
            email.setMsg("Here is the picture you wanted");

            // add the attachment
            email.attach(attachment);

            // send the email
            email.send();

        } catch (EmailException e) {
            e.printStackTrace();
        }

    }

    public void sendWithUrl(String url){
        try {
            // Create the attachment
            EmailAttachment attachment = new EmailAttachment();
            attachment.setURL(new URL("http://www.apache.org/images/asf_logo_wide.gif"));
            attachment.setDisposition(EmailAttachment.ATTACHMENT);
            attachment.setDescription("Apache logo");
            attachment.setName("Apache logo");

            // Create the email message
            MultiPartEmail email = new MultiPartEmail();
            email.setHostName("mail.myserver.com");
            email.addTo("jdoe@somewhere.org", "John Doe");
            email.setFrom("me@apache.org", "Me");
            email.setSubject("The logo");
            email.setMsg("Here is Apache's logo");

            // add the attachment
            email.attach(attachment);

            // send the email
            email.send();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (EmailException e) {
            e.printStackTrace();
        }
    }

    public void sendWithHtml(String html){
        try {
            // Create the email message
            HtmlEmail email = new HtmlEmail();
            email.setHostName("mail.myserver.com");
            email.addTo("jdoe@somewhere.org", "John Doe");
            email.setFrom("me@apache.org", "Me");
            email.setSubject("Test email with inline image");

            // embed the image and get the content id
            URL url = new URL("http://www.apache.org/images/asf_logo_wide.gif");
            String cid = email.embed(url, "Apache logo");

            // set the html message
            email.setHtmlMsg("<html>The apache logo - <img src=\"cid:"+cid+"\"></html>");

            // set the alternative message
            email.setTextMsg("Your email client does not support HTML messages");

            // send the email
            email.send();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (EmailException e) {
            e.printStackTrace();
        }
    }


}
