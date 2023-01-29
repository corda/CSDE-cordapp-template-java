package com.r3.developers.csdetemplate.utxoexample.workflows;

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

@InitiatingFlow(protocol = "finalize-chat-protocol")
public class FinalizeChatSubFlow implements SubFlow<String> {

    private final static Logger log = LoggerFactory.getLogger(FinalizeChatSubFlow.class);
    private final UtxoSignedTransaction signedTransaction;
    private final MemberX500Name otherMember;

    public FinalizeChatSubFlow(UtxoSignedTransaction signedTransaction, MemberX500Name otherMember) {
        this.signedTransaction = signedTransaction;
        this.otherMember = otherMember;
    }

    @CordaInject
    public UtxoLedgerService ledgerService;

    @CordaInject
    public FlowMessaging flowMessaging;

    @Override
    @Suspendable
    public String call() {

        log.info("FinalizeChatFlow.call() called");

        FlowSession session = flowMessaging.initiateFlow(otherMember);

        String result;
        try {
            List<FlowSession> sessionsList = Arrays.asList(session);

            UtxoSignedTransaction finalizedSignedTransaction = ledgerService.finalize(
                    signedTransaction,
                    sessionsList
            );

            result = finalizedSignedTransaction.getId().toString();
            log.info("Success! Response: " + result);

        } catch (Exception e) {
            log.warn("Finality failed", e);
            result = "Finality failed, " + e.getMessage();
        }

        return result;
    }
}
