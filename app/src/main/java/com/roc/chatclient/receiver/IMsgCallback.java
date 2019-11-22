package com.roc.chatclient.receiver;

import com.roc.chatclient.model.CmdInfo;

public interface IMsgCallback {
    void HandleMsg(CmdInfo info, String msg);

    void HandleError(CmdInfo info, String msg);
}
