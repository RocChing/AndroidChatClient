package com.roc.chatclient.ui;

import androidx.appcompat.app.AppCompatActivity;

import com.roc.chatclient.R;
import com.roc.chatclient.adapter.MainTabAdpter;
import com.roc.chatclient.ui.fragment.ConversationListFragment;
import com.roc.chatclient.util.CommonUtils;
import com.roc.chatclient.util.MFGT;
import com.roc.chatclient.widget.DMTabHost;
import com.roc.chatclient.widget.MFViewPager;

import android.os.Bundle;
import android.view.KeyEvent;

import java.util.Timer;
import java.util.TimerTask;

public class HomeActivity extends AppCompatActivity {

    private MainTabAdpter adapter;
    private int keyBackClickCount = 0;
    private int PageIndex = 0;
    private DMTabHost mHost;
    private MFViewPager viewpager;
    //private TitlePopup titlePopup;
//    private ConversationListFragment conversationListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mHost = findViewById(R.id.tab_host);
        viewpager = findViewById(R.id.viewpager);

        initMainTab();
    }

    private void initMainTab() {

        mHost.setChecked(0);
        adapter = new MainTabAdpter(getSupportFragmentManager());
        viewpager.setAdapter(adapter);
        adapter.clear();
//        viewpager.setScrollble(false);
        viewpager.setOffscreenPageLimit(4);
        adapter.addFragment(new ConversationListFragment(), getString(R.string.app_name));
//        adapter.addFragment(new ContactListFragment(), getString(R.string.contacts));
//        adapter.addFragment(new Fragment_Dicover(), getString(R.string.discover));
//        adapter.addFragment(new Fragment_Profile(), getString(R.string.me));
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            switch (keyBackClickCount++) {
                case 0:
                    CommonUtils.showLongToast(getString(R.string.key_back_msg));
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            keyBackClickCount = 0;
                        }
                    }, 3000);
                    break;
                case 1:
                    MFGT.finishFormBottom(this);
                    break;
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
