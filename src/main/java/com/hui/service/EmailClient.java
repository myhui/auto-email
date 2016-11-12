package com.hui.service;

import com.hui.bean.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created by hui on 2016/11/12.
 */
@Component
public class EmailClient {

    private static final Logger logger = LoggerFactory.getLogger(EmailClient.class);


    @Value("email.receive.host")
    String receive_host;
    @Value("email.receive.protocol")
    String receive_protocol;
    @Value("email.receive.username")
    String receive_username;
    @Value("email.receive.password")
    String receive_password;
    @Value("email.receive.isSSL")
    boolean receive_isSSL;
    @Value("email.receive.port")
    int receive_port;

    //send
    @Value("email.send.username")
    String send_username;
    @Value("emial.send.password")
    String send_password;
    @Value("email.send.isSSL")
    boolean send_isSSL;
    @Value("email.send.host")
    String send_host;
    @Value("emial.send.port")
    int send_port;
    @Value("email.send.from")
    String send_from;
    @Value("email.send.isAuth")
    boolean send_isAuth;



    Properties props = null;
    Session session = null;

    Store store = null;
    Folder folder = null;

    public boolean send(String to, String subject, String text) {

        if(subject == null || to == null || text == null) {
            logger.error("参数错误。");
            return false;
        }

        Properties props = new Properties();
        props.put("mail.smtp.ssl.enable", send_isSSL);
        props.put("mail.smtp.host", send_host);
        props.put("mail.smtp.port", send_port);
        props.put("mail.smtp.auth", send_isAuth);

        Session session = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(send_username, send_password);
            }
        });

        try {
            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(send_from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(text);

            Transport.send(message);
            return true;
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 收取邮件
     * @return
     * @throws MessagingException
     */
    public Message[] fetch(){

        props = new Properties();
        props.put("mail.pop3.ssl.enable", receive_isSSL);
        props.put("mail.pop3.host", receive_host);
        props.put("mail.pop3.port", receive_port);

        session = Session.getDefaultInstance(props);

        try {

            store = session.getStore(receive_protocol);
            store.connect(receive_username, receive_password);


            folder = store.getFolder("INBOX");

        /* Folder.READ_ONLY：只读权限
          *  Folder.READ_WRITE：可读可写（可以修改邮件的状态）
          *  Folder.READ_WRITE 打卡邮件箱
          */
            folder.open(Folder.READ_WRITE);


            // 由于POP3协议无法获知邮件的状态,所以getUnreadMessageCount得到的是收件箱的邮件总数
            System.out.println("未读邮件数: " + folder.getUnreadMessageCount());

            // 由于POP3协议无法获知邮件的状态,所以下面得到的结果始终都是为0
            System.out.println("删除邮件数: " + folder.getDeletedMessageCount());
            System.out.println("新邮件: " + folder.getNewMessageCount());

            // 获得收件箱中的邮件总数
            System.out.println("邮件总数: " + folder.getMessageCount());

            // 得到收件箱中的所有邮件
            return folder.getMessages();

        }catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //释放资源
            try {
                if (folder != null) {
                    folder.close(false);
                }
                if (store != null) {
                    store.close();
                }
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }

        return null;

    }

    /**
     * 解析邮件
     * @param messages 要解析的邮件列表
     */
    public List<Email> parseMessage(Message ...messages) throws MessagingException, IOException {
        if (messages == null || messages.length < 1)
            throw new MessagingException("未找到要解析的邮件!");

        List<Email> emails = new ArrayList<>();

        // 解析所有邮件
        for (int i = 0, count = messages.length; i < count; i++) {
            MimeMessage msg = (MimeMessage) messages[i];
            emails.add(parseMessage(msg));
        }
        return emails;
    }

    /**
     * 解析邮件
     * @param messages 要解析的邮件列表
     */
    public Email parseMessage(Message messages) throws MessagingException, IOException {
        if (messages == null)
            throw new MessagingException("未找到要解析的邮件!");

        MimeMessage msg = (MimeMessage) messages;
        Email email = new Email();
        logger.info("------------------解析第" + msg.getMessageNumber() + "封邮件-------------------- ");
        email.setMessageId(msg.getMessageNumber());
        logger.info("主题: " + getSubject(msg));
        email.setSubject(getSubject(msg));
        logger.info("发件人: " + getFrom(msg));
        email.setFrom(getFrom(msg));
        logger.info("收件人：" + getReceiveAddress(msg, null));
        email.setReceiveAddress(getReceiveAddress(msg, null));
        logger.info("发送时间：" + getSentDate(msg, null));
        email.setSentDate(getSentDate(msg, null));
        logger.info("是否已读：" + isSeen(msg));
        email.setSeen(isSeen(msg));
        logger.info("邮件优先级：" + getPriority(msg));
        email.setPriority(getPriority(msg));
        logger.info("是否需要回执：" + isReplySign(msg));
        email.setReplySign(isReplySign(msg));
        logger.info("邮件大小：" + msg.getSize() * 1024 + "kb");
        email.setSize(msg.getSize());
        boolean isContainerAttachment = isContainAttachment(msg);
        logger.info("是否包含附件：" + isContainerAttachment);
        email.setContainerAttachment(isContainerAttachment);
        if (isContainerAttachment) {
            saveAttachment(msg, "\\mailtmp\\"+msg.getSubject() + "_"); //保存附件
        }
        email.setAttachmentPath("");
        StringBuffer content = new StringBuffer(30);
        getMailTextContent(msg, content);
        logger.info("邮件正文：" + (content.length() > 100 ? content.substring(0,100) + "..." : content));
        email.setContent(content.toString());
        logger.info("------------------第" + msg.getMessageNumber() + "封邮件解析结束-------------------- ");

        return email;

    }



    /**
     * 获得邮件主题
     * @param msg 邮件内容
     * @return 解码后的邮件主题
     */
    public static String getSubject(MimeMessage msg) throws UnsupportedEncodingException, MessagingException {
        return MimeUtility.decodeText(msg.getSubject());
    }

    /**
     * 获得邮件发件人
     * @param msg 邮件内容
     * @return 姓名 <Email地址>
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    public static String getFrom(MimeMessage msg) throws MessagingException, UnsupportedEncodingException {
        String from = "";
        Address[] froms = msg.getFrom();
        if (froms.length < 1)
            throw new MessagingException("没有发件人!");

        InternetAddress address = (InternetAddress) froms[0];
        String person = address.getPersonal();
        if (person != null) {
            person = MimeUtility.decodeText(person) + " ";
        } else {
            person = "";
        }
        from = person + "<" + address.getAddress() + ">";

        return from;
    }

    /**
     * 根据收件人类型，获取邮件收件人、抄送和密送地址。如果收件人类型为空，则获得所有的收件人
     * <p>Message.RecipientType.TO  收件人</p>
     * <p>Message.RecipientType.CC  抄送</p>
     * <p>Message.RecipientType.BCC 密送</p>
     * @param msg 邮件内容
     * @param type 收件人类型
     * @return 收件人1 <邮件地址1>, 收件人2 <邮件地址2>, ...
     * @throws MessagingException
     */
    public static String getReceiveAddress(MimeMessage msg, Message.RecipientType type) throws MessagingException {
        StringBuffer receiveAddress = new StringBuffer();
        Address[] addresss = null;
        if (type == null) {
            addresss = msg.getAllRecipients();
        } else {
            addresss = msg.getRecipients(type);
        }

        if (addresss == null || addresss.length < 1)
            throw new MessagingException("没有收件人!");
        for (Address address : addresss) {
            InternetAddress internetAddress = (InternetAddress)address;
            receiveAddress.append(internetAddress.toUnicodeString()).append(",");
        }

        receiveAddress.deleteCharAt(receiveAddress.length()-1); //删除最后一个逗号

        return receiveAddress.toString();
    }

    /**
     * 获得邮件发送时间
     * @param msg 邮件内容
     * @return yyyy年mm月dd日 星期X HH:mm
     * @throws MessagingException
     */
    public static String getSentDate(MimeMessage msg, String pattern) throws MessagingException {
        Date receivedDate = msg.getSentDate();
        if (receivedDate == null)
            return "";

        if (pattern == null || "".equals(pattern))
            pattern = "yyyy年MM月dd日 E HH:mm ";

        return new SimpleDateFormat(pattern).format(receivedDate);
    }

    /**
     * 判断邮件中是否包含附件
     *  msg 邮件内容
     * @return 邮件中存在附件返回true，不存在返回false
     * @throws MessagingException
     * @throws IOException
     */
    public static boolean isContainAttachment(Part part) throws MessagingException, IOException {
        boolean flag = false;
        if (part.isMimeType("multipart/*")) {
            MimeMultipart multipart = (MimeMultipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                String disp = bodyPart.getDisposition();
                if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {
                    flag = true;
                } else if (bodyPart.isMimeType("multipart/*")) {
                    flag = isContainAttachment(bodyPart);
                } else {
                    String contentType = bodyPart.getContentType();
                    if (contentType.indexOf("application") != -1) {
                        flag = true;
                    }

                    if (contentType.indexOf("name") != -1) {
                        flag = true;
                    }
                }

                if (flag) break;
            }
        } else if (part.isMimeType("message/rfc822")) {
            flag = isContainAttachment((Part)part.getContent());
        }
        return flag;
    }

    /**
     * 判断邮件是否已读
     * @param msg 邮件内容
     * @return 如果邮件已读返回true,否则返回false
     * @throws MessagingException
     */
    public static boolean isSeen(MimeMessage msg) throws MessagingException {
        return msg.getFlags().contains(Flags.Flag.SEEN);
    }

    /**
     * 判断邮件是否需要阅读回执
     * @param msg 邮件内容
     * @return 需要回执返回true,否则返回false
     * @throws MessagingException
     */
    public static boolean isReplySign(MimeMessage msg) throws MessagingException {
        boolean replySign = false;
        String[] headers = msg.getHeader("Disposition-Notification-To");
        if (headers != null)
            replySign = true;
        return replySign;
    }

    /**
     * 获得邮件的优先级
     * @param msg 邮件内容
     * @return 1(High):紧急  3:普通(Normal)  5:低(Low)
     * @throws MessagingException
     */
    public static String getPriority(MimeMessage msg) throws MessagingException {
        String priority = "普通";
        String[] headers = msg.getHeader("X-Priority");
        if (headers != null) {
            String headerPriority = headers[0];
            if (headerPriority.indexOf("1") != -1 || headerPriority.indexOf("High") != -1)
                priority = "紧急";
            else if (headerPriority.indexOf("5") != -1 || headerPriority.indexOf("Low") != -1)
                priority = "低";
            else
                priority = "普通";
        }
        return priority;
    }

    /**
     * 获得邮件文本内容
     * @param part 邮件体
     * @param content 存储邮件文本内容的字符串
     * @throws MessagingException
     * @throws IOException
     */
    public static void getMailTextContent(Part part, StringBuffer content) throws MessagingException, IOException {
        //如果是文本类型的附件，通过getContent方法可以取到文本内容，但这不是我们需要的结果，所以在这里要做判断
        boolean isContainTextAttach = part.getContentType().indexOf("name") > 0;
        if (part.isMimeType("text/*") && !isContainTextAttach) {
            content.append(part.getContent().toString());
        } else if (part.isMimeType("message/rfc822")) {
            getMailTextContent((Part)part.getContent(),content);
        } else if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                getMailTextContent(bodyPart,content);
            }
        }
    }

    /**
     * 保存附件
     * @param part 邮件中多个组合体中的其中一个组合体
     * @param destDir  附件保存目录
     * @throws UnsupportedEncodingException
     * @throws MessagingException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void saveAttachment(Part part, String destDir) throws UnsupportedEncodingException, MessagingException,
            FileNotFoundException, IOException {
        if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();    //复杂体邮件
            //复杂体邮件包含多个邮件体
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                //获得复杂体邮件中其中一个邮件体
                BodyPart bodyPart = multipart.getBodyPart(i);
                //某一个邮件体也有可能是由多个邮件体组成的复杂体
                String disp = bodyPart.getDisposition();
                if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {
                    InputStream is = bodyPart.getInputStream();
                    saveFile(is, destDir, decodeText(bodyPart.getFileName()));
                } else if (bodyPart.isMimeType("multipart/*")) {
                    saveAttachment(bodyPart,destDir);
                } else {
                    String contentType = bodyPart.getContentType();
                    if (contentType.indexOf("name") != -1 || contentType.indexOf("application") != -1) {
                        saveFile(bodyPart.getInputStream(), destDir, decodeText(bodyPart.getFileName()));
                    }
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            saveAttachment((Part) part.getContent(),destDir);
        }
    }

    /**
     * 读取输入流中的数据保存至指定目录
     * @param is 输入流
     * @param fileName 文件名
     * @param destDir 文件存储目录
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static void saveFile(InputStream is, String destDir, String fileName)
            throws FileNotFoundException, IOException {

        BufferedInputStream bis = new BufferedInputStream(is);
        BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(new File(destDir + fileName)));
        int len = -1;
        while ((len = bis.read()) != -1) {
            bos.write(len);
            bos.flush();
        }
        bos.close();
        bis.close();
    }

    /**
     * 文本解码
     * @param encodeText 解码MimeUtility.encodeText(String text)方法编码后的文本
     * @return 解码后的文本
     * @throws UnsupportedEncodingException
     */
    public static String decodeText(String encodeText) throws UnsupportedEncodingException {
        if (encodeText == null || "".equals(encodeText)) {
            return "";
        } else {
            return MimeUtility.decodeText(encodeText);
        }
    }

}
