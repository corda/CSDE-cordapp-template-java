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

    public FinalizeChatSubFlow(UtxoSignedTransaction signedTransaction, MemberX500Name otherMember) {
        this.signedTransaction = signedTransaction;
        this.otherMember = otherMember;
    }

    private final static Logger log = LoggerFactory.getLogger(FinalizeChatSubFlow.class);

    @CordaInject
    public UtxoLedgerService ledgerService;

    @CordaInject
    public FlowMessaging flowMessaging;

    @Override
    @Suspendable
    public String call() {

//        log.info("AppendChatFlow.call() called");
//        log.info("otherMember = " + otherMember);
        FlowSession session = flowMessaging.initiateFlow(otherMember);

        String retVal;
        try {
            List<FlowSession> sessionsList = Arrays.asList(session);
            log.info("sessionList.size()=" + sessionsList.size());

            UtxoSignedTransaction finalizedSignedTransaction = ledgerService.finalize(
                    signedTransaction,
                    sessionsList
            );

            retVal = finalizedSignedTransaction.getId().toString();
            log.info("Success! Response: " + retVal);
        } catch (Exception e) {
            log.warn("Finality failed", e);
            retVal = "Finality failed, " + e.getMessage();
        }
        log.info("AppendChatSubFlow call returns=" + retVal);
        return retVal;
    }

    public UtxoSignedTransaction getSignedTransaction() {
        return signedTransaction;
    }

    public void setSignedTransaction(UtxoSignedTransaction signedTransaction) {
        this.signedTransaction = signedTransaction;
    }

    public MemberX500Name getOtherMember() {
        return otherMember;
    }

    public void setOtherMember(MemberX500Name otherMember) {
        this.otherMember = otherMember;
    }

    public UtxoSignedTransaction signedTransaction;
    public MemberX500Name otherMember;
}
