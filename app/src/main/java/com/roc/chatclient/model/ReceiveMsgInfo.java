package com.roc.chatclient.model;

public class ReceiveMsgInfo {
    /**
     * 发送者信息
     */
    public UserInfo From;

    /**
     * 信息
     */
    public String Msg;

    /**
     * 信息类型
     */
    public MsgType Type;

    public ReceiveMsgInfo() {
    }
}
