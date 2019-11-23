package com.roc.chatclient.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.roc.chatclient.model.Constant;
import com.roc.chatclient.util.MFGT;
import com.roc.chatclient.widget.EmptyLayout;

import com.roc.chatclient.R;

/**
 * Description :
 * Author : AllenJuns
 * Date   : 2016-3-08
 */
public class CommonActivity extends AppCompatActivity {

    private TextView txt_title;
    private ImageView img_back;
    private View layout;
    private EmptyLayout emptyLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_common);
        super.onCreate(savedInstanceState);
    }


    protected void findView() {
        emptyLayout = new EmptyLayout(this, layout);
        emptyLayout.showEmpty();
    }


    protected void initView() {
        img_back.setVisibility(View.VISIBLE);
        String titile = getIntent().getStringExtra(Constant.TITLE);
        txt_title.setText(titile);
    }



    public void btnBack(View view) {
        MFGT.finish(this);
    }
}
