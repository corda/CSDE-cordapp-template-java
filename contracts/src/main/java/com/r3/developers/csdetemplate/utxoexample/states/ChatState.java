package com.r3.developers.csdetemplate.utxoexample.states;

import com.r3.developers.csdetemplate.utxoexample.contracts.ChatContract;
import net.corda.v5.base.types.MemberX500Name;
import net.corda.v5.ledger.utxo.BelongsToContract;
import net.corda.v5.ledger.utxo.ContractState;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.util.*;

/*
@BelongsToContract(ChatContract::class)
data class ChatState(
        val id : UUID = UUID.randomUUID(),
        val chatName: String,
        val messageFrom: MemberX500Name,
        val message: String,
        override val participants: List<PublicKey>) : ContractState {

        fun updateMessage(messageFrom: MemberX500Name, message: String) = copy(messageFrom = messageFrom, message = message)
        }

*/

@BelongsToContract(ChatContract.class)
public class ChatState implements ContractState {
    public ChatState() {
    }

    public ChatState(UUID id,
             String chatName,
             MemberX500Name messageFrom,
             String message,
             List<PublicKey> participants
              ) {
        this.id = id;
        this.chatName = chatName;
        this.messageFrom = messageFrom;
        this.message = message;
        this.participants = participants;
    }

    public ChatState(String chatName,
              MemberX500Name messageFrom,
              String message,
              List<PublicKey> participants
    ) {
        this(UUID.randomUUID(),
                chatName,
                messageFrom,
                message,
                participants);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public MemberX500Name getMessageFrom() {
        return messageFrom;
    }

    public void setMessageFrom(MemberX500Name messageFrom) {
        this.messageFrom = messageFrom;
    }

    public String getMessage() {
        return message;
    }

    @NotNull
    @Override
    public List<PublicKey> getParticipants() {
        return participants;
    }

    public void setParticipants(List<PublicKey> participants) {
        this.participants = participants;
    }

    private UUID id;
    private String chatName;
    private MemberX500Name messageFrom;
    private String message;
    List<PublicKey> participants;

    public ChatState updateMessage(MemberX500Name name, String message) {
        return new ChatState(chatName, name, message, participants);
    }
}