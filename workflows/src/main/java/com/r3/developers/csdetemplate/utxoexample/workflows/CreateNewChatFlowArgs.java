package com.r3.developers.csdetemplate.utxoexample.workflows;

public class CreateNewChatFlowArgs{

    public CreateNewChatFlowArgs() {}
    public CreateNewChatFlowArgs(String chatName, String message, String otherMember) {
        this.chatName = chatName;
        this.message = message;
        this.otherMember = otherMember;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOtherMember() {
        return otherMember;
    }

    public void setOtherMember(String otherMember) {
        this.otherMember = otherMember;
    }

    private String chatName;
    private String message;


    private String otherMember;
}