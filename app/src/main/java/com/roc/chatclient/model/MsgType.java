package com.roc.chatclient.model;

public enum MsgType {
    Text(1), Image(2), Voice(3), Video(4), Link(5);
    private int value;

    private MsgType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static MsgType getType(int value) {
        for (MsgType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
