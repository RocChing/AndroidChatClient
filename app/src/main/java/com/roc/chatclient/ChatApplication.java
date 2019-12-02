package com.roc.chatclient;

import android.app.Application;
import android.content.Intent;
import android.os.StrictMode;
import android.util.Log;

import com.roc.chatclient.model.ChatHelper;
import com.roc.chatclient.service.MsgService;
import com.roc.chatclient.util.FinishActivityManager;

public class ChatApplication extends Application {

    private static ChatApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        //解决拍照问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        Intent intent = new Intent(this, MsgService.class);
        startService(intent);

        ChatHelper.getInstance().init(this);
    }

    public static ChatApplication getInstance() {
        return instance;
    }
}
