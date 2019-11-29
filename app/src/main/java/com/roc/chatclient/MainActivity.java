package com.roc.chatclient;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.roc.chatclient.model.ChatHelper;
import com.roc.chatclient.model.CmdType;
import com.roc.chatclient.model.Constant;
import com.roc.chatclient.ui.HomeActivity;
import com.roc.chatclient.ui.LoginActivity;
import com.roc.chatclient.ui.RegisterActivity;
import com.roc.chatclient.util.MFGT;
import com.roc.chatclient.util.PreferenceManager;

public class MainActivity extends AppCompatActivity {

    private String Tag = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        boolean isLogin = PreferenceManager.getInstance().isLogin();
        if (isLogin) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        }

        ImageView registerBtn = findViewById(R.id.img_register);

        String deviceId = getDeviceId();
        if (hasPermission(deviceId)) {
            registerBtn.setVisibility(View.VISIBLE);
        } else {
            registerBtn.setVisibility(View.GONE);
        }

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    public void toLoginView(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private String getDeviceId() {
        String DEVICE_ID = "";
        checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE);
        try {
            final TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            DEVICE_ID = manager.getDeviceId();
        } catch (Exception e) {
            Log.e(Tag, e.getMessage());
        }
        Log.d(Tag, "the device id is:" + DEVICE_ID);
        return DEVICE_ID;
    }

    private boolean hasPermission(String deviceId) {
        String[] deviceIds = Constant.DEVICEIDS;
        for (String d : deviceIds) {
            if (d.equalsIgnoreCase(deviceId)) {
                return true;
            }
        }
        return false;
    }
}
