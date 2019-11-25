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
import com.roc.chatclient.entity.User;
import com.roc.chatclient.model.ChatHelper;
import com.roc.chatclient.model.CmdInfo;
import com.roc.chatclient.model.CmdType;
import com.roc.chatclient.model.LoginInfo;
import com.roc.chatclient.model.UserExtInfo;
import com.roc.chatclient.receiver.IMsgCallback;
import com.roc.chatclient.receiver.MsgString;
import com.roc.chatclient.receiver.ReceiveMsgReceiver;
import com.roc.chatclient.util.CommonUtils;
import com.roc.chatclient.util.MFGT;
import com.roc.chatclient.util.PreferenceManager;

public class LoginActivity extends AppCompatActivity {
    private String Tag = "LoginActivity";

    //private ReceiveMsgReceiver msgReceiver;

    private ProgressBar progressBar;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        PreferenceManager preferenceManager = PreferenceManager.getInstance();
        Log.d(Tag, "get user name from preferenceManager is :" + preferenceManager.getCurrentUsername());
    }

    public void btnLoginClick(View view) {
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        EditText userName = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);
        String name = userName.getText().toString().trim();
        String pwd = password.getText().toString().trim();

        ChatHelper.getInstance().sendMsg(CmdType.Login, new LoginInfo(name, pwd));
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

        ChatHelper.getInstance().setMsgCallback(new IMsgCallback() {
            @Override
            public void HandleMsg(CmdInfo info, String msg) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                //CommonUtils.showLongToast(info.getDataJson());
                PreferenceManager preferenceManager = PreferenceManager.getInstance();
                UserExtInfo user = info.of(UserExtInfo.class);
                preferenceManager.setCurrentUser(user);

//                Intent intent = new Intent(getBaseContext(), HomeActivity.class);
//                startActivity(intent);
//                finish();
                MFGT.goActivity(HomeActivity.class);
            }

            @Override
            public void HandleError(CmdInfo info, String msg) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                CommonUtils.showLongToast(info.Data.toString());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
