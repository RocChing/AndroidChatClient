package com.roc.chatclient.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.roc.chatclient.model.CmdInfo;
import com.roc.chatclient.model.CmdType;
import com.roc.chatclient.util.StringUtils;

public class ReceiveMsgReceiver extends BroadcastReceiver {

    private IMsgCallback msgCallback;

    private IMsgCallback sendMsgCallback;

    private IMsgCallback checkMsgCallback;

    public ReceiveMsgReceiver() {

    }

    public ReceiveMsgReceiver(IMsgCallback callback) {
        this.setMsgCallback(callback);
    }

    public void setMsgCallback(IMsgCallback callback) {
        this.msgCallback = callback;
    }

    public void setSendMsgCallback(IMsgCallback sendMsgCallback) {
        this.sendMsgCallback = sendMsgCallback;
    }

    public void setCheckMsgCallback(IMsgCallback checkMsgCallback) {
        this.checkMsgCallback = checkMsgCallback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String msg = intent.getStringExtra(MsgString.Default_Args);

        if (action.equals(MsgString.CheckMsg)) {
            if (checkMsgCallback != null) {
                checkMsgCallback.HandleMsg(null, msg);
            }
            return;
        }

        if (!action.equals(MsgString.ReceiveMsg)) return;

        CmdInfo info = JSON.parseObject(msg, CmdInfo.class);
        CmdType type = CmdType.getType(info.Type);
        switch (type) {
            case Error:
                if (msgCallback != null) {
                    msgCallback.HandleError(info, msg);
                }
                if (sendMsgCallback != null) {
                    sendMsgCallback.HandleError(info, msg);
                }
                break;
            case SendMsg:
                if (sendMsgCallback != null) {
                    sendMsgCallback.HandleMsg(info, msg);
                }
                break;
            default:
                if (msgCallback != null) {
                    msgCallback.HandleMsg(info, msg);
                }
                break;
        }
    }
}
