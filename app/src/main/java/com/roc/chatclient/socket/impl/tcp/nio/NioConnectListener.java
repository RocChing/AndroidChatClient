package com.roc.chatclient.socket.impl.tcp.nio;

import com.roc.chatclient.socket.impl.tcp.nio.processor.NioReadWriteProcessor;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * author       :   long
 * created on   :   2017/11/30
 * description  :   连接状态回调
 */

public interface NioConnectListener {

    void onConnectSuccess(NioReadWriteProcessor mSocketProcessor, SocketChannel socketChannel) throws IOException;

    void onConnectFailed(NioReadWriteProcessor mSocketProcessor);

}