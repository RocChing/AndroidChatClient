package com.roc.chatclient.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.alibaba.fastjson.JSON;
import com.roc.chatclient.model.CmdInfo;
import com.roc.chatclient.model.CmdType;
import com.roc.chatclient.receiver.SendMsgReceiver;
import com.roc.chatclient.receiver.MsgString;
import com.roc.chatclient.socket.impl.tcp.IReceiveData;
import com.roc.chatclient.socket.impl.tcp.nio.NioClient;
import com.roc.chatclient.socket.structures.BaseClient;
import com.roc.chatclient.socket.structures.BaseMessageProcessor;
import com.roc.chatclient.socket.structures.IConnectListener;
import com.roc.chatclient.socket.structures.TcpAddress;
import com.roc.chatclient.socket.structures.message.Message;
import com.roc.chatclient.util.StringUtils;

import java.util.LinkedList;

public class MsgService extends Service {

    private NioClient client;
    private String Tag = "MsgService";
    private SendMsgReceiver msgReceiver;

    public MsgService() {

    }

    @Override
    public void onCreate() {
        Log.d(Tag, "onCreate");
        msgReceiver = new SendMsgReceiver(this);
        IntentFilter intentFilter = new IntentFilter(MsgString.SendMsg);
        LocalBroadcastManager.getInstance(this).registerReceiver(msgReceiver, intentFilter);

        client = new NioClient(messageProcessor, connectListener, receiveData);
        client.setConnectAddress(new TcpAddress[]{new TcpAddress(MsgString.ServerIp, MsgString.ServerPort)});
        client.connect();
    }

    @Override
    public void onDestroy() {
        client.disconnect();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(msgReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 发送消息
     *
     * @param msg
     */
    public void sendMsg(String msg) {
        Log.d(Tag, "sendMsg:" + msg);
        if (StringUtils.isEmpty(msg)) return;
        msg += "\r\n";

        messageProcessor.send(client, msg.getBytes());
    }

    public void sendMsg(CmdInfo info) {
        String json = JSON.toJSONString(info);
        json += "\r\n";
        messageProcessor.send(client, json.getBytes());
    }

    private IReceiveData receiveData = new IReceiveData() {
        @Override
        public void HandleMsg(Message msg) {
            String json = new String(msg.data, msg.offset, msg.length);

            Log.d(Tag, "receiveMsg:" + json);

            Intent intent = new Intent();
            intent.setAction(MsgString.ReceiveMsg);
            intent.putExtra(MsgString.Default_Args, json);
            LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
        }
    };

    private BaseMessageProcessor messageProcessor = new BaseMessageProcessor() {
        @Override
        public void onReceiveMessages(BaseClient mClient, LinkedList<Message> mQueen) {

        }
    };

    private IConnectListener connectListener = new IConnectListener() {
        @Override
        public void onConnectionSuccess() {
            Log.i(Tag, "connected server success");
        }

        @Override
        public void onConnectionFailed() {
            Log.i(Tag, "connected server failed");
        }
    };
}
