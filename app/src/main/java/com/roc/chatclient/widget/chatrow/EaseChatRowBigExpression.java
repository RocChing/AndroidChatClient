package com.roc.chatclient.widget.chatrow;

import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.roc.chatclient.entity.Msg;
import com.roc.chatclient.R;

/**
 * big emoji icons
 */
public class EaseChatRowBigExpression extends EaseChatRowText {

    private ImageView imageView;


    public EaseChatRowBigExpression(Context context, Msg message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflatView() {
        inflater.inflate(message.getSender() == currentUserId ?
                R.layout.ease_row_sent_bigexpression : R.layout.ease_row_received_bigexpression, this);
    }

    @Override
    protected void onFindViewById() {
        //percentageView = (TextView) findViewById(R.id.percentage);
       // imageView = (ImageView) findViewById(R.id.image);
    }

    @Override
    public void onSetUpView() {
//        String emojiconId = message.getStringAttribute(EaseConstant.MESSAGE_ATTR_EXPRESSION_ID, null);
//        EaseEmojicon emojicon = null;
//        if (EaseUI.getInstance().getEmojiconInfoProvider() != null) {
//            emojicon = EaseUI.getInstance().getEmojiconInfoProvider().getEmojiconInfo(emojiconId);
//        }
//        if (emojicon != null) {
//            if (emojicon.getBigIcon() != 0) {
//                Glide.with(activity).load(emojicon.getBigIcon()).error(R.drawable.ease_default_expression).into(imageView);
//            } else if (emojicon.getBigIconPath() != null) {
//                Glide.with(activity).load(emojicon.getBigIconPath()).error(R.drawable.ease_default_expression).into(imageView);
//            } else {
//                imageView.setImageResource(R.drawable.ease_default_expression);
//            }
//        }

        handleTextMessage();
    }
}
