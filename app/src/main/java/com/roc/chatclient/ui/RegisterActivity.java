package com.roc.chatclient.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.alibaba.fastjson.JSON;
import com.roc.chatclient.MainActivity;
import com.roc.chatclient.R;
import com.roc.chatclient.model.ChatHelper;
import com.roc.chatclient.model.CmdInfo;
import com.roc.chatclient.model.CmdType;
import com.roc.chatclient.model.LoginInfo;
import com.roc.chatclient.model.UserExtInfo;
import com.roc.chatclient.receiver.IMsgCallback;
import com.roc.chatclient.receiver.MsgString;
import com.roc.chatclient.util.CommonUtils;
import com.roc.chatclient.util.MFGT;
import com.roc.chatclient.util.StringUtils;

/**
 * Description :注册
 * Author : AllenJuns
 * Date   : 2016-3-03
 */
public class RegisterActivity extends BaseActivity {
//    @Bind(R.id.txt_title)
//    TextView txt_title;
//    @Bind(R.id.img_back)
//    ImageView img_back;
//    @Bind(R.id.et_usertel)
//    EditText userNameEditText;
//    @Bind(R.id.et_password)
//    EditText passwordEditText;
//    @Bind(R.id.et_code)
//    EditText et_codeEditText;

    private int gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_register);
        super.onCreate(savedInstanceState);

        init();
    }


    public void register() {
        EditText et_name = findViewById(R.id.et_name);
        EditText et_nickName = findViewById(R.id.et_nickname);
        EditText et_phone = findViewById(R.id.et_phone);

        String name = et_name.getText().toString();
        String nickName = et_nickName.getText().toString();
        String phone = et_phone.getText().toString();

        if (StringUtils.isEmpty(name)) {
            CommonUtils.showLongToast("账号不能为空");
            return;
        }
        if (StringUtils.isEmpty(nickName)) {
            CommonUtils.showLongToast("昵称不能为空");
            return;
        }

        UserExtInfo userExtInfo = new UserExtInfo();
        userExtInfo.Name = name;
        userExtInfo.Gender = gender;
        userExtInfo.NickName = nickName;
        userExtInfo.Phone = phone;

        ChatHelper.getInstance().sendMsg(CmdType.AddUser, userExtInfo);
    }

    private void init() {
        gender = 1;
        Spinner spinner_gender = findViewById(R.id.et_gender);
        Button btnRegister = findViewById(R.id.btn_register);

        SpinnerOnItemSelectedListener spinnerOnItemSelectedListener = new SpinnerOnItemSelectedListener();
        spinner_gender.setOnItemSelectedListener(spinnerOnItemSelectedListener);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
        ChatHelper.getInstance().setMsgCallback(new IMsgCallback() {
            @Override
            public void HandleMsg(CmdInfo info, String msg) {
                CommonUtils.showLongToast("注册成功");
                MFGT.goActivity(MainActivity.class);
            }

            @Override
            public void HandleError(CmdInfo info, String msg) {
                CommonUtils.showLongToast("注册失败-" + info.Data.toString());
            }
        });
    }

    private class SpinnerOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        //private int gender;

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String item = parent.getSelectedItem().toString();
            if (item.equalsIgnoreCase("男")) {
                gender = 1;
            } else {
                gender = 0;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            gender = 1;
        }
    }
}
