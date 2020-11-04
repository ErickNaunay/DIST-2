package com.corejsf;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

@Named("msm")
@SessionScoped
public class MessageManager implements Serializable {

    private String messageTitle = "";
    private String messageToSend = "";
    private ArrayList<String> recipients = new ArrayList<>();
    private Boolean sendToAll = false;

    @Inject
    private UserBean user;
    @Inject
    private Server server;

    public String getMessageTitle() {
        return messageTitle;
    }

    public void setMessageTitle(String messageTitle) {
        this.messageTitle = messageTitle;
    }

    public String getMessageToSend() {
        return messageToSend;
    }

    public void setMessageToSend(String messageToSend) {
        this.messageToSend = messageToSend;
    }

    public ArrayList<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(ArrayList<String> recipients) {
        this.recipients = recipients;
    }

    public Boolean getSendToAll() {
        return sendToAll;
    }

    public void setSendToAll(Boolean sendToAll) {
        this.sendToAll = sendToAll;
    }

    public void selectAllRecipients(){
        System.out.println("value changed to: " + sendToAll);

        if(sendToAll){
            recipients.addAll(server.getRegisteredUsers().keySet());
        }
        else{
            recipients.clear();
        }

    }

    public String sendMessage() {

        System.out.println("sending all the messages for: " + user.getName());

        if (!messageToSend.isEmpty() || !messageTitle.isEmpty()) {

            for (String recipient : recipients) {
                Message msg = new Message(messageTitle, messageToSend, user.getName(), recipient, new Date(), "new");

                System.out.println("Message Title: " + messageTitle);
                System.out.println("From: " + user.getName() + " TO: " + recipient);
                System.out.println(messageToSend);

                server.getQueuedMessages().add(msg);
            }

            System.out.println("MESSAGES IN SERVER QUEUE: " + server.getQueuedMessages().size());

        } else {
            FacesContext context = FacesContext.getCurrentInstance();

            FacesMessage infoMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Message or Title box is empty",
                    "Please check these fields.");
            context.addMessage(null, infoMessage);
        }

        recipients.clear();
        sendToAll = false;
        messageTitle = "";
        messageToSend = "";
        server.checkNewMessages();
        return null;
    }

}
