package com.roc.chatclient.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.roc.chatclient.MainActivity;
import com.roc.chatclient.R;
import com.roc.chatclient.db.DbManager;
import com.roc.chatclient.model.ChatHelper;
import com.roc.chatclient.util.FinishActivityManager;
import com.roc.chatclient.util.PreferenceManager;

public class SettingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
    }

    public void btnExitClick(View view) {
        ChatHelper.getInstance().resetData();

        Intent intent = new Intent(this, MainActivity.class);
        FinishActivityManager.getManager().finishAllActivity();
        startActivity(intent);
        finish();
    }
}
