package com.r3.developers.csdetemplate.flowexample.workflows;

import net.corda.v5.application.flows.*;
import net.corda.v5.application.marshalling.JsonMarshallingService;
import net.corda.v5.application.membership.MemberLookup;
import net.corda.v5.application.messaging.FlowMessaging;
import net.corda.v5.application.messaging.FlowSession;
import net.corda.v5.base.annotations.Suspendable;
import net.corda.v5.base.types.MemberX500Name;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// MyFirstFlow is an initiating flow, it's corresponding responder flow is called MyFirstFlowResponder (defined below)
// to link the two sides of the flow together they need to have the same protocol.
@InitiatingFlow(protocol = "my-first-flow")
// MyFirstFlow should inherit from RPCStartableFlow, which tells Corda it can be started via an RPC call
public class MyFirstFlow implements RPCStartableFlow {

    // Log messages from the flows for debugging.
    private final Logger log = LoggerFactory.getLogger(MyFirstFlow.class);

    // Corda has a set of injectable services which are injected into the flow at runtime.
    // Flows declare them with @CordaInjectable, then the flows have access to their services.

    // JsonMarshallingService provides a service for manipulating JSON.
    @CordaInject
    public JsonMarshallingService jsonMarshallingService;

    // FlowMessaging provides a service that establishes flow sessions between virtual nodes 
    // that send and receive payloads between them.
    @CordaInject
    public FlowMessaging flowMessaging;

     // MemberLookup provides a service for looking up information about members of the virtual network which
     // this CorDapp operates in.
    @CordaInject
    public MemberLookup memberLookup;

    public MyFirstFlow() {}

    // When a flow is invoked its call() method is called.
    // Call() methods must be marked as @Suspendable, this allows Corda to pause mid-execution to wait
    // for a response from the other flows and services.
    @NotNull
    @Suspendable
    @Override
    public String call(RPCRequestData requestBody) {

        // Follow what happens in the console or logs.
        log.info("MFF: MyFirstFlow.call() called");

        // Show the requestBody in the logs - this can be used to help establish the format for starting a flow on Corda.
        log.info("MFF: requestBody: " + requestBody.getRequestBody());

        // Deserialize the Json requestBody into the MyfirstFlowStartArgs class using the JsonSerialisation service.
        MyFirstFlowStartArgs flowArgs = requestBody.getRequestBodyAs(jsonMarshallingService, MyFirstFlowStartArgs.class);

        // Obtain the MemberX500Name of the counterparty.
        MemberX500Name otherMember = flowArgs.otherMember;

        // Get our identity from the MemberLookup service.
        MemberX500Name ourIdentity = memberLookup.myInfo().getName();

        // Create the message payload using the MessageClass we defined.
        Message message = new Message(otherMember, "Hello from " + ourIdentity + ".");

        // Log the message to be sent.
        log.info("MFF: message.message: " + message.message);

        // Start a flow session with the otherMember using the FlowMessaging service.
        // The otherMember's virtual node will run the corresponding MyFirstFlowResponder responder flow.
        FlowSession session = flowMessaging.initiateFlow(otherMember);

        // Send the Payload using the send method on the session to the MyFirstFlowResponder responder flow.
        session.send(message);

        // Receive a response from the responder flow.
        Message response = session.receive(Message.class);

        // The return value of a RPCStartableFlow must always be a String. This will be passed
        // back as the REST RPC response when the status of the flow is queried on Corda, or as the return
        // value from the flow when testing using the simulator.
        return response.message;
    }
}


/*
RequestBody for triggering the flow via http-rpc:
{
    "clientRequestId": "r1",
    "flowClassName": "com.r3.developers.csdetemplate.workflows.MyFirstFlow",
    "requestData": {
        "otherMember":"CN=Bob, OU=Test Dept, O=R3, L=London, C=GB"
        }
}
 */
