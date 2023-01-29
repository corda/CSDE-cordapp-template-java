package com.r3.developers.csdetemplate.utxoexample.workflows;

import java.util.UUID;

public class UpdateChatFlowArgs {
    public UpdateChatFlowArgs() {}

    private UUID id;
    private String message;

    public UpdateChatFlowArgs(UUID id, String message) {
        this.id = id;
        this.message = message;
    }

    public UUID getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }
}
