package com.r3.developers.csdetemplate.utxoexample.workflows;

import java.util.UUID;

public class GetChatFlowArgs {

    private UUID id;
    private int numberOfRecords;
    public GetChatFlowArgs() {}

    public GetChatFlowArgs(UUID id, int numberOfRecords ) {
        this.id = id;
        this.numberOfRecords = numberOfRecords;
    }

    public UUID getId() {
        return id;
    }

    public int getNumberOfRecords() {
        return numberOfRecords;
    }
}
