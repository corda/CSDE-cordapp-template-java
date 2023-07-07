package com.r3.developers.apples.contracts;

import com.r3.developers.apples.states.BasketOfApples;
import net.corda.v5.ledger.utxo.Command;
import net.corda.v5.ledger.utxo.transaction.UtxoSignedTransaction;
import org.junit.jupiter.api.Test;

public class BasketOfApplesContractPackBasketCommandTest extends ApplesContractTest {

    @Test
    public void happyPath() {
        UtxoSignedTransaction transaction = getLedgerService()
                .createTransactionBuilder()
                .addOutputState(outputBasketOfApplesState)
                .addCommand(new AppleCommands.PackBasket())
                .addSignatories(outputBasketOfApplesStateParticipants)
                .toSignedTransaction();
        assertVerifies(transaction);
    }

    @Test
    public void outputContractStateSizeNoteOne() {
        UtxoSignedTransaction transaction = getLedgerService()
                .createTransactionBuilder()
                .addOutputState(outputBasketOfApplesState)
                .addOutputState(outputBasketOfApplesState)
                .addCommand(new AppleCommands.PackBasket())
                .addSignatories(outputBasketOfApplesStateParticipants)
                .toSignedTransaction();
        assertFailsWith(transaction, "This transaction should only output one BasketOfApples state");
    }

    @Test
    public void blankDescription() {
        UtxoSignedTransaction transaction = getLedgerService()
                .createTransactionBuilder()
                .addOutputState(
                        new BasketOfApples(
                                "",
                                outputBasketOfApplesStateFarm,
                                outputBasketOfApplesStateOwner,
                                outputBasketOfApplesStateWeight,
                                outputBasketOfApplesStateParticipants
                        )
                )
                .addCommand(new AppleCommands.PackBasket())
                .addSignatories(outputBasketOfApplesStateParticipants)
                .toSignedTransaction();
        assertFailsWith(transaction, "The output BasketOfApples state should have clear description of Apple product");
    }

    @Test
    public void basketWeightIsZero() {
        UtxoSignedTransaction transaction = getLedgerService()
                .createTransactionBuilder()
                .addOutputState(
                        new BasketOfApples(
                                outputBasketOfApplesStateDescription,
                                outputBasketOfApplesStateFarm,
                                outputBasketOfApplesStateOwner,
                                0,
                                outputBasketOfApplesStateParticipants
                        )
                )
                .addCommand(new AppleCommands.PackBasket())
                .addSignatories(outputBasketOfApplesStateParticipants)
                .toSignedTransaction();
        assertFailsWith(transaction, "The output BasketOfApples state should have non zero weight");
    }

    @Test
    public void basketWeightIsNegative() {
        UtxoSignedTransaction transaction = getLedgerService()
                .createTransactionBuilder()
                .addOutputState(
                        new BasketOfApples(
                                outputBasketOfApplesStateDescription,
                                outputBasketOfApplesStateFarm,
                                outputBasketOfApplesStateOwner,
                                -1,
                                outputBasketOfApplesStateParticipants
                        )
                )
                .addCommand(new AppleCommands.PackBasket())
                .addSignatories(outputBasketOfApplesStateParticipants)
                .toSignedTransaction();
        assertFailsWith(transaction, "The output BasketOfApples state should have non zero weight");
    }

    @Test
    public void missingCommand() {
        UtxoSignedTransaction transaction = getLedgerService()
                .createTransactionBuilder()
                .addOutputState(outputBasketOfApplesState)
                .addSignatories(outputBasketOfApplesStateParticipants)
                .toSignedTransaction();
        assertFailsWith(transaction, "Index 0 out of bounds for length 0");
    }

    @Test
    public void unknownCommand() {
        class DummyCommand implements Command {
        }

        UtxoSignedTransaction transaction = getLedgerService()
                .createTransactionBuilder()
                .addOutputState(outputBasketOfApplesState)
                .addCommand(new DummyCommand())
                .addSignatories(outputBasketOfApplesStateParticipants)
                .toSignedTransaction();
        assertFailsWith(transaction, "Incorrect type of BasketOfApples commands: " +
                "class com.r3.developers.apples.contracts.BasketOfApplesContractPackBasketCommandTest$1DummyCommand");
    }
}