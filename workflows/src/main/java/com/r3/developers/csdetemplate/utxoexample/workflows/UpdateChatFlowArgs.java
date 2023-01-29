package com.r3.developers.csdetemplate.utxoexample.workflows;

import net.corda.v5.base.annotations.ConstructorForDeserialization;
import net.corda.v5.base.annotations.CordaSerializable;

import java.util.UUID;
//@CordaSerializable
public class UpdateChatFlowArgs {
    public UpdateChatFlowArgs() {}

    private UUID id;
    private String message;

//    @ConstructorForDeserialization
    public UpdateChatFlowArgs(UUID id, String message) {
        this.id = id;
        this.message = message;
    }

    public UUID getId() {
        return id;
    }

//    public void setId(UUID id) {
//        this.id = id;
//    }

    public String getMessage() {
        return message;
    }

//    public void setMessage(String message) {
//        this.message = message;
//    }



}
