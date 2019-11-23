package com.roc.chatclient.event;

public interface EventListener
{
    public void handleMessage(int what, int arg1, int arg2, Object dataobj);
}
