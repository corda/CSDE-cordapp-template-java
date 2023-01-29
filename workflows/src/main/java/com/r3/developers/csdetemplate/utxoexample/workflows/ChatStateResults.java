package com.r3.developers.csdetemplate.utxoexample.workflows;

import java.util.UUID;

public class ChatStateResults {

    private UUID id;
    private String chatName;
    private String messageFromName;
    private String message;

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

    public String getChatName() {
        return chatName;
    }

    public String getMessageFromName() {
        return messageFromName;
    }

    public String getMessage() {
        return message;
    }
}
