package com.r3.developers.apples.workflows;

import com.r3.developers.apples.contracts.AppleCommands;
import com.r3.developers.apples.states.BasketOfApples;
import net.corda.v5.application.flows.ClientRequestBody;
import net.corda.v5.application.flows.ClientStartableFlow;
import net.corda.v5.application.flows.CordaInject;
import net.corda.v5.application.marshalling.JsonMarshallingService;
import net.corda.v5.application.membership.MemberLookup;
import net.corda.v5.base.annotations.Suspendable;
import net.corda.v5.ledger.common.NotaryLookup;
import net.corda.v5.ledger.utxo.UtxoLedgerService;
import net.corda.v5.ledger.utxo.transaction.UtxoSignedTransaction;
import net.corda.v5.membership.NotaryInfo;
import java.security.PublicKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

public class PackageApplesFlow implements ClientStartableFlow {

    @CordaInject
    JsonMarshallingService jsonMarshallingService;

    @CordaInject
    MemberLookup memberLookup;

    @CordaInject
    NotaryLookup notaryLookup;

    @CordaInject
    UtxoLedgerService utxoLedgerService;

    public PackageApplesFlow() {}

    @Suspendable
    @Override
    public String call(ClientRequestBody requestBody) {

        PackageApplesRequest request = requestBody.getRequestBodyAs(jsonMarshallingService, PackageApplesRequest.class);
        String appleDescription = request.getAppleDescription();
        int weight = request.getWeight();

        NotaryInfo notary = notaryLookup.getNotaryServices().iterator().next();

        PublicKey myKey = memberLookup.myInfo().getLedgerKeys().get(0);

        // Building the output BasketOfApples state
        BasketOfApples basket = new BasketOfApples(
                appleDescription,
                myKey,
                myKey,
                weight,
                List.of(myKey)
        );

        // Create the transaction
        UtxoSignedTransaction transaction = utxoLedgerService.createTransactionBuilder()
                .setNotary(notary.getName())
                .addOutputState(basket)
                .addCommand(new AppleCommands.PackBasket())
                .setTimeWindowUntil(Instant.now().plus(1, ChronoUnit.DAYS))
                .addSignatories(List.of(myKey))
                .toSignedTransaction();

        try {
            // Record the transaction, no sessions are passed in as the transaction is only being
            // recorded locally
            return utxoLedgerService.finalize(transaction, Collections.emptyList()).toString();
        } catch (Exception e) {
            return String.format("Flow failed, message: %s", e.getMessage());
        }
    }
}