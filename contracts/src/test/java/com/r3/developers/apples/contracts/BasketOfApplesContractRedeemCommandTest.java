package com.r3.developers.apples.contracts;

import com.r3.developers.apples.states.AppleStamp;
import com.r3.developers.apples.states.BasketOfApples;
import net.corda.v5.ledger.utxo.StateAndRef;
import net.corda.v5.ledger.utxo.transaction.UtxoSignedTransaction;
import org.junit.jupiter.api.Test;
import java.util.List;

public class BasketOfApplesContractRedeemCommandTest extends ApplesContractTest {

    @Test
    public void happyPath() {
        StateAndRef inputAppleStampState = createInputStateAppleStamp(outputAppleStampState);
        StateAndRef inputBasketOfApplesStates = createInputStateBasketOfApples(outputBasketOfApplesState);
        UtxoSignedTransaction transaction = getLedgerService()
                .createTransactionBuilder()
                .addInputStates(List.of(inputAppleStampState.getRef(), inputBasketOfApplesStates.getRef()))
                .addOutputState(outputBasketOfApplesState)
                .addCommand(new AppleCommands.Redeem())
                .addSignatories(outputBasketOfApplesStateParticipants)
                .toSignedTransaction();
        assertVerifies(transaction);
    }

    @Test
    public void inputContractStateSizeNotTwo() {
        StateAndRef inputAppleStampState = createInputStateAppleStamp(outputAppleStampState);
        UtxoSignedTransaction transaction = getLedgerService()
                .createTransactionBuilder()
                .addInputStates(inputAppleStampState.getRef())
                .addOutputState(outputBasketOfApplesState)
                .addCommand(new AppleCommands.Redeem())
                .addSignatories(outputBasketOfApplesStateParticipants)
                .toSignedTransaction();
        assertFailsWith(transaction, "This transaction should consume two states");
    }

    @Test
    public void twoAppleStampStateInputs() {
        StateAndRef inputAppleStampState = createInputStateAppleStamp(outputAppleStampState);
        UtxoSignedTransaction transaction = getLedgerService()
                .createTransactionBuilder()
                .addInputStates(List.of(inputAppleStampState.getRef(), inputAppleStampState.getRef()))
                .addOutputState(outputBasketOfApplesState)
                .addCommand(new AppleCommands.Redeem())
                .addSignatories(outputBasketOfApplesStateParticipants)
                .toSignedTransaction();
        assertFailsWith(transaction, "This transaction should have exactly one AppleStamp and one BasketOfApples input state");
    }

    @Test
    public void appleStampIssuerDifferentFromBasketFarm() {
        AppleStamp appleStampStateDifferentIssuer = new AppleStamp(
                outputAppleStampStateId,
                outputAppleStampStateStampDesc,
                aliceKey,
                outputAppleStampStateHolder,
                List.of(aliceKey, outputAppleStampStateHolder)
        );
        StateAndRef invalidInputAppleStampState = createInputStateAppleStamp(appleStampStateDifferentIssuer);
        StateAndRef inputBasketOfApplesStates = createInputStateBasketOfApples(outputBasketOfApplesState);
        UtxoSignedTransaction transaction = getLedgerService()
                .createTransactionBuilder()
                .addInputStates(List.of(invalidInputAppleStampState.getRef(), inputBasketOfApplesStates.getRef()))
                .addOutputState(outputBasketOfApplesState)
                .addCommand(new AppleCommands.Redeem())
                .addSignatories(outputBasketOfApplesStateParticipants)
                .toSignedTransaction();
        assertFailsWith(transaction, "The issuer of the Apple stamp should be the producing farm of this basket of apple");
    }

    @Test
    public void basketWeightIsZero() {
        BasketOfApples inputBasketOfApplesStatesZeroWeight = new BasketOfApples(
                outputBasketOfApplesStateDescription,
                outputBasketOfApplesStateFarm,
                outputBasketOfApplesStateOwner,
                0,
                outputBasketOfApplesStateParticipants
        );
        StateAndRef inputAppleStampState = createInputStateAppleStamp(outputAppleStampState);
        StateAndRef invalidInputBasketOfApplesStates = createInputStateBasketOfApples(inputBasketOfApplesStatesZeroWeight);
        UtxoSignedTransaction transaction = getLedgerService()
                .createTransactionBuilder()
                .addInputStates(List.of(inputAppleStampState.getRef(), invalidInputBasketOfApplesStates.getRef()))
                .addOutputState(outputBasketOfApplesState)
                .addCommand(new AppleCommands.Redeem())
                .addSignatories(outputBasketOfApplesStateParticipants)
                .toSignedTransaction();
        assertFailsWith(transaction, "The basket of apple has to weigh more than 0");
    }
}