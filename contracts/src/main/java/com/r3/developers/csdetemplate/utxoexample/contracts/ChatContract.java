package com.r3.developers.csdetemplate.utxoexample.contracts;

import net.corda.v5.ledger.utxo.Command;
import net.corda.v5.ledger.utxo.Contract;
import net.corda.v5.ledger.utxo.ContractState;
import net.corda.v5.ledger.utxo.transaction.UtxoLedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.util.Set;

import static java.util.Objects.*;

public class ChatContract implements Contract {

    public static class Create implements Command { }
    public static class Update implements Command { }

    @Override
    public boolean isRelevant(@NotNull ContractState state, @NotNull Set<? extends PublicKey> myKeys) {
        return Contract.super.isRelevant(state, myKeys);
    }

    @Override
    public void verify(@NotNull UtxoLedgerTransaction transaction) throws IllegalArgumentException {
        Command command = requireNonNull( transaction.getCommands().get(0), "Require a single command");

        if(command.getClass() == Create.class) {
            requireThat(transaction.getInputContractStates().isEmpty(), "When command is Create there should be no input state");
            requireThat(transaction.getOutputContractStates().size() == 1, "When command is Create there should be one and only one output state");
        }
        else if(command.getClass() == Update.class) {
            requireThat(transaction.getInputContractStates().size() == 1, "When command is Update there should be one and only one input state");
            requireThat(transaction.getOutputContractStates().size() == 1, "When command is Update there should be one and only one output state");
        }
        else {
            throw new IllegalArgumentException("Unsupported command");
        }
    }

    private void requireThat(boolean asserted, String errorMessage) throws IllegalArgumentException {
        if(!asserted) {
            throw new IllegalArgumentException("Failed requirement: " + errorMessage);
        }
    }
}
