package com.r3.developers.utxodemo.workflows;

import net.corda.v5.application.flows.CordaInject;
import net.corda.v5.application.flows.InitiatedBy;
import net.corda.v5.application.flows.ResponderFlow;
import net.corda.v5.base.annotations.Suspendable;

@InitiatedBy("utxo-flow-protocol")
class UtxoResponderFlow extends ResponderFlow {

private companion object {
        val log = contextLogger()
        }

@CordaInject
    lateinit var utxoLedgerService: UtxoLedgerService

@Suspendable
    override fun call(session: FlowSession) {
            try {
            val finalizedSignedTransaction = utxoLedgerService.receiveFinality(session) { ledgerTransaction ->
            val state = ledgerTransaction.outputContractStates.first() as TestUtxoState
            if (state.testField == "fail") {
            log.info("Failed to verify the transaction - ${ledgerTransaction.id}")
            throw IllegalStateException("Failed verification")
            }
            log.info("Verified the transaction- ${ledgerTransaction.id}")
            }
            log.info("Finished responder flow - ${finalizedSignedTransaction.id}")
            } catch (e: Exception) {
            log.warn("Exceptionally finished responder flow", e)
            }
            }
            }
