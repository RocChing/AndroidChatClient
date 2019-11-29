package com.roc.chatclient.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.alibaba.fastjson.JSON;
import com.roc.chatclient.R;
import com.roc.chatclient.model.CmdInfo;
import com.roc.chatclient.model.CmdType;
import com.roc.chatclient.model.Constant;
import com.roc.chatclient.receiver.SendMsgReceiver;
import com.roc.chatclient.receiver.MsgString;
import com.roc.chatclient.socket.impl.tcp.IReceiveData;
import com.roc.chatclient.socket.impl.tcp.nio.NioClient;
import com.roc.chatclient.socket.structures.BaseClient;
import com.roc.chatclient.socket.structures.BaseMessageProcessor;
import com.roc.chatclient.socket.structures.IConnectListener;
import com.roc.chatclient.socket.structures.TcpAddress;
import com.roc.chatclient.socket.structures.message.Message;
import com.roc.chatclient.util.PreferenceManager;
import com.roc.chatclient.util.StringUtils;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class MsgService extends Service {

    private NioClient client;
    private String Tag = "MsgService";
    private SendMsgReceiver msgReceiver;

    private String validString;

    private Timer timer;

    public MsgService() {

    }

    @Override
    public void onCreate() {
        Log.d(Tag, "onCreate");
        validString = getString(R.string.validString);
        msgReceiver = new SendMsgReceiver(this);
        IntentFilter intentFilter = new IntentFilter(MsgString.SendMsg);
        LocalBroadcastManager.getInstance(this).registerReceiver(msgReceiver, intentFilter);

        client = new NioClient(messageProcessor, connectListener, receiveData);
        client.setConnectAddress(new TcpAddress[]{new TcpAddress(MsgString.ServerIp, MsgString.ServerPort)});
        client.connect();

        initTimer();
    }

    private void initTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!client.isConnected()) {
                    client.connect();
                    return;
                }
                sendCheckMsg();
            }
        }, 1000, 60000);
    }

    @Override
    public void onDestroy() {
        client.disconnect();
        timer = null;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(msgReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 发送消息
     *
     * @param json
     */
    public void sendMsg(String json) {
        Log.d(Tag, "sendMsg:" + json);
        if (StringUtils.isEmpty(json)) return;
        json += "\r\n";

        messageProcessor.send(client, json.getBytes());
    }

    public void sendMsg(CmdInfo info) {
        String json = JSON.toJSONString(info);
        Log.d(Tag, "sendMsg:" + json);
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
            sendLocalCheckMsg(Constant.SUCCESS);
        }

        @Override
        public void onConnectionFailed() {
            Log.i(Tag, "connected server failed");
            sendLocalCheckMsg(Constant.FAILED);
        }
    };

    private void sendCheckMsg() {
        int id = PreferenceManager.getInstance().getCurrentUserId();
        String validString = getString(R.string.validString);
        sendMsg(new CmdInfo(validString, CmdType.Check, id));
    }

    private void sendLocalCheckMsg(String result) {
        Intent intent = new Intent();
        intent.setAction(MsgString.CheckMsg);
        intent.putExtra(MsgString.Default_Args, result);
        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
    }
}
