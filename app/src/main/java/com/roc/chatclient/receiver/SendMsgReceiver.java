package com.roc.chatclient.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.roc.chatclient.service.MsgService;

public class SendMsgReceiver extends BroadcastReceiver {

    private MsgService msgService;

    public SendMsgReceiver(MsgService msgService) {
        this.msgService = msgService;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case MsgString.Login:
                String json = intent.getStringExtra(MsgString.Login_Args);
                msgService.sendMsg(json);
                break;
        }
    }
}
