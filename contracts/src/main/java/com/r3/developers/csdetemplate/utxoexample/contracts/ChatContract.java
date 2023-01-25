package com.r3.developers.csdetemplate.utxoexample.contracts;

import net.corda.v5.base.exceptions.CordaRuntimeException;
import net.corda.v5.ledger.utxo.Command;
import net.corda.v5.ledger.utxo.Contract;
import net.corda.v5.ledger.utxo.ContractState;
import net.corda.v5.ledger.utxo.transaction.UtxoLedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

import static java.util.Objects.*;

public class ChatContract implements Contract {

    // Command Class used to indicate that the transaction should start a new chat.
    public static class Create implements Command { }
    // Command Class used to indicate that the transaction should append a new ChatState to an existing chat.
    public static class Update implements Command { }

    /*
    @Override
    public boolean isRelevant(@NotNull ContractState state, @NotNull Set<? extends PublicKey> myKeys) {
        return Contract.super.isRelevant(state, myKeys);
    }

     */

    // verify() function is used to apply contract rules to the transaction.
    @Override
    public void verify(@NotNull UtxoLedgerTransaction transaction) {
        Command command = requireSingleCommand(transaction.getCommands());

        requireThat(() -> {
             return transaction.getOutputContractStates().get(0).getParticipants().size() == 2;
                },
                "The output state should have two and only two participants"
        );

        // Rules applied only to transactions with the Create Command.
        if(command.getClass() == Create.class) {
            requireThat(transaction.getInputContractStates().isEmpty(), "When command is Create there should be no input state");
            requireThat(transaction.getOutputContractStates().size() == 1, "When command is Create there should be one and only one output state");
        }
        else if(command.getClass() == Update.class) {
            // Rules applied only to transactions with the Update Command.
            requireThat(transaction.getInputContractStates().size() == 1, "When command is Update there should be one and only one input state");
            requireThat(transaction.getOutputContractStates().size() == 1, "When command is Update there should be one and only one output state");
        }
        else {
            throw new IllegalArgumentException("Unsupported command");
        }
    }

    private static void requireThat(boolean asserted, String errorMessage) {
        if(!asserted) {
            throw new IllegalArgumentException("Failed requirement: " + errorMessage);
        }
    }

    private static void requireThat(BooleanSupplier booleanSupplierFn, String errorMessage) {
        if(!booleanSupplierFn.getAsBoolean()) {
            throw new CordaRuntimeException(errorMessage);
        }
    }

    private static Command requireSingleCommand(List<Command> commandList) {
        if(commandList.size() == 1) {
          throw new CordaRuntimeException("Require a single command");
        }
       return commandList.get(0);
    }

    private static <T>  T single(List<T> list) {
        switch (list.size()) {
            case 0:
                throw new CordaRuntimeException("List is empty");
            case 1:
                return list.stream().iterator().next();
            default:
                throw new CordaRuntimeException("List has more than one element");
        }
    }
}