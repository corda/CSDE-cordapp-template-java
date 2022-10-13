package com.r3.developers.csdetemplate;

import net.corda.v5.base.types.MemberX500Name;

// // A class to hold the arguments required to start the flow
//class MyFirstFlowStartArgs(val otherMember: MemberX500Name)
public class MyFirstFlowStartArgs {
    public MemberX500Name otherMember;

    public MemberX500Name getOtherMember() {
        return otherMember;
    }

    public MyFirstFlowStartArgs(MemberX500Name otherMember) {
        this.otherMember = otherMember;
    }

    // Without the following we get
    // "Cannot construct instance of `com.r3.developers.csdetemplate.MyFirstFlowStartArgs` (no Creators, like default constructor, exist): cannot deserialize from Object value (no delegate- or property-based Creator)\n at [Source: (String)\"{\"otherMember\":\"CN=Bob, OU=Test Dept, O=R3, L=London, C=GB\"}\"; line: 1, column: 2]"
    public MyFirstFlowStartArgs() {}
}
