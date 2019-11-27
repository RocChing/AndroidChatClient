package com.roc.chatclient.ui.fragment;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.roc.chatclient.entity.Msg;
import com.roc.chatclient.model.ChatHelper;
import com.roc.chatclient.model.Constant;
import com.roc.chatclient.model.EaseEmojicon;
import com.roc.chatclient.model.UserExtInfo;
import com.roc.chatclient.widget.EaseChatInputMenu;
import com.roc.chatclient.widget.EaseChatMessageList;
import com.roc.chatclient.R;
import com.roc.chatclient.widget.chatrow.EaseCustomChatRowProvider;

import java.util.List;

public class EaseChatFragment extends EaseBaseFragment {

    private EaseChatMessageList messageList;
    private ListView listView;

    private int chatType;
    private String toChatUsername;

    private EaseChatInputMenu inputMenu;

    private SwipeRefreshLayout swipeRefreshLayout;
    private InputMethodManager inputManager;
    private ClipboardManager clipboard;
    private boolean isMessageListInited;

    private ChatHelper chatHelper;

    protected boolean isloading;
    protected boolean haveMoreData = true;
    protected int pagesize = 20;

    private int chatId;

    private String Tag = "EaseChatFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ease_fragment_chat, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Bundle fragmentArgs = getArguments();
        // check if single chat or group chat
        chatType = fragmentArgs.getInt(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_SINGLE);
        // userId you are chat with or group id
        toChatUsername = fragmentArgs.getString(Constant.EXTRA_USER_ID);
        //聊天id
        chatId = fragmentArgs.getInt(Constant.EXTRA_CHAT_ID);

        Log.d(Tag, "the chat id is:" + chatId);
        chatHelper = ChatHelper.getInstance();

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void initView() {
        // message list layout
        messageList = getView().findViewById(R.id.message_list);
        if (chatType != Constant.CHATTYPE_SINGLE)
            messageList.setShowUserNick(true);
        listView = messageList.getListView();
        inputMenu = getView().findViewById(R.id.input_menu);

        inputMenu.init();

        inputMenu.setChatInputMenuListener(new EaseChatInputMenu.ChatInputMenuListener() {

            @Override
            public void onSendMessage(String content) {
                sendTextMessage(content);
            }

            @Override
            public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
//                return voiceRecorderView.onPressToSpeakBtnTouch(v, event, new EaseVoiceRecorderCallback() {
//
//                    @Override
//                    public void onVoiceRecordComplete(String voiceFilePath, int voiceTimeLength) {
//                        sendVoiceMessage(voiceFilePath, voiceTimeLength);
//                    }
//                });
                return false;
            }

            @Override
            public void onBigExpressionClicked(EaseEmojicon emojicon) {
                //sendBigExpressionMessage(emojicon.getName(), emojicon.getIdentityCode());
            }
        });

        swipeRefreshLayout = messageList.getSwipeRefreshLayout();
        swipeRefreshLayout.setColorSchemeResources(R.color.black_deep);

        inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void setUpView() {
        titleBar.setTitle(toChatUsername);
        if (chatType == Constant.CHATTYPE_SINGLE) {
            // set title
            UserExtInfo userExtInfo = chatHelper.getUserInfo(toChatUsername);
            if (userExtInfo != null) {
                titleBar.setTitle(userExtInfo.NickName);
            }
            titleBar.setRightImageResource(R.drawable.ease_mm_title_remove);
        } else {
            titleBar.setRightImageResource(R.drawable.ease_to_group_details_normal);
            if (chatType == Constant.CHATTYPE_GROUP) {
                //group chat
                // EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatUsername);
//                if (group != null)
//                    titleBar.setTitle(group.getGroupName());
//                // listen the event that user moved out group or group is dismissed
//                groupListener = new GroupListener();
//                EMClient.getInstance().groupManager().addGroupChangeListener(groupListener);
            } else {
//                onChatRoomViewCreation();
            }

        }
        if (chatType != Constant.CHATTYPE_CHATROOM) {
//            onConversationInit();
            onMessageListInit();
        }

        titleBar.setLeftLayoutClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        titleBar.setRightLayoutClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (chatType == Constant.CHATTYPE_SINGLE) {
                    emptyHistory();
                } else {
//                    toGroupDetails();
                }
            }
        });

        setRefreshLayoutListener();

        // show forward message if the message is not null
        String forward_msg_id = getArguments().getString("forward_msg_id");
        if (forward_msg_id != null) {
//            forwardMessage(forward_msg_id);
        }

        List<Msg> list = chatHelper.getChatMsgList(1, pagesize, chatId);
        messageList.setList(list);
    }

    protected void setRefreshLayoutListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (listView.getFirstVisiblePosition() == 0 && !isloading && haveMoreData) {
                            List<Msg> messages = null;
                            try {
                                if (chatType == Constant.CHATTYPE_SINGLE) {
                                    messages = chatHelper.getChatMsgList(1, pagesize, chatId);
                                }
//                                else {
//                                    messages = conversation.loadMoreMsgFromDB(messageList.getItem(0).getMsgId(),
//                                            pagesize);
//                                }
                            } catch (Exception e1) {
                                swipeRefreshLayout.setRefreshing(false);
                                return;
                            }
                            if (messages != null && messages.size() > 0) {
                                messageList.refreshSeekTo(messages.size() - 1);
                                if (messages.size() != pagesize) {
                                    haveMoreData = false;
                                }
                            } else {
                                haveMoreData = false;
                            }

                            isloading = false;

                        } else {
                            Toast.makeText(getActivity(), getResources().getString(R.string.no_more_messages),
                                    Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 600);
            }
        });
    }

    protected void onMessageListInit() {
        messageList.init(toChatUsername, chatType, chatFragmentHelper != null ? chatFragmentHelper.onSetCustomChatRowProvider() : null);
        //setListItemClickListener();

        messageList.getListView().setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                inputMenu.hideExtendMenuContainer();
                return false;
            }
        });

        isMessageListInited = true;
    }

    protected void sendTextMessage(String content) {
        Log.d(Tag, "the input msg is:" + content);
    }

    protected void emptyHistory() {
//        String msg = getResources().getString(R.string.Whether_to_empty_all_chats);
//        new EaseAlertDialog(getActivity(),null, msg, null,new AlertDialogUser() {
//
//            @Override
//            public void onResult(boolean confirmed, Bundle bundle) {
//                if(confirmed){
//                    EMClient.getInstance().chatManager().deleteConversation(toChatUsername, true);
//                    messageList.refresh();
//                }
//            }
//        }, true).show();;
    }

    /**
     * hide
     */
    protected void hideKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void onBackPressed() {
        if (inputMenu.onBackPressed()) {
            getActivity().finish();
            if (chatType == Constant.CHATTYPE_CHATROOM) {
                //EMClient.getInstance().chatroomManager().leaveChatRoom(toChatUsername);
            }
        }
    }

    protected EaseChatFragmentHelper chatFragmentHelper;

    public void setChatFragmentListener(EaseChatFragmentHelper chatFragmentHelper) {
        this.chatFragmentHelper = chatFragmentHelper;
    }

    public interface EaseChatFragmentHelper {
        /**
         * set message attribute
         */
        void onSetMessageAttributes(Msg message);

        /**
         * enter to chat detail
         */
        void onEnterToChatDetails();

        /**
         * on avatar clicked
         *
         * @param username
         */
        void onAvatarClick(String username);

        /**
         * on avatar long pressed
         *
         * @param username
         */
        void onAvatarLongClick(String username);

        /**
         * on message bubble clicked
         */
        boolean onMessageBubbleClick(Msg message);

        /**
         * on message bubble long pressed
         */
        void onMessageBubbleLongClick(Msg message);

        /**
         * on extend menu item clicked, return true if you want to override
         *
         * @param view
         * @param itemId
         * @return
         */
        boolean onExtendMenuItemClick(int itemId, View view);

        /**
         * on set custom chat row provider
         *
         * @return
         */
        EaseCustomChatRowProvider onSetCustomChatRowProvider();
    }
}
