package com.r3.developers.csdetemplate.utxoexample.workflows;

import com.r3.developers.csdetemplate.utxoexample.contracts.ChatContract;
import com.r3.developers.csdetemplate.utxoexample.states.ChatState;
import net.corda.v5.application.flows.*;
import net.corda.v5.application.marshalling.JsonMarshallingService;
import net.corda.v5.application.membership.MemberLookup;
import net.corda.v5.application.messaging.FlowMessaging;
import net.corda.v5.base.annotations.Suspendable;
import net.corda.v5.base.types.MemberX500Name;
import net.corda.v5.ledger.common.NotaryLookup;
import net.corda.v5.ledger.common.Party;
import net.corda.v5.ledger.utxo.UtxoLedgerService;
import net.corda.v5.ledger.utxo.transaction.UtxoSignedTransaction;
import net.corda.v5.ledger.utxo.transaction.UtxoTransactionBuilder;
import net.corda.v5.membership.MemberInfo;
import net.corda.v5.membership.NotaryInfo;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static java.util.Objects.*;

@InitiatingFlow(protocol = "create-chat-protocol")
public class CreateNewChatFlow implements RPCStartableFlow {

    private final static Logger log = LoggerFactory.getLogger(CreateNewChatFlow.class);

    @CordaInject
    public JsonMarshallingService jsonMarshallingService;

    @CordaInject
    public MemberLookup memberLookup;

    @CordaInject
    public UtxoLedgerService ledgerService;

    @CordaInject
    public NotaryLookup notaryLookup;

    @CordaInject
    public FlowMessaging flowMessaging;

    @CordaInject
    public FlowEngine flowEngine;

    @NotNull
    @Suspendable
    @Override
    public String call(@NotNull RPCRequestData requestBody) throws IllegalArgumentException {
        log.info("CreateNewChatFlow.call() called");

        try {
            CreateNewChatFlowArgs flowArgs = requestBody.getRequestBodyAs(jsonMarshallingService, CreateNewChatFlowArgs.class);

            MemberInfo myInfo = memberLookup.myInfo();
            MemberInfo otherMember = requireNonNull(
                    memberLookup.lookup(MemberX500Name.parse(flowArgs.getOtherMember())),
                    "can't find other member"
            );

            ChatState chatState = new ChatState(
                    flowArgs.getChatName(),
                    myInfo.getName(),
                    flowArgs.getMessage(),
                    Arrays.asList(myInfo.getLedgerKeys().get(0), otherMember.getLedgerKeys().get(0))
            );

            NotaryInfo notary = notaryLookup.getNotaryServices().iterator().next();
            /*
            // Lambda have problems see https://r3-cev.atlassian.net/browse/CORE-8983

            // Lambda here.
            Predicate<MemberInfo> myPred = memberInfo -> Objects.equals(
                    memberInfo.getMemberProvidedContext().get("corda.notary.service.name"),
                    notary.getName().toString()
            );

            List<MemberInfo> lmi = memberLookup.lookup();
            MemberInfo thing = lmi.stream().filter(myPred).iterator().next();
            PublicKey notaryKey = thing.getLedgerKeys().get(0);

             */

            // Quasar checkpointing has a bugs handling lambdas in flows.
            // This is being worked upon.
            PublicKey notaryKey = null;
            for(MemberInfo info: memberLookup.lookup()){
                if(Objects.equals(info.getMemberProvidedContext().get("corda.notary.service.name"), notary.getName().toString()) ) {
                    notaryKey = info.getLedgerKeys().get(0);
                    break;
                }
            }
            if(notary == null) {
                throw new NullPointerException("No notary found");
            }

            log.info("notary.getName()=" + notary.getName());
            log.info("chatState = " + chatState);
            log.info("chatState.getParticipants().size() = " + chatState.getParticipants().size());

            UtxoTransactionBuilder txBuilder = ledgerService.getTransactionBuilder()
                    .setNotary(new Party(notary.getName(), notaryKey))
                    .setTimeWindowBetween(Instant.now(), Instant.now().plusMillis(Duration.ofDays(1).toMillis()))
                    .addOutputState(chatState)
                    .addCommand(new ChatContract.Create())
                    .addSignatories(chatState.getParticipants());


            log.info("Before UtxoSignedTransaction signedTransaction = txBuilder.toSignedTransaction(myInfo.getLedgerKeys().get(0));");
            log.info("myInfo.getLedgerKeys().size() = " + myInfo.getLedgerKeys().size());
            log.info("myInfo.getLedgerKeys().get(0) = " + myInfo.getLedgerKeys().get(0));
            @SuppressWarnings("DEPRECATION")
            UtxoSignedTransaction signedTransaction = txBuilder.toSignedTransaction(myInfo.getLedgerKeys().get(0));
            log.info("After UtxoSignedTransaction signedTransaction = txBuilder.toSignedTransaction(myInfo.getLedgerKeys().get(0));");
            return flowEngine.subFlow(new AppendChatSubFlow(signedTransaction, otherMember.getName()));
        }
        catch (Exception e) {
            log.warn("Failed to process utxo flow for request body '$requestBody' because:'${e.message}'");
            throw e;
        }
    }
}


/*
RequestBody for triggering the flow via http-rpc:
{
    "clientRequestId": "create-1",
    "flowClassName": "com.r3.developers.csdetemplate.utxoexample.workflows.CreateNewChatFlow",
    "requestData": {
        "chatName":"Chat with Bob",
        "otherMember":"CN=Bob, OU=Test Dept, O=R3, L=London, C=GB",
        "message": "Hello Bob"
        }
}
 */
