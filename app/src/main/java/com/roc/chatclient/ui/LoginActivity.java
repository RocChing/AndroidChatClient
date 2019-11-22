package com.roc.chatclient.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.roc.chatclient.R;
import com.roc.chatclient.model.CmdInfo;
import com.roc.chatclient.model.CmdType;
import com.roc.chatclient.model.LoginInfo;
import com.roc.chatclient.receiver.IMsgCallback;
import com.roc.chatclient.receiver.MsgString;
import com.roc.chatclient.receiver.ReceiveMsgReceiver;

public class LoginActivity extends AppCompatActivity {
    private String Tag = "LoginActivity";

    private ReceiveMsgReceiver msgReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }

    public void btnLoginClick(View view) {
        final Button btnLogin = findViewById(R.id.login);
        ProgressBar progressBar = findViewById(R.id.loading);
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        String validString = getString(R.string.validString);
        EditText userName = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);
        String name = userName.getText().toString().trim();
        String pwd = password.getText().toString().trim();

        String json = JSON.toJSONString(new CmdInfo(validString, CmdType.Login, new LoginInfo(name, pwd)));

        Intent intent = new Intent();
        intent.setAction(MsgString.Login);
        intent.putExtra(MsgString.Login_Args, json);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void init() {
        EditText password = findViewById(R.id.password);
        final Button btnLogin = findViewById(R.id.login);
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnLogin.setEnabled(s.length() > 3);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        msgReceiver = new ReceiveMsgReceiver(new IMsgCallback() {
            @Override
            public void HandleMsg(CmdInfo info, String msg) {
                Log.i(Tag, "success:" + msg);
                Toast.makeText(getBaseContext(), info.Data.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void HandleError(CmdInfo info, String msg) {
                Log.e(Tag, "error:" + msg);
                Toast.makeText(getBaseContext(), info.Data.toString(), Toast.LENGTH_LONG).show();
            }
        });

        IntentFilter filter = new IntentFilter(MsgString.ReceiveMsg);
        LocalBroadcastManager.getInstance(this).registerReceiver(msgReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (msgReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(msgReceiver);
        }
    }
}
