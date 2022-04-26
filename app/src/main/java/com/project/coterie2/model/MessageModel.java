package com.project.coterie2.model;

public class MessageModel {
    private String senderID, senderName, message;

    public MessageModel(String senderID, String senderName, String message) {
        this.senderID = senderID;
        this.senderName = senderName;
        this.message = message;
    }

    public MessageModel(){

    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
