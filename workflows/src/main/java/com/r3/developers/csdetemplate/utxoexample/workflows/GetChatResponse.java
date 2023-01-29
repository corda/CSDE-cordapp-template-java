package com.r3.developers.csdetemplate.utxoexample.workflows;

import net.corda.v5.base.annotations.ConstructorForDeserialization;
import net.corda.v5.base.annotations.CordaSerializable;

//@CordaSerializable
public class GetChatResponse {

    private String messageFrom;
    private String message;
    public GetChatResponse() {}

//    @ConstructorForDeserialization
    public GetChatResponse(String messageFrom, String message) {
        this.messageFrom = messageFrom;
        this.message = message;
    }

    public String getMessageFrom() {
        return messageFrom;
    }

//    public void setMessageFrom(String messageFrom) {
//        this.messageFrom = messageFrom;
//    }

    public String getMessage() {
        return message;
    }

//    public void setMessage(String message) {
//        this.message = message;
//    }


}
