package com.r3.developers.utxodemo.contracts;

import net.corda.v5.ledger.utxo.Contract;
import net.corda.v5.ledger.utxo.transaction.UtxoLedgerTransaction;

public class TestContract implements Contract {
        public void verify(UtxoLedgerTransaction transaction) {
            // Empty
        }
}