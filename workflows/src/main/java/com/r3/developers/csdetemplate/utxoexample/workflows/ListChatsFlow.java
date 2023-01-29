package com.r3.developers.csdetemplate.utxoexample.workflows;

import com.r3.developers.csdetemplate.utxoexample.states.ChatState;
import net.corda.v5.application.flows.CordaInject;
import net.corda.v5.application.flows.RPCRequestData;
import net.corda.v5.application.flows.RPCStartableFlow;
import net.corda.v5.application.marshalling.JsonMarshallingService;
import net.corda.v5.base.annotations.Suspendable;
import net.corda.v5.ledger.utxo.StateAndRef;
import net.corda.v5.ledger.utxo.UtxoLedgerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class ListChatsFlow implements RPCStartableFlow{

    private final static Logger log = LoggerFactory.getLogger(ListChatsFlow.class);

    @CordaInject
    public JsonMarshallingService jsonMarshallingService;

    @CordaInject
    public UtxoLedgerService utxoLedgerService;

    @Suspendable
    @Override
    public String call(RPCRequestData requestBody) {

        log.info("ListChatsFlow.call() called");

        List<StateAndRef<ChatState>> states = utxoLedgerService.findUnconsumedStatesByType(ChatState.class);
        List<ChatStateResults> results = states.stream().map( stateAndRef ->
            new ChatStateResults(
                    stateAndRef.getState().getContractState().getId(),
                    stateAndRef.getState().getContractState().getChatName(),
                    stateAndRef.getState().getContractState().getMessageFrom().toString(),
                    stateAndRef.getState().getContractState().getMessage()
                    )
        ).collect(Collectors.toList());

        return jsonMarshallingService.format(results);
    }
}

/*
RequestBody for triggering the flow via http-rpc:
{
    "clientRequestId": "list-1",
    "flowClassName": "com.r3.developers.csdetemplate.utxoexample.workflows.ListChatsFlow",
    "requestData": {}
}
*/