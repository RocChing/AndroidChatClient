package com.roc.chatclient.ui;

import android.os.Bundle;
import android.view.View;

import com.roc.chatclient.util.MFGT;
import com.roc.chatclient.util.StatusBarCompat;
import com.roc.chatclient.R;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.Utils;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityBase;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper;

public abstract class BaseEaseChatActivity extends EaseBaseActivity {
    //private SwipeBackActivityHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ExitAppUtils.getInstance().addActivity(this);
//        mHelper = new SwipeBackActivityHelper(this);
//        mHelper.onActivityCreate();
    }
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.colorPrimary));
//        bind(this);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
       // mHelper.onPostCreate();
    }

    @Override
    public View findViewById(int id) {
        View v = super.findViewById(id);
//        if (v == null && mHelper != null)
//            return mHelper.findViewById(id);
        return v;
    }

//    @Override
//    public SwipeBackLayout getSwipeBackLayout() {
//        return mHelper.getSwipeBackLayout();
//    }
//
//    @Override
//    public void setSwipeBackEnable(boolean enable) {
//        getSwipeBackLayout().setEnableGesture(enable);
//    }
//
//    @Override
//    public void scrollToFinishActivity() {
//        Utils.convertActivityToTranslucent(this);
//        getSwipeBackLayout().scrollToFinishActivity();
//    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MFGT.finish(this);
    }

}
