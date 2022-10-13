package com.r3.developers.csdetemplate;

import net.corda.v5.base.annotations.CordaSerializable;
import net.corda.v5.base.types.MemberX500Name;

// // A class which will contain a message, It must be marked with @CordaSerializable for Corda
//// to be able to send from one virtual node to another.
@CordaSerializable
public class Message {
    // public Message() {}
    public Message(MemberX500Name sender, String message) {
        this.sender = sender;
        this.message = message;
    }
    public MemberX500Name getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public MemberX500Name sender;
    public String message;
}
