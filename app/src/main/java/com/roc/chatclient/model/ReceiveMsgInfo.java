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
     * 发送时间
     */
    public String ReceiveTime;

    public int To;

    public int ToType;

    /**
     * 信息类型
     */
    public int Type;

    public ReceiveMsgInfo() {
    }
}
