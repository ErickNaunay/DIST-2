package com.corejsf;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
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
    private String selectedMessage = "";
    private String displayMessage = "";
    @Inject
    private UserBean user;
    @Inject
    private Server server;

    public String getDisplayMessage() {
        return displayMessage;
    }

    public String getSelectedMessage() {
        return selectedMessage;
    }

    public void setSelectedMessage(String selectedMessage) {
        this.selectedMessage = selectedMessage;
    }

    public void setSelectedMessage(ValueChangeEvent e) {
        selectedMessage = (String) e.getNewValue();
        for (Message message : user.getReceivedMessages()) {
            if (message.getTitle().equals(selectedMessage)) {
                displayMessage = "From: " + message.getSender() + " on " + message.getDate() + "\n" + "Title: " + message.getTitle() + "\n" + message.getMessage();
                System.out.println("SET " + selectedMessage);
            }
        }
    }

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

    public void selectAllRecipients() {
        System.out.println("value changed to: " + sendToAll);

        if (sendToAll) {
            recipients.addAll(server.getRegisteredUsers().keySet());
        } else {
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

    public void getMessagesFromServer() {
        ArrayList<Message> messagesOfServer = new ArrayList<>(server.getQueuedMessages());
        for (Message message : messagesOfServer) {
            if (message.getReceiver().equals(user.getName())) {
                user.AddNewMessage(message);
                server.RemoveMessageFromQueue(message);
            }
        }
        server.setUserMessageNotification("You don't have new messages");
    }

    public ArrayList<String> getUserMessageTitles() {
        getMessagesFromServer();
        ArrayList<String> userMessageTitle = new ArrayList<>();
        for (Message msg : user.getReceivedMessages()) {
            userMessageTitle.add(msg.getTitle());
        }
        return userMessageTitle;
    }

    public void deleteReceivedMessage() {
        if (!user.getReceivedMessages().isEmpty()) {
            ArrayList<Message> userMessages = new ArrayList<>(user.getReceivedMessages());
            for (Message message : userMessages) {
                if (message.getTitle().equals(selectedMessage)) {
                    user.RemoveMessage(message);
                    break;
                }
            }
            selectedMessage = "";
            displayMessage = "";
        } else {
            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage message1 = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "There are no messages selected to be removed",
                    "Please be sure you are selecting a message to delete");
            context.addMessage(null, message1);
        }

    }

}
