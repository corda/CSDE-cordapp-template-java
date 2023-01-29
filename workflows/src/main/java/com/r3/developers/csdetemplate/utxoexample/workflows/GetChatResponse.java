package com.r3.developers.csdetemplate.utxoexample.workflows;

public class GetChatResponse {

    private String messageFrom;
    private String message;
    public GetChatResponse() {}

    public GetChatResponse(String messageFrom, String message) {
        this.messageFrom = messageFrom;
        this.message = message;
    }

    public String getMessageFrom() {
        return messageFrom;
    }

    public String getMessage() {
        return message;
    }
}
