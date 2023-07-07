package com.r3.developers.apples.contracts;

import com.r3.developers.apples.states.AppleStamp;
import net.corda.v5.ledger.utxo.Command;
import net.corda.v5.ledger.utxo.transaction.UtxoSignedTransaction;
import org.junit.jupiter.api.Test;

public class AppleStampContractIssueCommandTest extends ApplesContractTest {

    @Test
    public void happyPath() {
        UtxoSignedTransaction transaction = getLedgerService()
                .createTransactionBuilder()
                .addOutputState(outputAppleStampState)
                .addCommand(new AppleCommands.Issue())
                .addSignatories(outputAppleStampStateParticipants)
                .toSignedTransaction();
        assertVerifies(transaction);
    }

    @Test
    public void outputContractStateSizeNotOne() {
        UtxoSignedTransaction transaction = getLedgerService()
                .createTransactionBuilder()
                .addOutputState(outputAppleStampState)
                .addOutputState(outputAppleStampState)
                .addCommand(new AppleCommands.Issue())
                .addSignatories(outputAppleStampStateParticipants)
                .toSignedTransaction();
        assertFailsWith(transaction, "This transaction should only have one AppleStamp state as output");
    }

    @Test
    public void blankStampDescription() {
        UtxoSignedTransaction transaction = getLedgerService()
                .createTransactionBuilder()
                .addOutputState(
                        new AppleStamp(
                                outputAppleStampStateId,
                                "",
                                outputAppleStampStateIssuer,
                                outputAppleStampStateHolder,
                                outputAppleStampStateParticipants
                        )
                )
                .addCommand(new AppleCommands.Issue())
                .addSignatories(outputAppleStampStateParticipants)
                .toSignedTransaction();
        assertFailsWith(transaction, "The output AppleStamp state should have clear description of the type of redeemable goods");
    }

    @Test
    public void missingCommand() {
        UtxoSignedTransaction transaction = getLedgerService()
                .createTransactionBuilder()
                .addOutputState(outputAppleStampState)
                .addSignatories(outputAppleStampStateParticipants)
                .toSignedTransaction();
        assertFailsWith(transaction, "Index 0 out of bounds for length 0");
    }

    @Test
    public void unknownCommand() {
        class DummyCommand implements Command {
        }

        UtxoSignedTransaction transaction = getLedgerService()
                .createTransactionBuilder()
                .addOutputState(outputAppleStampState)
                .addCommand(new DummyCommand())
                .addSignatories(outputAppleStampStateParticipants)
                .toSignedTransaction();
        assertFailsWith(transaction, "Incorrect type of AppleStamp commands: " +
                "class com.r3.developers.apples.contracts.AppleStampContractIssueCommandTest$1DummyCommand");
    }
}