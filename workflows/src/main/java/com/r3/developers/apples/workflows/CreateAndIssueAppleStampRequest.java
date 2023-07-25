package com.r3.developers.apples.workflows;

import net.corda.v5.base.types.MemberX500Name;

public class CreateAndIssueAppleStampRequest {

    private String stampDescription;

    private MemberX500Name holder;

    // The JSON Marshalling Service, which handles serialisation, needs this constructor.
    public CreateAndIssueAppleStampRequest() {}

    public CreateAndIssueAppleStampRequest(String stampDescription, MemberX500Name holder) {
        this.stampDescription = stampDescription;
        this.holder = holder;
    }

    public String getStampDescription() {
        return stampDescription;
    }

    public MemberX500Name getHolder() {
        return holder;
    }
}