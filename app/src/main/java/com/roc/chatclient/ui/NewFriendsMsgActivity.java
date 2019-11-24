package com.roc.chatclient.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.roc.chatclient.R;
import com.roc.chatclient.model.ChatHelper;
import com.roc.chatclient.model.CmdInfo;
import com.roc.chatclient.model.CmdType;
import com.roc.chatclient.model.LoginInfo;
import com.roc.chatclient.receiver.IMsgCallback;
import com.roc.chatclient.receiver.MsgString;
import com.roc.chatclient.receiver.ReceiveMsgReceiver;

public class NewFriendsMsgActivity extends AppCompatActivity {

    private EditText query;
    private ImageButton search_clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friends_msg);

        init();
    }

    private void init() {
        TextView txt_title = findViewById(R.id.txt_title);
        String title = getString(R.string.recommended_friends);
        txt_title.setText(title);

        query = findViewById(R.id.query);
        search_clear = findViewById(R.id.search_clear);

        query.addTextChangedListener(queryTextChangedListener);
        search_clear.setOnClickListener(searchClearOnClickListener);

        ChatHelper.getInstance().setMsgCallback(new IMsgCallback() {
            @Override
            public void HandleMsg(CmdInfo info, String msg) {
                Log.d("NewFriend", msg);
            }

            @Override
            public void HandleError(CmdInfo info, String msg) {

            }
        });
    }

    private TextWatcher queryTextChangedListener = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() > 0) {
                search_clear.setVisibility(View.VISIBLE);
            } else {
                search_clear.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() < 3) return;

            String validString = getString(R.string.validString);
            String json = JSON.toJSONString(new CmdInfo(validString, CmdType.SearchUser, s.toString()));

            Intent intent = new Intent();
            intent.setAction(MsgString.Login);
            intent.putExtra(MsgString.Login_Args, json);
            LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
    };

    private View.OnClickListener searchClearOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("NewFriend", "the searchClearOnClickListener click");
            query.getText().clear();
            hideSoftKeyboard();
        }
    };

    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
