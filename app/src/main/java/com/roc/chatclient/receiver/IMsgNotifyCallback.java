package com.roc.chatclient.receiver;

import com.roc.chatclient.entity.Msg;

public interface IMsgNotifyCallback {
    void HandleMsg(Msg msg, String originJson);
}
