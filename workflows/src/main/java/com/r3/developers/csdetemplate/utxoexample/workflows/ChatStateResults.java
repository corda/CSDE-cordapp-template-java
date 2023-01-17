package com.r3.developers.csdetemplate.utxoexample.workflows;

import net.corda.v5.base.annotations.ConstructorForDeserialization;
import net.corda.v5.base.annotations.CordaSerializable;

import java.util.UUID;

@CordaSerializable
public class ChatStateResults {
    public ChatStateResults() {}

    @ConstructorForDeserialization
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

    public UUID id;
    public String chatName;
    public String messageFromName;
    public String message;

}
