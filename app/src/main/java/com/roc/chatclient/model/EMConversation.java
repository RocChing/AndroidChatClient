package com.roc.chatclient.model;

public class EMConversation {

    private String name;

    private String nickName;

    private int toType;

    private int unreadMsgCount;

    private int allMsgCount;

    private int chatId;

    private ReceiveMsgInfo lastMsg;

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getToType() {
        return toType;
    }

    public void setToType(int toType) {
        this.toType = toType;
    }

    public int getUnreadMsgCount() {
        return unreadMsgCount;
    }

    public void setUnreadMsgCount(int unreadMsgCount) {
        this.unreadMsgCount = unreadMsgCount;
    }

    public int getAllMsgCount() {
        return allMsgCount;
    }

    public void setAllMsgCount(int allMsgCount) {
        this.allMsgCount = allMsgCount;
    }

    public ReceiveMsgInfo getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(ReceiveMsgInfo lastMsg) {
        this.lastMsg = lastMsg;
    }
}
