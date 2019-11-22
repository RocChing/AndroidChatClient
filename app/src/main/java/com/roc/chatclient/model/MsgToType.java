package com.roc.chatclient.model;

public enum MsgToType {
    User(1), Group(2), System(3);

    private int value;

    private MsgToType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static MsgToType getType(int value) {
        for (MsgToType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
