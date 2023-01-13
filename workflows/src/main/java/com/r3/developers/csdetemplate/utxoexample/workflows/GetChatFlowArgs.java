package com.r3.developers.csdetemplate.utxoexample.workflows;

import java.util.UUID;

public class GetChatFlowArgs {
    public GetChatFlowArgs() {}

    public GetChatFlowArgs(UUID id, int numberOfRecords ) {
        this.id = id;
        this.numberOfRecords = numberOfRecords;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }


    public int getNumberOfRecords() {
        return numberOfRecords;
    }

    public void setNumberOfRecords(int numberOfRecords) {
        this.numberOfRecords = numberOfRecords;
    }

    private UUID id;
    private int numberOfRecords;
}
