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

    public ReceiveMsgReceiver() {

    }

    public ReceiveMsgReceiver(IMsgCallback callback) {
        this.setMsgCallback(callback);
    }

    public void setMsgCallback(IMsgCallback callback) {
        this.msgCallback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (!action.equals(MsgString.ReceiveMsg)) return;

        String msg = intent.getStringExtra(MsgString.Default_Args);
        Log.d("tag1", msg);
        CmdInfo info = JSON.parseObject(msg, CmdInfo.class);
        CmdType type = CmdType.getType(info.Type);
        switch (type) {
            case Login:
                if (msgCallback != null) {
                    msgCallback.HandleMsg(info, msg);
                }
                break;
            case Error:
                if (msgCallback != null) {
                    Log.d("aaa", "ssssss");
                    msgCallback.HandleError(info, msg);
                }
                break;
        }
//        switch (action) {
//            case MsgString.Error:
//                if (msgCallback != null) {
//                    msgCallback.HandleError(msg);
//                }
//                break;
//            default:
//                if (msgCallback != null) {
//                    msgCallback.HandleMsg(msg);
//                }
//                break;
//        }
    }
}
