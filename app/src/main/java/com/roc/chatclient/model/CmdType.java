package com.roc.chatclient.model;

public enum CmdType {
    Login(1), SendMsg(2), SearchUser(3), AddUser(4), LoginById(5), Error(0), Info(100);

    private int value;

    private CmdType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static CmdType getType(int value) {
        for (CmdType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
