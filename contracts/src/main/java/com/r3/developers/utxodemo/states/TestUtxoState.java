package com.r3.developers.utxodemo.states;

import com.r3.developers.utxodemo.contracts.TestContract;
import net.corda.v5.ledger.utxo.BelongsToContract;
import net.corda.v5.ledger.utxo.ContractState;
import java.security.PublicKey;
import java.util.List;

@BelongsToContract(TestContract.class)
public class TestUtxoState {
    public TestUtxoState() {
    }

    public TestUtxoState(String input, List<PublicKey> participants ) {
        this.input = input;
        this.participants = participants;
    }

    public List<PublicKey> getParticipants() {
        return participants;
    }

    public void setParticipants(List<PublicKey> participants) {
        this.participants = participants;
    }

    public String input;
    private List<PublicKey> participants;
}
