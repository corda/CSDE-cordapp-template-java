package com.r3.developers.csdetemplate.utxoexample.workflows;

import net.corda.v5.base.annotations.ConstructorForDeserialization;
import net.corda.v5.base.annotations.CordaSerializable;

//@CordaSerializable - we're not sending it down the wire so we don't need this.
public class CreateNewChatFlowArgs{

    // Serialisation service requires a default constructor
    public CreateNewChatFlowArgs() {}

    private String chatName;
    private String message;
    private String otherMember;


//    @ConstructorForDeserialization  // don't appear to need this
    public CreateNewChatFlowArgs(String chatName, String message, String otherMember) {
        this.chatName = chatName;
        this.message = message;
        this.otherMember = otherMember;
    }

    public String getChatName() {
        return chatName;
    }

//    public void setChatName(String chatName) {
//        this.chatName = chatName;
//    }

    public String getMessage() {
        return message;
    }

//    public void setMessage(String message) {
//        this.message = message;
//    }

    public String getOtherMember() {
        return otherMember;
    }

//    public void setOtherMember(String otherMember) {
//        this.otherMember = otherMember;
//    }


}