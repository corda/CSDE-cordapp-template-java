package com.r3.developers.utxodemo.workflows;

import com.r3.developers.csdetemplate.workflows.MyFirstFlow;
import com.r3.developers.utxodemo.contracts.TestCommand;
import com.r3.developers.utxodemo.states.TestUtxoState;
import net.corda.v5.application.flows.CordaInject;
import net.corda.v5.application.flows.InitiatedBy;
import net.corda.v5.application.flows.InitiatingFlow;
import net.corda.v5.application.flows.RPCRequestData;
import net.corda.v5.application.flows.RPCStartableFlow;
import net.corda.v5.application.flows.ResponderFlow;
import net.corda.v5.application.flows.getRequestBodyAs;
import net.corda.v5.application.marshalling.JsonMarshallingService;
import net.corda.v5.application.membership.MemberLookup;
import net.corda.v5.application.messaging.FlowMessaging;
import net.corda.v5.application.messaging.FlowSession;
import net.corda.v5.base.annotations.Suspendable;
import net.corda.v5.base.types.MemberX500Name;
import net.corda.v5.base.util.contextLogger;
import net.corda.v5.base.util.days;
import net.corda.v5.ledger.common.NotaryLookup;
import net.corda.v5.ledger.common.Party;
import net.corda.v5.ledger.utxo.UtxoLedgerService;
import net.corda.v5.membership.MemberInfo;
import net.corda.v5.membership.NotaryInfo;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.*;

/**
 * Example utxo flow.
 * TODO expand description
 */

/*
{
    "clientRequestId": "r1",
    "flowClassName": "com.r3.developers.utxodemo.workflows.UtxoDemoFlow",
    "requestData": {
        "input" : "Inputty McInputty-Face",
        "members" : [
            "CN=Alice, OU=Test Dept, O=R3, L=London, C=GB",
            "CN=Bob, OU=Test Dept, O=R3, L=London, C=GB",
            "CN=Charlie, OU=Test Dept, O=R3, L=London, C=GB"
        ],
        "notary" : "CN=NotaryRep1, OU=Test Dept, O=R3, L=London, C=GB"
    }
}
 */

@InitiatingFlow("utxo-flow-protocol")
public class UtxoDemoFlow implements RPCStartableFlow {
    public static class InputMessage {
        String input;
        List<String> members;
        String notary;
    }

// Log messages from the flows for debugging.
private final Logger log = LoggerFactory.getLogger(MyFirstFlow.class);

@CordaInject
public FlowMessaging flowMessaging;

@CordaInject
public UtxoLedgerService utxoLedgerService;

@CordaInject
public JsonMarshallingService  jsonMarshallingService;

@CordaInject
public MemberLookup memberLookup;

@CordaInject
public NotaryLookup notaryLookup;

@NotNull
@Suspendable
public String call(RPCRequestData requestBody) {
    log.info("Utxo flow demo starting...");
    try {
        InputMessage request = requestBody.getRequestBodyAs(jsonMarshallingService, InputMessage.class);

        MemberInfo myInfo = memberLookup.myInfo();

        // This generates a list of members that:
        //    exist (null 'members' throw exception with message given identifying the missing member)
        //    
        /*
         members = request.members.map { x500 ->
        requireNotNull(memberLookup.lookup(MemberX500Name.parse(x500))) {
        "Member $x500 does not exist in the membership group"
        }
        }
        */


        // Java version
        /*
        // Ver1
        List<MemberInfo> members = new LinkedList<MemberInfo>();
        for (String member : request.members) {
            MemberInfo memInfo = memberLookup.lookup(MemberX500Name.parse(member));
            Objects.requireNonNull(memInfo, "Member " + member + " does not exist in the membership group");
            members.add(memInfo);
        }

        // Ver2
        request.members.forEach(member -> {
            members.add(
                    Objects.requireNonNull(
                            memberLookup.lookup(MemberX500Name.parse(member)),
                                    "Member " + member + " does not exist in the membership group"
                            )
                    );
        });
         */

        // Ver3
        List<MemberInfo> members = request.members.stream().map(member ->
                requireNonNull(
                        memberLookup.lookup(MemberX500Name.parse(member)),
                        "Member " + member + " does not exist in the membership group"
            )
        ).collect(
                Collectors.toList()
        );

        /*
        TestUtxoState testUtxoState = new TestUtxoState(
        request.input,
        members.map { it.ledgerKeys.first() } + myInfo.ledgerKeys.first()
        )

         */

        TestUtxoState testUtxoState = new TestUtxoState(
                request.input,
                members.stream().map( x ->  x.getLedgerKeys().get(0) ).collect(Collectors.toList())
        );

        // This is bad design, we probably only have it in the example as it's a testing example
        //NotaryInfo notaryInfo = notaryLookup.getNotaryServices().stream().findFirst().get();
        // Or
        NotaryInfo notaryInfo = notaryLookup.getNotaryServices().iterator().next();
        val notaryKey = memberLookup.lookup().first {
        it.memberProvidedContext["corda.notary.service.name"] == notary.name.toString()
        }.ledgerKeys.first();
        // TODO CORE-6173 use proper notary key
        //val notaryKey = notary.publicKey

        val txBuilder = utxoLedgerService.getTransactionBuilder();

@Suppress("DEPRECATION")
            val signedTransaction = txBuilder
                    .setNotary(Party(notary.name, notaryKey))
                    .setTimeWindowBetween(Instant.now(), Instant.now().plusMillis(1.days.toMillis()))
                    .addOutputState(testUtxoState)
                    .addCommand(TestCommand())
                    .addSignatories(testUtxoState.participants)
                    .toSignedTransaction(myInfo.ledgerKeys.first());

                    val sessions = members.map { flowMessaging.initiateFlow(it.name) }

                    return try {
                    val finalizedSignedTransaction = utxoLedgerService.finalize(
                    signedTransaction,
                    sessions
                    )
                    finalizedSignedTransaction.id.toString().also {
                    log.info("Success! Response: $it")
                    }

                    } catch (Exception e) {
                    log.warn("Finality failed", e);
                        return  new String("Finality failed, " + e.getMessage());
                    }
                    } catch (Exception e) {
                    log.warn("Failed to process utxo flow for request body '$requestBody' because:'${e.message}'");
                    throw e;
                    }
                    }
                    }

