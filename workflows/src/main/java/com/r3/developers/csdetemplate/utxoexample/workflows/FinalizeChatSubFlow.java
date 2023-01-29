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

    // these need to be private + good practice is to declare them at the start of the class
    // java code conventions:
    /*
    * According to Code Conventions for the Java Programming Language, the parts of a class or interface declaration should appear in the following order:
        * Class (static) variables. First the public class variables, then protected, then package level (no access modifier), and then private.
        * Instance variables. First the public class variables, then protected, then package level (no access modifier), and then private.
        * Constructors
        * Methods
    * */

    private final UtxoSignedTransaction signedTransaction;
    private final MemberX500Name otherMember;

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

        log.info("FinalizeChatFlow.call() called");
//        log.info("otherMember = " + otherMember);
        FlowSession session = flowMessaging.initiateFlow(otherMember);

        String result;
        try {
            List<FlowSession> sessionsList = Arrays.asList(session);
//            log.info("sessionList.size()=" + sessionsList.size());

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
//        log.info("FinalizeChatSubFlow call returns=" + retVal);
        return result;
    }

    //  We don't need getters and setters as the properties should be private

//    public UtxoSignedTransaction getSignedTransaction() {
//        return signedTransaction;
//    }
//
//    public void setSignedTransaction(UtxoSignedTransaction signedTransaction) {
//        this.signedTransaction = signedTransaction;
//    }
//
//    public MemberX500Name getOtherMember() {
//        return otherMember;
//    }
//
//    public void setOtherMember(MemberX500Name otherMember) {
//        this.otherMember = otherMember;
//    }


}
