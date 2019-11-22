package com.roc.chatclient;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.roc.chatclient.service.MsgService;

public class ChatApplication extends Application {

    private static ChatApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        Intent intent = new Intent(this, MsgService.class);
        startService(intent);
    }

    public static ChatApplication getInstance() {
        return instance;
    }
}
