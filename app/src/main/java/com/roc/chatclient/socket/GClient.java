package com.roc.chatclient.socket;

import com.roc.chatclient.socket.structures.message.MessageBuffer;
import com.roc.chatclient.socket.structures.pools.MessagePool;

/**
 * author       :   long
 * created on   :   2017/11/30
 * description  :   客户端全局数据
 */

public final class GClient {

    private static boolean isInitialized = false;

    public static final void init() {
        if (!isInitialized) {
            MessagePool.init(10);
            MessageBuffer.init(8 * MessageBuffer.KB, 100 * MessageBuffer.KB, 2 * MessageBuffer.MB, 5, 2, 1, 2);
            isInitialized = true;
        }
    }
}
