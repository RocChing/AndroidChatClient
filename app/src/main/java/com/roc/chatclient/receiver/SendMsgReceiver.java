package com.roc.chatclient.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.roc.chatclient.service.MsgService;
import com.roc.chatclient.util.StringUtils;

public class SendMsgReceiver extends BroadcastReceiver {

    private MsgService msgService;

    public SendMsgReceiver(MsgService msgService) {
        this.msgService = msgService;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (!action.equalsIgnoreCase(MsgString.SendMsg)) return;

        String json = intent.getStringExtra(MsgString.Default_Args);
//        Log.d("aaa", json);
        if (!StringUtils.isEmpty(json)) {
            msgService.sendMsg(json);
        }
    }
}
