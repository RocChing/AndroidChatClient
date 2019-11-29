package com.roc.chatclient.widget.chatrow;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.roc.chatclient.adapter.EaseMessageAdapter;
import com.roc.chatclient.entity.Msg;
import com.roc.chatclient.model.ChatHelper;
import com.roc.chatclient.model.UserExtInfo;
import com.roc.chatclient.util.CommonUtils;
import com.roc.chatclient.util.DateUtils;
import com.roc.chatclient.util.PreferenceManager;
import com.roc.chatclient.widget.EaseChatMessageList;
import com.roc.chatclient.widget.EaseChatMessageList.MessageListItemClickListener;
import com.roc.chatclient.R;

import java.util.Date;

public abstract class EaseChatRow extends LinearLayout {
    protected static final String TAG = EaseChatRow.class.getSimpleName();

    protected LayoutInflater inflater;
    protected Context context;
    protected BaseAdapter adapter;
    protected Msg message;
    protected int position;

    protected TextView timeStampView;
    protected ImageView userAvatarView;
    protected View bubbleLayout;
    protected TextView usernickView;

    protected TextView percentageView;
    protected ProgressBar progressBar;
    protected ImageView statusView;
    protected Activity activity;

    protected TextView ackedView;
    protected TextView deliveredView;

    protected int currentUserId;
    protected String currentUserAvatar;

    protected EaseChatMessageList.MessageListItemClickListener itemClickListener;

    public EaseChatRow(Context context, Msg message, int position, BaseAdapter adapter) {
        super(context);
        this.context = context;
        this.activity = (Activity) context;
        this.message = message;
        this.position = position;
        this.adapter = adapter;
        inflater = LayoutInflater.from(context);

        currentUserAvatar = PreferenceManager.getInstance().getCurrentUserAvatar();
        currentUserId = PreferenceManager.getInstance().getCurrentUserId();

        initView();
    }

    private void initView() {
        onInflatView();
        timeStampView = (TextView) findViewById(R.id.timestamp);
        userAvatarView = (ImageView) findViewById(R.id.iv_userhead);
        bubbleLayout = findViewById(R.id.bubble);
        usernickView = (TextView) findViewById(R.id.tv_userid);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        statusView = (ImageView) findViewById(R.id.msg_status);
        ackedView = (TextView) findViewById(R.id.tv_ack);
        deliveredView = (TextView) findViewById(R.id.tv_delivered);

        onFindViewById();
    }

    protected boolean isSendMsg() {
        return message.getSender() == currentUserId;
    }

    /**
     * set property according message and postion
     *
     * @param message
     * @param position
     */
    public void setUpView(Msg message, int position,
                          MessageListItemClickListener itemClickListener) {
        this.message = message;
        this.position = position;
        this.itemClickListener = itemClickListener;

        setUpBaseView();
        onSetUpView();
        setClickListener();
    }

    private void setUpBaseView() {
        // set nickname, avatar and background of bubble
        TextView timestamp = (TextView) findViewById(R.id.timestamp);
        if (timestamp != null) {
            if (position == 0) {
                timestamp.setText(message.getSendTime());//DateUtils.getTimestampString(new Date(message.getSendTime()))
                timestamp.setVisibility(View.VISIBLE);
            } else {
                // show time stamp if interval with last message is > 30 seconds
                Msg prevMessage = (Msg) adapter.getItem(position - 1);
                if (prevMessage != null) {//&& DateUtils.isCloseEnough(message.getSendTime(), prevMessage.getSendTime())
                    timestamp.setVisibility(View.GONE);
                } else {
                    timestamp.setText(message.getSendTime());
                    timestamp.setVisibility(View.VISIBLE);
                }
            }
        }

        int sender = message.getSender();
        //set nickname and avatar
        if (isSendMsg()) {
            // EaseUserUtils.setUserAvatar(context, EMClient.getInstance().getCurrentUser(), userAvatarView);
            CommonUtils.setUserAvatar(context, currentUserAvatar, userAvatarView);
        } else {
            // EaseUserUtils.setUserAvatar(context, message.getFrom(), userAvatarView);
            //EaseUserUtils.setUserNick(message.getSender(), usernickView);
            UserExtInfo user = ChatHelper.getInstance().getUserInfo(sender);
            if (user != null) {
                CommonUtils.setUserAvatar(context, user.Avatar, userAvatarView);
                CommonUtils.setUserNick(user, usernickView);
            }
        }

        if (deliveredView != null) {
//            if (message.isDelivered()) {
//                deliveredView.setVisibility(View.VISIBLE);
//            } else {
//                deliveredView.setVisibility(View.INVISIBLE);
//            }
            deliveredView.setVisibility(View.VISIBLE);
        }

//        if (ackedView != null) {
//            if (message.isAcked()) {
//                if (deliveredView != null) {
//                    deliveredView.setVisibility(View.INVISIBLE);
//                }
//                ackedView.setVisibility(View.VISIBLE);
//            } else {
//                ackedView.setVisibility(View.INVISIBLE);
//            }
//        }
        if (ackedView != null) {
            ackedView.setVisibility(View.INVISIBLE);
        }


        if (adapter instanceof EaseMessageAdapter) {
            if (((EaseMessageAdapter) adapter).isShowAvatar())
                userAvatarView.setVisibility(View.VISIBLE);
            else
                userAvatarView.setVisibility(View.GONE);
            if (usernickView != null) {
                if (((EaseMessageAdapter) adapter).isShowUserNick())
                    usernickView.setVisibility(View.VISIBLE);
                else
                    usernickView.setVisibility(View.GONE);
            }
            if (sender == currentUserId) {
                if (((EaseMessageAdapter) adapter).getMyBubbleBg() != null) {
                    bubbleLayout.setBackgroundDrawable(((EaseMessageAdapter) adapter).getMyBubbleBg());
                }
            } else {
                if (((EaseMessageAdapter) adapter).getOtherBuddleBg() != null) {
                    bubbleLayout.setBackgroundDrawable(((EaseMessageAdapter) adapter).getOtherBuddleBg());
                }
            }
        }
    }

//    /**
//     * set callback for sending message
//     */
//    protected void setMessageSendCallback() {
//        if (messageSendCallback == null) {
//            messageSendCallback = new EMCallBack() {
//
//                @Override
//                public void onSuccess() {
//                    updateView();
//                }
//
//                @Override
//                public void onProgress(final int progress, String status) {
//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (percentageView != null)
//                                percentageView.setText(progress + "%");
//
//                        }
//                    });
//                }
//
//                @Override
//                public void onError(int code, String error) {
//                    updateView();
//                }
//            };
//        }
//        message.setMessageStatusCallback(messageSendCallback);
//    }

//    /**
//     * set callback for receiving message
//     */
//    protected void setMessageReceiveCallback() {
//        if (messageReceiveCallback == null) {
//            messageReceiveCallback = new EMCallBack() {
//
//                @Override
//                public void onSuccess() {
//                    updateView();
//                }
//
//                @Override
//                public void onProgress(final int progress, String status) {
//                    activity.runOnUiThread(new Runnable() {
//                        public void run() {
//                            if (percentageView != null) {
//                                percentageView.setText(progress + "%");
//                            }
//                        }
//                    });
//                }
//
//                @Override
//                public void onError(int code, String error) {
//                    updateView();
//                }
//            };
//        }
//        message.setMessageStatusCallback(messageReceiveCallback);
//    }


    private void setClickListener() {
        if (bubbleLayout != null) {
            bubbleLayout.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        if (!itemClickListener.onBubbleClick(message)) {
                            // if listener return false, we call default handling
                            onBubbleClick();
                        }
                    }
                }
            });

            bubbleLayout.setOnLongClickListener(new OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onBubbleLongClick(message);
                    }
                    return true;
                }
            });
        }

        if (statusView != null) {
            statusView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onResendClick(message);
                    }
                }
            });
        }

        final int sender = message.getSender();

        if (userAvatarView != null) {
            userAvatarView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
//                        if (currentUserId == sender) {
//                            itemClickListener.onUserAvatarClick(EMClient.getInstance().getCurrentUser());
//                        } else {
//                            itemClickListener.onUserAvatarClick(message.getFrom());
//                        }
                    }
                }
            });
            userAvatarView.setOnLongClickListener(new OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    if (itemClickListener != null) {
//                        if (message.direct() == Direct.SEND) {
//                            itemClickListener.onUserAvatarLongClick(EMClient.getInstance().getCurrentUser());
//                        } else {
//                            itemClickListener.onUserAvatarLongClick(message.getFrom());
//                        }
                        return true;
                    }
                    return false;
                }
            });
        }
    }


    protected void updateView() {
        activity.runOnUiThread(new Runnable() {
            public void run() {
//                if (message.status() == EMMessage.Status.FAIL) {
//
//                    if (message.getError() == EMError.MESSAGE_INCLUDE_ILLEGAL_CONTENT) {
//                        Toast.makeText(activity, activity.getString(R.string.send_fail) + activity.getString(R.string.error_send_invalid_content), 0).show();
//                    } else if (message.getError() == EMError.GROUP_NOT_JOINED) {
//                        Toast.makeText(activity, activity.getString(R.string.send_fail) + activity.getString(R.string.error_send_not_in_the_group), 0).show();
//                    } else {
//                        Toast.makeText(activity, activity.getString(R.string.send_fail) + activity.getString(R.string.connect_failuer_toast), 0).show();
//                    }
//                }

                onUpdateView();
            }
        });

    }

    protected abstract void onInflatView();

    /**
     * find view by id
     */
    protected abstract void onFindViewById();

    /**
     * refresh list view when message status change
     */
    protected abstract void onUpdateView();

    /**
     * setup view
     */
    protected abstract void onSetUpView();

    /**
     * on bubble clicked
     */
    protected abstract void onBubbleClick();

}
