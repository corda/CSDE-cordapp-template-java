package com.r3.developers.apples.contracts;

import com.r3.corda.ledger.utxo.testing.ContractTest;
import com.r3.developers.apples.states.AppleStamp;
import com.r3.developers.apples.states.BasketOfApples;
import net.corda.v5.ledger.utxo.StateAndRef;
import net.corda.v5.ledger.utxo.transaction.UtxoSignedTransaction;
import java.security.PublicKey;
import java.util.List;
import java.util.UUID;

public abstract class ApplesContractTest extends ContractTest {

    protected UUID outputAppleStampStateId = UUID.randomUUID();
    protected String outputAppleStampStateStampDesc = "Can be exchanged for a single basket of apples";
    protected PublicKey outputAppleStampStateIssuer = bobKey;
    protected PublicKey outputAppleStampStateHolder = daveKey;
    protected List<PublicKey> outputAppleStampStateParticipants = List.of(bobKey, daveKey);
    protected AppleStamp outputAppleStampState = new AppleStamp(
            outputAppleStampStateId,
            outputAppleStampStateStampDesc,
            outputAppleStampStateIssuer,
            outputAppleStampStateHolder,
            outputAppleStampStateParticipants
    );

    protected String outputBasketOfApplesStateDescription = "Golden delicious apples, picked on 11th May 2023";
    protected PublicKey outputBasketOfApplesStateFarm = bobKey;
    protected PublicKey outputBasketOfApplesStateOwner = bobKey;
    protected int outputBasketOfApplesStateWeight = 214;
    protected List<PublicKey> outputBasketOfApplesStateParticipants = List.of(bobKey);
    protected BasketOfApples outputBasketOfApplesState = new BasketOfApples(
            outputBasketOfApplesStateDescription,
            outputBasketOfApplesStateFarm,
            outputBasketOfApplesStateOwner,
            outputBasketOfApplesStateWeight,
            outputBasketOfApplesStateParticipants
    );

    @SuppressWarnings("unchecked")
    protected StateAndRef<AppleStamp> createInputStateAppleStamp(AppleStamp outputState) {
        UtxoSignedTransaction transaction = getLedgerService()
                .createTransactionBuilder()
                .addOutputState(outputState)
                .addCommand(new AppleCommands.Issue())
                .addSignatories(outputState.getParticipants())
                .toSignedTransaction();
        transaction.toLedgerTransaction();
        return (StateAndRef<AppleStamp>) transaction.getOutputStateAndRefs().get(0);
    }

    @SuppressWarnings("unchecked")
    protected StateAndRef<BasketOfApples> createInputStateBasketOfApples(BasketOfApples outputState) {
        UtxoSignedTransaction transaction = getLedgerService()
                .createTransactionBuilder()
                .addOutputState(outputState)
                .addCommand(new AppleCommands.PackBasket())
                .addSignatories(outputState.getParticipants())
                .toSignedTransaction();
        transaction.toLedgerTransaction();
        return (StateAndRef<BasketOfApples>) transaction.getOutputStateAndRefs().get(0);
    }
}