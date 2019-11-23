package com.roc.chatclient.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int rootLayoutId = getRootLayoutId();
        View rootView = inflater.inflate(rootLayoutId, container, false);
        initView(rootView);
        initData(rootView);
        setListener(rootView);
        return rootView;
    }

    public abstract int getRootLayoutId();

    protected abstract void initView(View view);

    protected abstract void initData(View view);

    protected abstract void setListener(View view);
}
