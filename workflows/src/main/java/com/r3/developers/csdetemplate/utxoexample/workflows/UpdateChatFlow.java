package com.r3.developers.csdetemplate.utxoexample.workflows;

import com.r3.developers.csdetemplate.utxoexample.contracts.ChatContract;
import com.r3.developers.csdetemplate.utxoexample.states.ChatState;
import net.corda.v5.application.flows.CordaInject;
import net.corda.v5.application.flows.FlowEngine;
import net.corda.v5.application.flows.RPCRequestData;
import net.corda.v5.application.flows.RPCStartableFlow;
import net.corda.v5.application.marshalling.JsonMarshallingService;
import net.corda.v5.application.membership.MemberLookup;
import net.corda.v5.base.annotations.Suspendable;
import net.corda.v5.base.exceptions.CordaRuntimeException;
import net.corda.v5.ledger.utxo.StateAndRef;
import net.corda.v5.ledger.utxo.UtxoLedgerService;
import net.corda.v5.ledger.utxo.transaction.UtxoSignedTransaction;
import net.corda.v5.ledger.utxo.transaction.UtxoTransactionBuilder;
import net.corda.v5.membership.MemberInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static java.util.Objects.*;
import static java.util.stream.Collectors.toList;

public class UpdateChatFlow implements RPCStartableFlow {

    private final static Logger log = LoggerFactory.getLogger(UpdateChatFlow.class);

    @CordaInject
    public JsonMarshallingService jsonMarshallingService;

    @CordaInject
    public MemberLookup memberLookup;

    @CordaInject
    public UtxoLedgerService ledgerService;

    @CordaInject
    public FlowEngine flowEngine;

    @Suspendable
    @Override
    public String call(RPCRequestData requestBody) {

        log.info("UpdateNewChatFlow.call() called");

        try {
             UpdateChatFlowArgs flowArgs = requestBody.getRequestBodyAs(jsonMarshallingService, UpdateChatFlowArgs.class);

            List<StateAndRef<ChatState>> chatStateAndRefs = ledgerService.findUnconsumedStatesByType(ChatState.class);
            List<StateAndRef<ChatState>> chatStateAndRefsWithId = chatStateAndRefs.stream()
                    .filter(sar -> sar.getState().getContractState().getId().equals(flowArgs.getId())).collect(toList());
            if (chatStateAndRefsWithId.size() != 1) throw new CordaRuntimeException("Multiple or zero Chat states with id " + flowArgs.getId() + " found");
            StateAndRef<ChatState> chatStateAndRef = chatStateAndRefsWithId.get(0);


            MemberInfo myInfo = memberLookup.myInfo();
            ChatState state = chatStateAndRef.getState().getContractState();

            List<MemberInfo> members = state.getParticipants().stream().map(
                    it -> requireNonNull(memberLookup.lookup(it), "Member not found from public Key "+ it + ".")
            ).collect(toList());

            members.remove(myInfo);
            if(members.size() != 1) throw new RuntimeException("Should be only one participant other than the initiator");

            MemberInfo otherMember = members.get(0);

            ChatState newChatState = state.updateMessage(myInfo.getName(), flowArgs.getMessage());

            UtxoTransactionBuilder txBuilder = ledgerService.getTransactionBuilder()
                    .setNotary(chatStateAndRef.getState().getNotary())
                    .setTimeWindowBetween(Instant.now(), Instant.now().plusMillis(Duration.ofDays(1).toMillis()))
                    .addOutputState(newChatState)
                    .addInputState(chatStateAndRef.getRef())
                    .addCommand(new ChatContract.Update())
                    .addSignatories(newChatState.getParticipants());

            @SuppressWarnings("DEPRECATION")
            UtxoSignedTransaction signedTransaction = txBuilder.toSignedTransaction(myInfo.getLedgerKeys().get(0));

            return flowEngine.subFlow(new FinalizeChatSubFlow(signedTransaction, otherMember.getName()));
        } catch (Exception e) {
            log.warn("Failed to process utxo flow for request body " + requestBody + " because: " + e.getMessage());
            throw e;
        }
    }
}

/*
RequestBody for triggering the flow via http-rpc:
{
    "clientRequestId": "update-1",
    "flowClassName": "com.r3.developers.csdetemplate.utxoexample.workflows.UpdateChatFlow",
    "requestData": {
        "id":" ** fill in id **",
        "message": "How are you today?"
        }
}
 */