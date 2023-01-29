package com.r3.developers.csdetemplate.utxoexample.workflows;

import com.r3.developers.csdetemplate.utxoexample.states.ChatState;
import net.corda.v5.application.flows.CordaInject;
import net.corda.v5.application.flows.RPCRequestData;
import net.corda.v5.application.flows.RPCStartableFlow;
import net.corda.v5.application.marshalling.JsonMarshallingService;
import net.corda.v5.base.annotations.Suspendable;
import net.corda.v5.base.exceptions.CordaRuntimeException;
import net.corda.v5.crypto.SecureHash;
import net.corda.v5.ledger.utxo.StateAndRef;
import net.corda.v5.ledger.utxo.UtxoLedgerService;
import net.corda.v5.ledger.utxo.transaction.UtxoLedgerTransaction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import static java.util.Objects.*;
import static java.util.stream.Collectors.toList;

public class GetChatFlow implements RPCStartableFlow {

    private final static Logger log = LoggerFactory.getLogger(GetChatFlow.class);

    @CordaInject
    public JsonMarshallingService jsonMarshallingService;

    @CordaInject
    public UtxoLedgerService ledgerService;

//    @NotNull
    @Override
    @Suspendable
    public String call(RPCRequestData requestBody)  {

        GetChatFlowArgs flowArgs = requestBody.getRequestBodyAs(jsonMarshallingService, GetChatFlowArgs.class);

        List<StateAndRef<ChatState>> chatStateAndRefs = ledgerService.findUnconsumedStatesByType(ChatState.class);
        List<StateAndRef<ChatState>> chatStateAndRefsWithId = chatStateAndRefs.stream()
                .filter(sar -> sar.getState().getContractState().getId().equals(flowArgs.getId())).collect(toList());
        if (chatStateAndRefsWithId.size() != 1) throw new CordaRuntimeException("Multiple or zero Chat states with id " + flowArgs.getId() + " found");
        StateAndRef<ChatState> chatStateAndRef = chatStateAndRefsWithId.get(0);

        return jsonMarshallingService.format(resolveMessagesFromBackchain(chatStateAndRef, flowArgs.getNumberOfRecords() ));
    }

    @NotNull
    @Suspendable
    private List<GetChatResponse> resolveMessagesFromBackchain(StateAndRef<?> stateAndRef, int numberOfRecords) {

        List<GetChatResponse> messages = new LinkedList<>();

        StateAndRef<?> currentStateAndRef = stateAndRef;
        int recordsToFetch = numberOfRecords;
        boolean moreBackchain = true;

        while (moreBackchain) {

            SecureHash transactionId = currentStateAndRef.getRef().getTransactionHash();

            UtxoLedgerTransaction transaction = requireNonNull(
                 ledgerService.findLedgerTransaction(transactionId),
                 "Transaction " +  transactionId + " not found."
            );

            List<ChatState> chatStates = transaction.getOutputStates(ChatState.class);
            if (chatStates.size() != 1) throw new CordaRuntimeException(
                    "Expecting one and only one ChatState output for transaction " + transactionId + ".");
            ChatState output = chatStates.get(0);

            messages.add(new GetChatResponse(output.getMessageFrom().toString(), output.getMessage()));
            recordsToFetch--;

            List<StateAndRef<?>> inputStateAndRefs = transaction.getInputStateAndRefs();

	        if (inputStateAndRefs.isEmpty() || recordsToFetch == 0) {
	            moreBackchain = false;
	        } else if (inputStateAndRefs.size() > 1) {
	            throw new CordaRuntimeException("More than one input state found for transaction " + transactionId + ".");
	        } else {
	            currentStateAndRef = inputStateAndRefs.get(0);
	        }
        }
        return messages;
    }
}

/*
RequestBody for triggering the flow via http-rpc:
{
    "clientRequestId": "get-1",
    "flowClassName": "com.r3.developers.csdetemplate.utxoexample.workflows.GetChatFlow",
    "requestData": {
        "id":"** fill in id **",
        "numberOfRecords":"4"
    }
}
 */

