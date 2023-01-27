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

//@CordaSerializable
@BelongsToContract(ChatContract.class)
public class ChatState implements ContractState {

    private UUID id;
    private String chatName;
    private MemberX500Name messageFrom;
    private String message;
    public List<PublicKey> participants;

//    public ChatState() {        // todo why do we need this?
//    }

    // Allows serialisation and to use a specified UUID.
    @ConstructorForDeserialization
    public ChatState(UUID id,
                     String chatName,
                     MemberX500Name messageFrom,
                     String message,
                     List<PublicKey> participants) {
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
                     List<PublicKey> participants) {
        this(UUID.randomUUID(),
                chatName,
                messageFrom,
                message,
                participants);
    }

    public UUID getId() {
        return id;
    }
    public String getChatName() {
        return chatName;
    }
    public MemberX500Name getMessageFrom() {
        return messageFrom;
    }
    public String getMessage() {
        return message;
    }
//    @NotNull
//    @Override
    public List<PublicKey> getParticipants() {
        return participants;
    }


    public ChatState updateMessage(MemberX500Name name, String message) {
        return new ChatState(id, chatName, name, message, participants);
    }
//
//    // todo: why is this overridden
//    @Override
//    public String toString() {
//        return ChatState.class.getName() +
//                "(id=" + id +
//                ", chatName=" + chatName +
//                ", messageFrom=" + messageFrom +
//                ", message=" + message +
//                ", participants=" + participants +
//                ")";
//    }
}