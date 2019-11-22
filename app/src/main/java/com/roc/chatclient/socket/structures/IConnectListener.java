package com.roc.chatclient.socket.structures;

/**
 * author       :   long
 * created on   :   2017/11/30
 * description  :   连接状态回调
 */

public interface IConnectListener {

    void onConnectionSuccess();

    void onConnectionFailed();
}
