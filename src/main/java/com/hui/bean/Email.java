package com.hui.bean;

/**
 * Created by hui on 2016/11/12.
 */
public class Email {

//    logger.info("------------------解析第" + msg.getMessageNumber() + "封邮件-------------------- ");
//    logger.info("主题: " + getSubject(msg));
//    logger.info("发件人: " + getFrom(msg));
//    logger.info("收件人：" + getReceiveAddress(msg, null));
//    logger.info("发送时间：" + getSentDate(msg, null));
//    logger.info("是否已读：" + isSeen(msg));
//    logger.info("邮件优先级：" + getPriority(msg));
//    logger.info("是否需要回执：" + isReplySign(msg));
//    logger.info("邮件大小：" + msg.getSize() * 1024 + "kb");
//    boolean isContainerAttachment = isContainAttachment(msg);
//    logger.info("是否包含附件：" + isContainerAttachment);

    long messageId;
    String subject;
    String from;
    String receiveAddress;
    String sentDate;
    boolean isSeen;
    String priority;
    boolean isReplySign;
    long size;
    boolean isContainerAttachment;
    String attachmentPath;
    String content;


    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getReceiveAddress() {
        return receiveAddress;
    }

    public void setReceiveAddress(String receiveAddress) {
        this.receiveAddress = receiveAddress;
    }

    public String getSentDate() {
        return sentDate;
    }

    public void setSentDate(String sentDate) {
        this.sentDate = sentDate;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public boolean isReplySign() {
        return isReplySign;
    }

    public void setReplySign(boolean replySign) {
        isReplySign = replySign;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isContainerAttachment() {
        return isContainerAttachment;
    }

    public void setContainerAttachment(boolean containerAttachment) {
        isContainerAttachment = containerAttachment;
    }

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
