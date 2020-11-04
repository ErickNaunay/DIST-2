package com.corejsf;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.push.Push;
import javax.faces.push.PushContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@Named("server")
@ApplicationScoped
public class Server {


    private ArrayList<String> onlineUsers = new ArrayList<>();
    private ArrayList<String> offlineUsers = new ArrayList<>();
    private ArrayList<Message> queuedMessages = new ArrayList<>();
    private HashMap<String, String> registeredUsers = new HashMap<>();
    private HashMap<String, String> userNotifications = new HashMap<>();


    // EXAMPLE DATA
    private ArrayList<String> exampleMessageTitles = new ArrayList<>(Arrays.asList("ASUNTO 1", "REUNION 1", "INPERICIA 1", "MEMO 2"));
    // EXAMPLE DATA

    @Inject
    private UserBean user;
    @Inject
    private MessageManager msnManager;

    @Inject
    @Push
    private PushContext incoming;

    public ArrayList<String> getExampleMessageTitles() {
        return exampleMessageTitles;
    }

    public void setExampleMessageTitles(ArrayList<String> exampleMessageTitles) {
        this.exampleMessageTitles = exampleMessageTitles;
    }

    // helper methods
    public HashMap<String, String> getRegisteredUsers() {
        return registeredUsers;
    }

    public ArrayList<String> getOnlineUsers() {
        return onlineUsers;
    }

    public ArrayList<String> getOfflineUsers() {
        return offlineUsers;
    }

    public ArrayList<String> getAllUsers() {
        return new ArrayList<>(registeredUsers.keySet());
    }

    public ArrayList<Message> getQueuedMessages() {
        return queuedMessages;
    }

    public String getUserMessageNotification() {
        return userNotifications.get(user.getName());
    }

    public boolean checkEmptyFields() {

        FacesContext context = FacesContext.getCurrentInstance();

        if (user.getName().isEmpty() || user.getPassword().isEmpty()) {

            FacesMessage check1 = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Username or Password is empty", "Type username or password");
            context.addMessage(null, check1);
            return true;

        }
        return false;
    }

    public String register() {

        if (checkEmptyFields()) {
            return null;
        }

        FacesContext context = FacesContext.getCurrentInstance();
        String name = user.getName();
        String password = user.getPassword();

        if (!registeredUsers.containsKey(name)) {

            registeredUsers.put(name, password);
            return "signup";

        } else {
            FacesMessage message1 = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Username '"
                            + name
                            + "' already exists! Please choose a different username",
                    "Please choose a different username.");
            context.addMessage(null, message1);
            return null;
        }
    }

    public String login() {

        if (checkEmptyFields()) {
            return null;
        }

        FacesContext context = FacesContext.getCurrentInstance();
        String name = user.getName();
        String password = user.getPassword();

        System.out.println("LOGGED IN: " + name);

        if (registeredUsers.containsKey(name)) {

            if (registeredUsers.get(name).equals(password)) {

                if (onlineUsers.contains(name)) {

                    FacesMessage message0 = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "A user with unther the username: '" + name + "' is already connected!",
                            "Please sign in as another user");
                    context.addMessage(null, message0);
                    return null;

                } else {
                    userNotifications.put(name, "You don't have new messages.");
                    offlineUsers.remove(name);
                    user.setStatus("online");
                    onlineUsers.add(name);
                    incoming.send("userChange");
                    return "welcome";
                }

            } else {

                FacesMessage message0 = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Incorrect password for '" + name + "'",
                        "Please type the correct password.");
                context.addMessage(null, message0);
                return null;
            }

        } else {
            FacesMessage message1 = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "User '"
                            + name
                            + "' is not registered. Please sign up",
                    "Please sign up.");
            context.addMessage(null, message1);
            return null;
        }
    }

    public String logout() {

        String name = user.getName();
        System.out.println("LOGGED OUT: " + name);
        onlineUsers.remove(name);
        user.setStatus("offline");
        offlineUsers.add(name);
        incoming.send("userChange");
        return "home";
    }

    public void checkNewMessages() {

        System.out.println("ID: " + user.getName() + ", CHECK NEW MESSAGES PUSH");
        // check new messages for all users

        for (String username : userNotifications.keySet()) {
            int newMessagesCount = 0;
            for (Message queuedMessage : queuedMessages) {
                if (queuedMessage.getReceiver().equals(username)) {
                    newMessagesCount += 1;
                }
            }

            if (newMessagesCount > 0) {
                String msnNotification = "You have " + newMessagesCount + " new message(s)";
                userNotifications.replace(username, msnNotification);
            }
        }
        incoming.send("userChange");
        //
//        for (Message queuedMessage : queuedMessages) {
//            if (queuedMessage.getReceiver().equals(user.getName())) {
//                newMessagesCount += 1;
//            }
//        }
//
//        if (newMessagesCount > 0) {
//            msnManager.setNewMessagePrompt("You have " + newMessagesCount + " new message(s)");
//            messages.send("newMessage");
//        }
    }
}