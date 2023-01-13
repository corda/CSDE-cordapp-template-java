package com.r3.developers.csdetemplate.utxoexample.workflows;

import com.r3.developers.csdetemplate.utxoexample.states.ChatState;
import net.corda.v5.application.flows.*;
import net.corda.v5.application.messaging.FlowMessaging;
import net.corda.v5.application.messaging.FlowSession;
import net.corda.v5.base.annotations.Suspendable;
import net.corda.v5.base.types.MemberX500Name;
import net.corda.v5.ledger.utxo.UtxoLedgerService;
import net.corda.v5.ledger.utxo.transaction.UtxoSignedTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

@InitiatingFlow(protocol = "append-chat-protocol")
public class AppendChatSubFlow implements SubFlow<String> {

    public AppendChatSubFlow() {}
    public AppendChatSubFlow(UtxoSignedTransaction signedTransaction, MemberX500Name otherMember) {
        this.signedTransaction = signedTransaction;
        this.otherMember = otherMember;
    }

    private final Logger log = LoggerFactory.getLogger(AppendChatSubFlow.class);

    @CordaInject
    public UtxoLedgerService ledgerService;

    @CordaInject
    public FlowMessaging flowMessaging;

    @Override
    @Suspendable
    public String call() {

        log.info("AppendChatFlow.call() called");

        FlowSession session = flowMessaging.initiateFlow(otherMember);

        String retVal;
        try {
            UtxoSignedTransaction finalizedSignedTransaction = ledgerService.finalize(
                    signedTransaction,
                    List.of(session)
            );
            retVal = finalizedSignedTransaction.getId().toString();
            log.info("Success! Response: " + retVal);
        } catch (Exception e) {
            log.warn("Finality failed", e);
            retVal = "Finality failed, " + e.getMessage();
        }
        return retVal;
    }

    private UtxoSignedTransaction signedTransaction;
    private MemberX500Name otherMember;
}
