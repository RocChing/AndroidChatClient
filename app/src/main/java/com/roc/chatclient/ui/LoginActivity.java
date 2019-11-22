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
import com.roc.chatclient.util.CommonUtils;

public class LoginActivity extends AppCompatActivity {
    private String Tag = "LoginActivity";

    private ReceiveMsgReceiver msgReceiver;

    private ProgressBar progressBar;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }

    public void btnLoginClick(View view) {
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

    public void btnWentiClick(View view) {
        CommonUtils.showLongToast(getString(R.string.login_txt_answer));
    }

    private void init() {
        EditText password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.login);
        progressBar = findViewById(R.id.loading);

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
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                CommonUtils.showLongToast(info.getDataJson());
            }

            @Override
            public void HandleError(CmdInfo info, String msg) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                CommonUtils.showLongToast(info.Data.toString());
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
