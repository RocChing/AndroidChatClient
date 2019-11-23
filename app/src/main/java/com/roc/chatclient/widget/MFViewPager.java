package com.roc.chatclient.widget;

import android.content.Context;

import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

public class MFViewPager extends ViewPager {

    private boolean scrollble = true;

    public MFViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!scrollble) {
            return true;
        }
        return super.onTouchEvent(ev);
    }


    public boolean isScrollble() {
        return scrollble;
    }

    public void setScrollble(boolean scrollble) {
        this.scrollble = scrollble;
    }
}
