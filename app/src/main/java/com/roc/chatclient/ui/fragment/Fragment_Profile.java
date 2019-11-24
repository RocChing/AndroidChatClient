package com.roc.chatclient.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.roc.chatclient.R;
import com.roc.chatclient.entity.User;
import com.roc.chatclient.model.UserExtInfo;
import com.roc.chatclient.ui.SettingActivity;
import com.roc.chatclient.util.PreferenceManager;

public class Fragment_Profile extends BaseFragment {

    @Override
    public int getRootLayoutId() {
        return R.layout.fragment_profile;
    }

    @Override
    protected void initView(View view) {

    }

    @Override
    protected void initData(View view) {
        UserExtInfo user = PreferenceManager.getInstance().getCurrentUser();
        if (user != null) {
            TextView tvname = view.findViewById(R.id.tvname);
            tvname.setText(user.NickName);

            TextView tvmsg = view.findViewById(R.id.tvmsg);
            tvmsg.setText(String.format("微信号：%s", user.Name));

            ImageView iv_sex = view.findViewById(R.id.iv_sex);
            if (user.Gender == 0) {
                iv_sex.setImageResource(R.drawable.ic_sex_female);
            } else {
                iv_sex.setImageResource(R.drawable.ic_sex_male);
            }
        }
    }

    @Override
    protected void setListener(View view) {
        TextView setting = view.findViewById(R.id.txt_setting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setting_Click();
            }
        });
    }


    //    //相册按钮点击事件
//
//    public void txt_album_Click() {
//        MFGT.gotoCommon(getActivity(), getString(R.string.profile_txt_album));
//    }
//
//    //收藏按钮点击事件
//    @OnClick(R.id.txt_collect)
//    public void txt_collect_Click() {
//        MFGT.gotoCommon(getActivity(), getString(R.string.profile_txt_collect));
//    }
//
//    //钱包按钮点击事件
//    @OnClick(R.id.txt_money)
//    public void txt_money_Click() {
//        MFGT.gotoWalletActivity(getActivity() );
//    }
//
//    //卡包按钮点击事件
//    @OnClick(R.id.txt_card)
//    public void txt_card_Click() {
//        MFGT.gotoCommon(getActivity(), getString(R.string.profile_txt_card));
//    }
//
//    //表情按钮点击事件
//    @OnClick(R.id.txt_smail)
//    public void txt_smail_Click() {
//        MFGT.gotoCommon(getActivity(), getString(R.string.profile_txt_smail));
//    }
//
//    //设置按钮点击事件
//    @OnClick(R.id.txt_setting)
//    public void setting_Click() {
//        MFGT.gotoSettingActivity(getActivity());
//    }

    //个人信息点击事件
    public void setting_Click() {
        Intent intent = new Intent(this.getContext(), SettingActivity.class);
        startActivity(intent);
    }
}
