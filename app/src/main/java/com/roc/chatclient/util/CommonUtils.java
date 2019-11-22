package com.roc.chatclient.util;

import android.widget.Toast;

import com.roc.chatclient.ChatApplication;

public final class CommonUtils {
    public static void showLongToast( String pMsg) {
        Toast.makeText(ChatApplication.getInstance(), pMsg, Toast.LENGTH_LONG).show();
    }

    public static void showShortToast(String pMsg) {
        Toast.makeText(ChatApplication.getInstance(), pMsg, Toast.LENGTH_SHORT).show();
    }
}
