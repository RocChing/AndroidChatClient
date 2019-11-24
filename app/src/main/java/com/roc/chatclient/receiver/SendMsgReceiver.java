package com.roc.chatclient.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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
        String json = "";
        switch (action) {
            case MsgString.Login:
                json = intent.getStringExtra(MsgString.Login_Args);
                break;
            case MsgString.SearchUser:
                json = intent.getStringExtra(MsgString.Default_Args);
                break;
        }
        if (!StringUtils.isEmpty(json)) {
            msgService.sendMsg(json);
        }
    }
}
