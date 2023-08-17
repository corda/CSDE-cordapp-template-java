package com.r3.developers.csdetemplate.utxoexample.contracts;

import com.r3.corda.ledger.utxo.testing.ContractTest;
import com.r3.developers.csdetemplate.utxoexample.states.ChatState;
import net.corda.v5.ledger.utxo.StateAndRef;
import net.corda.v5.ledger.utxo.transaction.UtxoSignedTransaction;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static com.r3.developers.csdetemplate.utxoexample.contracts.ChatContract.*;

public class ChatContractUpdateCommandTest extends ContractTest {

    @SuppressWarnings("unchecked")
    private StateAndRef<ChatState> createInitialChatState() {
        ChatState outputChatState = new ChatContractCreateCommandTest().outputChatState;
        UtxoSignedTransaction transaction = getLedgerService()
                .createTransactionBuilder()
                .addOutputState(outputChatState)
                .addCommand(new Create())
                .addSignatories(outputChatState.participants)
                .toSignedTransaction();
        transaction.toLedgerTransaction();
        return (StateAndRef<ChatState>) transaction.getOutputStateAndRefs().get(0);
    }

    @Test
    public void happyPath() {
        StateAndRef<ChatState> existingState = createInitialChatState();
        ChatState updatedOutputChatState = existingState.getState().getContractState().updateMessage(bobName, "bobResponse");
        UtxoSignedTransaction transaction = getLedgerService()
                .createTransactionBuilder()
                .addInputState(existingState.getRef())
                .addOutputState(updatedOutputChatState)
                .addCommand(new Update())
                .addSignatories(updatedOutputChatState.participants)
                .toSignedTransaction();
        assertVerifies(transaction);
    }

    @Test
    public void shouldNotHaveNoInputState() {
        StateAndRef<ChatState> existingState = createInitialChatState();
        ChatState updatedOutputChatState = existingState.getState().getContractState().updateMessage(bobName, "bobResponse");
        UtxoSignedTransaction transaction = getLedgerService()
                .createTransactionBuilder()
                .addOutputState(updatedOutputChatState)
                .addCommand(new Update())
                .addSignatories(updatedOutputChatState.participants)
                .toSignedTransaction();
        assertFailsWith(transaction, "Failed requirement: " + UPDATE_COMMAND_SHOULD_HAVE_ONLY_ONE_INPUT_STATE);
    }

    @Test
    public void shouldNotHaveTwoInputStates() {
        StateAndRef<ChatState> existingState = createInitialChatState();
        ChatState updatedOutputChatState = existingState.getState().getContractState().updateMessage(bobName, "bobResponse");
        UtxoSignedTransaction transaction = getLedgerService()
                .createTransactionBuilder()
                .addInputState(existingState.getRef())
                .addInputState(existingState.getRef())
                .addOutputState(updatedOutputChatState)
                .addCommand(new Update())
                .addSignatories(updatedOutputChatState.participants)
                .toSignedTransaction();
        assertFailsWith(transaction, "Failed requirement: " + UPDATE_COMMAND_SHOULD_HAVE_ONLY_ONE_INPUT_STATE);
    }

    @Test
    public void shouldNotHaveTwoOutputStates() {
        StateAndRef<ChatState> existingState = createInitialChatState();
        ChatState updatedOutputChatState = existingState.getState().getContractState().updateMessage(bobName, "bobResponse");
        UtxoSignedTransaction transaction = getLedgerService()
                .createTransactionBuilder()
                .addInputState(existingState.getRef())
                .addOutputState(updatedOutputChatState)
                .addOutputState(updatedOutputChatState)
                .addCommand(new Update())
                .addSignatories(updatedOutputChatState.participants)
                .toSignedTransaction();
        assertFailsWith(transaction, "Failed requirement: " + UPDATE_COMMAND_SHOULD_HAVE_ONLY_ONE_OUTPUT_STATE);
    }

    @Test
    public void idShouldNotChange() {
        StateAndRef<ChatState> existingState = createInitialChatState();
        ChatState esDetails = existingState.getState().getContractState();
        ChatState updatedOutputChatState = new ChatState(
                UUID.randomUUID(),
                esDetails.getChatName(),
                bobName,
                "bobResponse",
                esDetails.getParticipants()
        );
        UtxoSignedTransaction transaction = getLedgerService()
                .createTransactionBuilder()
                .addInputState(existingState.getRef())
                .addOutputState(updatedOutputChatState)
                .addCommand(new Update())
                .addSignatories(updatedOutputChatState.participants)
                .toSignedTransaction();
        assertFailsWith(transaction, "Failed requirement: " + UPDATE_COMMAND_ID_SHOULD_NOT_CHANGE);
    }

    @Test
    public void chatNameShouldNotChange() {
        StateAndRef<ChatState> existingState = createInitialChatState();
        ChatState esDetails = existingState.getState().getContractState();
        ChatState updatedOutputChatState = new ChatState(
                esDetails.getId(),
                "newName",
                bobName,
                "bobResponse",
                esDetails.getParticipants()
        );
        UtxoSignedTransaction transaction = getLedgerService()
                .createTransactionBuilder()
                .addInputState(existingState.getRef())
                .addOutputState(updatedOutputChatState)
                .addCommand(new Update())
                .addSignatories(updatedOutputChatState.participants)
                .toSignedTransaction();
        assertFailsWith(transaction, "Failed requirement: " + UPDATE_COMMAND_CHATNAME_SHOULD_NOT_CHANGE);
    }

    @Test
    public void participantsShouldNotChange() {
        StateAndRef<ChatState> existingState = createInitialChatState();
        ChatState esDetails = existingState.getState().getContractState();
        ChatState updatedOutputChatState = new ChatState(
                esDetails.getId(),
                esDetails.getChatName(),
                bobName,
                "bobResponse",
                List.of(bobKey, charlieKey)
        );
        UtxoSignedTransaction transaction = getLedgerService()
                .createTransactionBuilder()
                .addInputState(existingState.getRef())
                .addOutputState(updatedOutputChatState)
                .addCommand(new Update())
                .addSignatories(updatedOutputChatState.participants)
                .toSignedTransaction();
        assertFailsWith(transaction, "Failed requirement: " + UPDATE_COMMAND_PARTICIPANTS_SHOULD_NOT_CHANGE);
    }

    @Test
    public void outputStateMustBeSigned() {
        StateAndRef<ChatState> existingState = createInitialChatState();
        ChatState updatedOutputChatState = existingState.getState().getContractState().updateMessage(bobName, "bobResponse");
        UtxoSignedTransaction transaction = getLedgerService()
                .createTransactionBuilder()
                .addInputState(existingState.getRef())
                .addOutputState(updatedOutputChatState)
                .addCommand(new Update())
                .toSignedTransaction();
        assertFailsWith(transaction, "Failed requirement: " + TRANSACTION_SHOULD_BE_SIGNED_BY_ALL_PARTICIPANTS);
    }

    @Test
    public void outputStateCannotBeSignedByOnlyOneParticipant() {
        StateAndRef<ChatState> existingState = createInitialChatState();
        ChatState updatedOutputChatState = existingState.getState().getContractState().updateMessage(bobName, "bobResponse");
        UtxoSignedTransaction transaction = getLedgerService()
                .createTransactionBuilder()
                .addInputState(existingState.getRef())
                .addOutputState(updatedOutputChatState)
                .addCommand(new Update())
                .addSignatories(updatedOutputChatState.participants.get(0))
                .toSignedTransaction();
        assertFailsWith(transaction, "Failed requirement: " + TRANSACTION_SHOULD_BE_SIGNED_BY_ALL_PARTICIPANTS);
    }
}
