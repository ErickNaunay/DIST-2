package com.corejsf;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;

@Named("user") // or @ManagedBean(name="user")
@SessionScoped
public class UserBean implements Serializable {

    private String name;
    private String password;
    private String status;
    private ArrayList<Message> receivedMessages = new ArrayList<>();
    private ArrayList<Message> sentMessages = new ArrayList<>();
    private ArrayList<Message> deletedMessages = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public void AddNewMessage(Message message)
    {
        receivedMessages.add(message);
    }

    public void RemoveMessage(Message message)
    {
        receivedMessages.remove(message);
    }

    public ArrayList<Message> getSentMessages() {
        return sentMessages;
    }

    public ArrayList<Message> getReceivedMessages() {
        return receivedMessages;
    }

    public ArrayList<Message> getDeletedMessages() {
        return deletedMessages;
    }

}
