package com.roc.chatclient.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ListView;
import com.roc.chatclient.R;

import java.util.ArrayList;
import java.util.List;

public class EaseConversationList extends ListView {

    protected int primaryColor;
    protected int secondaryColor;
    protected int timeColor;
    protected int primarySize;
    protected int secondarySize;
    protected float timeSize;


    protected final int MSG_REFRESH_ADAPTER_DATA = 0;

    protected Context context;
//    protected EaseConversationAdapater adapter;
//    protected List<EMConversation> conversations = new ArrayList<EMConversation>();
//    protected List<EMConversation> passedListRef = null;

    public EaseConversationList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EaseConversationList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EaseConversationList);

        primaryColor = ta.getColor(R.styleable.EaseConversationList_cvsListPrimaryTextColor, R.color.list_itease_primary_color);
        secondaryColor = ta.getColor(R.styleable.EaseConversationList_cvsListSecondaryTextColor, R.color.list_itease_secondary_color);
        timeColor = ta.getColor(R.styleable.EaseConversationList_cvsListTimeTextColor, R.color.list_itease_secondary_color);
        primarySize = ta.getDimensionPixelSize(R.styleable.EaseConversationList_cvsListPrimaryTextSize, 0);
        secondarySize = ta.getDimensionPixelSize(R.styleable.EaseConversationList_cvsListSecondaryTextSize, 0);
        timeSize = ta.getDimension(R.styleable.EaseConversationList_cvsListTimeTextSize, 0);

        ta.recycle();
    }
}
