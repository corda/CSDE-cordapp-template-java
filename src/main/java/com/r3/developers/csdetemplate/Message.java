package com.r3.developers.csdetemplate;

import net.corda.v5.base.annotations.CordaSerializable;
import net.corda.v5.base.types.MemberX500Name;

// Where a class contains a message, mark it with @CordaSerializable to enable Corda to 
// send it from one virtual node to another.
@CordaSerializable
public class Message {
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
