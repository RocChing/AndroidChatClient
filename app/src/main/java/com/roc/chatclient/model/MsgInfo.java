package com.roc.chatclient.model;

public class MsgInfo {
    public String Msg;

    public int Type;

    public int From;

    public int To;

    public int ToType;

    public MsgInfo(String msg, MsgType type, int from, int to, MsgToType toType) {
        Msg = msg;
        Type = type.getValue();
        From = from;
        To = to;
        ToType = toType.getValue();
    }
}
