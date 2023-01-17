package com.r3.developers.csdetemplate.utxoexample.workflows;

import com.r3.developers.csdetemplate.utxoexample.states.ChatState;
import net.corda.v5.application.flows.CordaInject;
import net.corda.v5.application.flows.InitiatedBy;
import net.corda.v5.application.flows.ResponderFlow;
import net.corda.v5.application.messaging.FlowSession;
import net.corda.v5.base.annotations.ConstructorForDeserialization;
import net.corda.v5.base.annotations.CordaSerializable;
import net.corda.v5.base.annotations.Suspendable;
import net.corda.v5.ledger.utxo.UtxoLedgerService;
import net.corda.v5.ledger.utxo.transaction.UtxoLedgerTransaction;
import net.corda.v5.ledger.utxo.transaction.UtxoSignedTransaction;
import net.corda.v5.ledger.utxo.transaction.UtxoTransactionValidator;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.r3.developers.csdetemplate.utxoexample.workflows.ResponderValidationHelpers.checkForBannedWords;
import static com.r3.developers.csdetemplate.utxoexample.workflows.ResponderValidationHelpers.checkMessageFromMatchesCounterparty;

@InitiatedBy(protocol = "append-chat-protocol")
class AppendChatResponderFlow implements ResponderFlow {

private final Logger log = LoggerFactory.getLogger(AppendChatResponderFlow.class);

@CordaInject
public UtxoLedgerService utxoLedgerService;

    @CordaSerializable
    static public class TxValidator implements UtxoTransactionValidator {
        private final Logger log = LoggerFactory.getLogger(AppendChatResponderFlow.class);

        @ConstructorForDeserialization
        public TxValidator(FlowSession session) {
            this.session = session;
        }

        @Override
        public void checkTransaction(@NotNull UtxoLedgerTransaction ledgerTransaction) {
            ChatState state = (ChatState) ledgerTransaction.getInputContractStates().get(0);
            if (checkForBannedWords(state.getMessage()) || !checkMessageFromMatchesCounterparty(state, session.getCounterparty())) {
                throw new IllegalStateException("Failed verification");
            }
            log.info("Verified the transaction - " + ledgerTransaction.getId());

        }
        private FlowSession session;
    }

    @Suspendable
    @Override
    public void call(@NotNull FlowSession session) {

        try {
            TxValidator txValidator = new TxValidator(session);

            // This is not a problem.
            UtxoSignedTransaction finalizedSignedTransaction = utxoLedgerService.receiveFinality(session, txValidator);
            log.info("Finished responder flow - " + finalizedSignedTransaction.getId());
        }
        catch(Exception e)
        {
            log.warn("Exceptionally finished responder flow", e);
        }
    }
}
