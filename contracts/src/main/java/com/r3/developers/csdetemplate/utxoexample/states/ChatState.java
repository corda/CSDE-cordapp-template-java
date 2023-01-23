package com.r3.developers.csdetemplate.utxoexample.states;

import com.r3.developers.csdetemplate.utxoexample.contracts.ChatContract;
import net.corda.v5.base.annotations.ConstructorForDeserialization;
import net.corda.v5.base.annotations.CordaSerializable;
import net.corda.v5.base.types.MemberX500Name;
import net.corda.v5.ledger.utxo.BelongsToContract;
import net.corda.v5.ledger.utxo.ContractState;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.util.*;

@CordaSerializable
@BelongsToContract(ChatContract.class)
public class ChatState implements ContractState {
    public ChatState() {
    }

    // Allows serialisation and to use a specified UUID.
    @ConstructorForDeserialization
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

    // Convenience constructor for initial ChatState objects that need a new UUID generated.
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

    public void setMessage(String message) {
        this.message = message;
    }

    @NotNull
    @Override
    public List<PublicKey> getParticipants() {
        return participants;
    }

    public void setParticipants(List<PublicKey> participants) {
        this.participants = participants;
    }

    public UUID id;
    public String chatName;
    public MemberX500Name messageFrom;
    public String message;
    public List<PublicKey> participants;

    public ChatState updateMessage(MemberX500Name name, String message) {
        return new ChatState(id, chatName, name, message, participants);
    }

    @Override
    public String toString() {
        return ChatState.class.getName() +
                "(id=" + id +
                ", chatName=" + chatName +
                ", messageFrom=" + messageFrom +
                ", message=" + message +
                ", participants=" + participants +
                ")";
    }
}