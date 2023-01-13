package com.r3.developers.csdetemplate.utxoexample.workflows;

public class GetChatResponse {
    public GetChatResponse() {}
    public GetChatResponse(String messageFrom, String message) {
        this.messageFrom = messageFrom;
        this.message = message;
    }

    public String getMessageFrom() {
        return messageFrom;
    }

    public void setMessageFrom(String messageFrom) {
        this.messageFrom = messageFrom;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String messageFrom;
    private String message;
}
