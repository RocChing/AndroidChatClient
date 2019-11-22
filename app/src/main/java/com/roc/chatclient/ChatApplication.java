package com.roc.chatclient;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.roc.chatclient.service.MsgService;

public class ChatApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("ChatApplication", "onCreate");
        Intent intent = new Intent(this, MsgService.class);
        startService(intent);
    }
}
