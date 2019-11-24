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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.roc.chatclient.R;
import com.roc.chatclient.model.ChatHelper;
import com.roc.chatclient.model.CmdInfo;
import com.roc.chatclient.model.CmdType;
import com.roc.chatclient.model.LoginInfo;
import com.roc.chatclient.model.UserExtInfo;
import com.roc.chatclient.receiver.IMsgCallback;
import com.roc.chatclient.receiver.MsgString;
import com.roc.chatclient.receiver.ReceiveMsgReceiver;
import com.roc.chatclient.util.CommonUtils;
import com.roc.chatclient.widget.EaseContactList;

import java.util.ArrayList;
import java.util.List;

public class NewFriendsMsgActivity extends BaseActivity {

    private EditText query;
    private ImageButton search_clear;

    private EaseContactList contactListLayout;
    private ListView listView;

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

        contactListLayout = findViewById(R.id.contact_list);
        listView = contactListLayout.getListView();

        ChatHelper.getInstance().setMsgCallback(new IMsgCallback() {
            @Override
            public void HandleMsg(CmdInfo info, String msg) {
                Log.d("NewFriend", msg);
                List<UserExtInfo> list = info.ofList(UserExtInfo.class);
                contactListLayout.init(list);
            }

            @Override
            public void HandleError(CmdInfo info, String msg) {
                CommonUtils.showLongToast("发生错误-" + msg);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserExtInfo user = (UserExtInfo) listView.getItemAtPosition(position);
                boolean flag = ChatHelper.getInstance().getModel().saveContact(user);
                if (flag) {
                    finish();
                }
                else {
                    CommonUtils.showLongToast("联系人添加失败");
                }
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
            query.getText().clear();
            contactListLayout.init(new ArrayList<UserExtInfo>());
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
