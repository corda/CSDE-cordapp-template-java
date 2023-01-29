package com.r3.developers.csdetemplate.utxoexample.workflows;

import net.corda.v5.base.annotations.ConstructorForDeserialization;
import net.corda.v5.base.annotations.CordaSerializable;

import java.util.UUID;

//@CordaSerializable
public class GetChatFlowArgs {

    private UUID id;
    private int numberOfRecords;
    public GetChatFlowArgs() {}

//    @ConstructorForDeserialization
    public GetChatFlowArgs(UUID id, int numberOfRecords ) {
        this.id = id;
        this.numberOfRecords = numberOfRecords;
    }

    public UUID getId() {
        return id;
    }

//    public void setId(UUID id) {
//        this.id = id;
//    }


    public int getNumberOfRecords() {
        return numberOfRecords;
    }

//    public void setNumberOfRecords(int numberOfRecords) {
//        this.numberOfRecords = numberOfRecords;
//    }


}
