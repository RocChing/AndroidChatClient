package com.roc.chatclient.socket.impl.tcp;


import com.roc.chatclient.socket.structures.message.Message;

public interface IReceiveData {
    void HandleMsg(Message msg);
}
