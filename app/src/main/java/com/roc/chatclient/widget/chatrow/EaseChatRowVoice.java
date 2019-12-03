package com.roc.chatclient.widget.chatrow;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.roc.chatclient.R;

import com.roc.chatclient.entity.Msg;
import com.roc.chatclient.model.VoiceInfo;

public class EaseChatRowVoice extends EaseChatRowFile {
    private static final String TAG = "EaseChatRowVoice";

    private ImageView voiceImageView;
    private TextView voiceLengthView;
    private ImageView readStatusView;

    private AnimationDrawable voiceAnimation;

    private VoiceInfo voiceInfo;

    public EaseChatRowVoice(Context context, Msg message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflatView() {
        inflater.inflate(isSendMsg() ?
                R.layout.ease_row_sent_voice : R.layout.ease_row_received_voice, this);
    }

    @Override
    protected void onFindViewById() {
        voiceImageView = ((ImageView) findViewById(R.id.iv_voice));
        voiceLengthView = (TextView) findViewById(R.id.tv_length);
        readStatusView = (ImageView) findViewById(R.id.iv_unread_voice);

        voiceInfo = JSON.parseObject(message.getContent(), VoiceInfo.class);
    }

    @Override
    protected void onSetUpView() {
//        EMVoiceMessageBody voiceBody = (EMVoiceMessageBody) message.getBody();
        int len = voiceInfo.Length;
        boolean flag = !isSendMsg();
        if (len > 0) {
            voiceLengthView.setText(voiceInfo.Length + "\"");
            voiceLengthView.setVisibility(View.VISIBLE);
        } else {
            voiceLengthView.setVisibility(View.INVISIBLE);
        }
        if (flag) {
            voiceImageView.setImageResource(R.drawable.ease_chatfrom_voice_playing);
        } else {
            voiceImageView.setImageResource(R.drawable.ease_chatto_voice_playing);
        }

        if (flag) {
            if (false) {
                // hide the unread icon
                readStatusView.setVisibility(View.INVISIBLE);
            } else {
                readStatusView.setVisibility(View.VISIBLE);
            }
            Log.d(TAG, "it is receive msg");

//            if (voiceBody.downloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
//                    voiceBody.downloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING) {
//                if (EMClient.getInstance().getOptions().getAutodownloadThumbnail()) {
//                    progressBar.setVisibility(View.VISIBLE);
//                } else {
//                    progressBar.setVisibility(View.INVISIBLE);
//                }
//
//            } else {
//                progressBar.setVisibility(View.INVISIBLE);
//            }
        }

        progressBar.setVisibility(View.INVISIBLE);

        // To avoid the item is recycled by listview and slide to this item again but the animation is stopped.
        EaseChatRowVoicePlayer voicePlayer = EaseChatRowVoicePlayer.getInstance(getContext());
        if (voicePlayer.isPlaying() && message.getId() == voicePlayer.getCurrentPlayingId()) {
            startVoicePlayAnimation();
        }
    }

//    @Override
//    protected void onViewUpdate(Msg msg) {
//        super.onViewUpdate(msg);
//
//        // Only the received message has the attachment download status.
//        if (isSendMsg()) {
//            return;
//        }
//
//        progressBar.setVisibility(View.INVISIBLE);
////        EMVoiceMessageBody voiceBody = (EMVoiceMessageBody) msg.getBody();
////        if (voiceBody.downloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
////                voiceBody.downloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING) {
////            progressBar.setVisibility(View.VISIBLE);
////        } else {
////            progressBar.setVisibility(View.INVISIBLE);
////        }
//    }

    public void startVoicePlayAnimation() {
        boolean flag = !isSendMsg();
        if (flag) {
            voiceImageView.setImageResource(R.drawable.voice_from_icon);
        } else {
            voiceImageView.setImageResource(R.drawable.voice_to_icon);
        }
        voiceAnimation = (AnimationDrawable) voiceImageView.getDrawable();
        voiceAnimation.start();

        // Hide the voice item not listened status view.
        if (flag) {
            readStatusView.setVisibility(View.INVISIBLE);
        }
    }

    public void stopVoicePlayAnimation() {
        if (voiceAnimation != null) {
            voiceAnimation.stop();
        }

        if (!isSendMsg()) {
            voiceImageView.setImageResource(R.drawable.ease_chatfrom_voice_playing);
        } else {
            voiceImageView.setImageResource(R.drawable.ease_chatto_voice_playing);
        }
    }

    @Override
    protected void onBubbleClick() {
        new EaseChatRowVoicePlayClickListener(voiceInfo, message, voiceImageView, readStatusView, adapter, activity).onClick(bubbleLayout);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
//        if (EaseChatRowVoicePlayClickListener.currentPlayListener != null && EaseChatRowVoicePlayClickListener.isPlaying) {
//            EaseChatRowVoicePlayClickListener.currentPlayListener.stopPlayVoice();
//        }
    }
}
