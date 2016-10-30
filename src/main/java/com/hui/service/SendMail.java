package com.hui.service;

import org.springframework.stereotype.*;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class SendMail {

    public void send() {
        boolean isSSL = true;
        String host = "smtp.163.com";
        int port = 465;
        String from = "jack@163.com";
        String to = "rose@163.com";
        boolean isAuth = true;
        final String username = "jack@163.com";
        final String password = "jack";

        Properties props = new Properties();
        props.put("mail.smtp.ssl.enable", isSSL);
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", isAuth);

        Session session = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("主题");
            message.setText("内容");

            Transport.send(message);
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        System.out.println("发送完毕！");
    }
}
