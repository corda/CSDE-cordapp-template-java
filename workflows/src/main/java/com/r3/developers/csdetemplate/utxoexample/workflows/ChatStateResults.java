package com.r3.developers.csdetemplate.utxoexample.workflows;

import java.util.UUID;

public class ChatStateResults {
    public ChatStateResults() {}
    public ChatStateResults(UUID id, String chatName, String messageFromName, String message) {
        this.id = id;
        this.chatName = chatName;
        this.messageFromName = messageFromName;
        this.message = message;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public String getMessageFromName() {
        return messageFromName;
    }

    public void setMessageFromName(String messageFromName) {
        this.messageFromName = messageFromName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private UUID id;
    private String chatName;
    private String messageFromName;
    private String message;

}
