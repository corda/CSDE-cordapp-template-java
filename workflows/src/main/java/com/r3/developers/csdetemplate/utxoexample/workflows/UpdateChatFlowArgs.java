package com.r3.developers.csdetemplate.utxoexample.workflows;

import java.util.UUID;

public class UpdateChatFlowArgs {
    public UpdateChatFlowArgs() {}

    public UpdateChatFlowArgs(UUID id, String message) {
        this.id = id;
        this.message = message;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private UUID id;
    private String message;

}
