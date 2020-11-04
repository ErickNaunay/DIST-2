package com.corejsf;

import java.util.Date;

public class Message {

    private String title;
    private String message;
    private String sender;
    private String receiver;
    private Date date;
    private String status;

    public Message(String title, String message, String sender, String receiver, Date date, String status) {
        this.title = title;
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.date = date;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String displayMessage() {
        return "[" + date + "] <From: " + sender + ", To: " + receiver + "> Msg: " + message;
    }
}
