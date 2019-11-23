package com.roc.chatclient.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.roc.chatclient.MainActivity;
import com.roc.chatclient.R;
import com.roc.chatclient.util.PreferenceManager;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
    }

    public void btnExitClick(View view) {
        PreferenceManager.getInstance().removeCurrentUserInfo();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
