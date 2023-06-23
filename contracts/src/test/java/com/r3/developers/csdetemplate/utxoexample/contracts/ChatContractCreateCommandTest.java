package com.r3.developers.csdetemplate.utxoexample.contracts;

import com.r3.corda.ledger.utxo.testing.ContractTest;
import com.r3.developers.csdetemplate.utxoexample.states.ChatState;
import net.corda.v5.ledger.utxo.Command;
import net.corda.v5.ledger.utxo.StateAndRef;
import net.corda.v5.ledger.utxo.transaction.UtxoSignedTransaction;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static com.r3.developers.csdetemplate.utxoexample.contracts.ChatContract.*;
import static java.util.Collections.emptyList;

public class ChatContractCreateCommandTest extends ContractTest {

    protected ChatState outputChatState = new ChatState(
            UUID.randomUUID(),
            "aliceChatName",
            aliceName,
            "aliceChatMessage",
            List.of(aliceKey, bobKey)
    );

    @Test
    public void happyPath() {
        UtxoSignedTransaction transaction = getLedgerService()
                .createTransactionBuilder()
                .addOutputState(outputChatState)
                .addCommand(new ChatContract.Create())
                .addSignatories(outputChatState.participants)
                .toSignedTransaction();
        assertVerifies(transaction);
    }

    @Test
    public void missingCommand() {
        UtxoSignedTransaction transaction = getLedgerService()
                .createTransactionBuilder()
                .addOutputState(outputChatState)
                .toSignedTransaction();
        assertFailsWith(transaction, "Failed requirement: " + REQUIRE_SINGLE_COMMAND);
    }

    @Test
    public void shouldNotAcceptUnknownCommand() {
        class MyDummyCommand implements Command {
        }

        UtxoSignedTransaction transaction = getLedgerService()
                .createTransactionBuilder()
                .addOutputState(outputChatState)
                .addCommand(new MyDummyCommand())
                .addSignatories(outputChatState.participants)
                .toSignedTransaction();

        assertFailsWith(transaction, UNKNOWN_COMMAND);
    }

    @Test
    public void outputStateCannotHaveZeroParticipants() {
        ChatState state = new ChatState(
                UUID.randomUUID(),
                "myChatName",
                aliceName,
                "myChatMessage",
                emptyList()
        );
        UtxoSignedTransaction transaction = getLedgerService()
                .createTransactionBuilder()
                .addOutputState(state)
                .addCommand(new ChatContract.Create())
                .toSignedTransaction();
        assertFailsWith(transaction, "Failed requirement: " + OUTPUT_STATE_SHOULD_ONLY_HAVE_TWO_PARTICIPANTS);
    }

    @Test
    public void outputStateCannotHaveOneParticipant() {
        ChatState state = new ChatState(
                UUID.randomUUID(),
                "myChatName",
                aliceName,
                "myChatMessage",
                List.of(aliceKey)
        );
        UtxoSignedTransaction transaction = getLedgerService()
                .createTransactionBuilder()
                .addOutputState(state)
                .addCommand(new ChatContract.Create())
                .toSignedTransaction();
        assertFailsWith(transaction, "Failed requirement: " + OUTPUT_STATE_SHOULD_ONLY_HAVE_TWO_PARTICIPANTS);
    }

    @Test
    public void outputStateCannotHaveThreeParticipants() {
        ChatState state = new ChatState(
                UUID.randomUUID(),
                "myChatName",
                aliceName,
                "myChatMessage",
                List.of(aliceKey, bobKey, charlieKey)
        );
        UtxoSignedTransaction transaction = getLedgerService()
                .createTransactionBuilder()
                .addOutputState(state)
                .addCommand(new ChatContract.Create())
                .toSignedTransaction();
        assertFailsWith(transaction, "Failed requirement: " + OUTPUT_STATE_SHOULD_ONLY_HAVE_TWO_PARTICIPANTS);
    }

    @Test
    public void outputStateMustBeSigned() {
        UtxoSignedTransaction transaction = getLedgerService()
                .createTransactionBuilder()
                .addOutputState(outputChatState)
                .addCommand(new ChatContract.Create())
                .toSignedTransaction();
        assertFailsWith(transaction, "Failed requirement: " + TRANSACTION_SHOULD_BE_SIGNED_BY_ALL_PARTICIPANTS);
    }

    @Test
    public void outputStateCannotBeSignedByOnlyOneParticipant() {
        UtxoSignedTransaction transaction = getLedgerService()
                .createTransactionBuilder()
                .addOutputState(outputChatState)
                .addCommand(new ChatContract.Create())
                .addSignatories(outputChatState.participants.get(0))
                .toSignedTransaction();
        assertFailsWith(transaction, "Failed requirement: " + TRANSACTION_SHOULD_BE_SIGNED_BY_ALL_PARTICIPANTS);
    }

    @Test
    public void shouldNotIncludeInputState() {
        happyPath(); // generate an existing state to search for
        StateAndRef<ChatState> existingState = getLedgerService().findUnconsumedStatesByType(ChatState.class).get(0); // doesn't matter which as this will fail validation
        UtxoSignedTransaction transaction = getLedgerService()
                .createTransactionBuilder()
                .addInputState(existingState.getRef())
                .addOutputState(outputChatState)
                .addCommand(new ChatContract.Create())
                .addSignatories(outputChatState.participants)
                .toSignedTransaction();
        assertFailsWith(transaction, "Failed requirement: " + CREATE_COMMAND_SHOULD_HAVE_NO_INPUT_STATES);
    }

    @Test
    public void shouldNotHaveTwoOutputStates() {
        UtxoSignedTransaction transaction = getLedgerService()
                .createTransactionBuilder()
                .addOutputState(outputChatState)
                .addOutputState(outputChatState)
                .addCommand(new ChatContract.Create())
                .addSignatories(outputChatState.participants)
                .toSignedTransaction();
        assertFailsWith(transaction, "Failed requirement: " + CREATE_COMMAND_SHOULD_HAVE_ONLY_ONE_OUTPUT_STATE);
    }
}
