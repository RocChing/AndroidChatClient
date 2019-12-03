package com.roc.chatclient.ui.fragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.roc.chatclient.entity.Msg;
import com.roc.chatclient.model.ChatHelper;
import com.roc.chatclient.model.CmdInfo;
import com.roc.chatclient.model.CmdType;
import com.roc.chatclient.model.Constant;
import com.roc.chatclient.model.EaseEmojicon;
import com.roc.chatclient.model.FileInfo;
import com.roc.chatclient.model.MsgInfo;
import com.roc.chatclient.model.MsgToType;
import com.roc.chatclient.model.MsgType;
import com.roc.chatclient.model.ReceiveMsgInfo;
import com.roc.chatclient.model.UserExtInfo;
import com.roc.chatclient.util.CommonUtils;
import com.roc.chatclient.util.DateUtils;
import com.roc.chatclient.util.FileUtils;
import com.roc.chatclient.util.ImageUtils;
import com.roc.chatclient.util.PathUtil;
import com.roc.chatclient.util.StringUtils;
import com.roc.chatclient.widget.EaseChatExtendMenu;
import com.roc.chatclient.widget.EaseChatInputMenu;
import com.roc.chatclient.widget.EaseChatMessageList;
import com.roc.chatclient.R;
import com.roc.chatclient.widget.EaseVoiceRecorderView;
import com.roc.chatclient.widget.chatrow.EaseCustomChatRowProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class EaseChatFragment extends EaseBaseFragment {

    private EaseChatMessageList messageList;
    private ListView listView;

    protected int chatType;
    private String toChatUsername;

    protected EaseChatInputMenu inputMenu;

    private SwipeRefreshLayout swipeRefreshLayout;
    private InputMethodManager inputManager;
    private ClipboardManager clipboard;
    private boolean isMessageListInited;

    private ChatHelper chatHelper;

    //    protected boolean isloading;
    protected boolean haveMoreData = true;
    protected int pageSize = 10;
    protected int allPages = 0;
    protected int currentPageIndex = 1;
    protected int chatId;

    protected int currentUserId = 0;
    protected int toId = 0;
    private String Tag = "EaseChatFragment";

    protected static final int REQUEST_CODE_MAP = 1;
    protected static final int REQUEST_CODE_CAMERA = 2;
    protected static final int REQUEST_CODE_LOCAL = 3;

    static final int ITEM_TAKE_PICTURE = 1;
    static final int ITEM_PICTURE = 2;
    static final int ITEM_LOCATION = 3;

    protected File cameraFile;

    protected int[] itemStrings = {R.string.attach_take_pic, R.string.attach_picture, R.string.attach_location};
    protected int[] itemdrawables = {R.drawable.ease_chat_takepic_selector, R.drawable.ease_chat_image_selector,
            R.drawable.ease_chat_location_selector};
    protected int[] itemIds = {ITEM_TAKE_PICTURE, ITEM_PICTURE, ITEM_LOCATION};
    protected MyItemClickListener extendMenuItemClickListener;

    protected EaseVoiceRecorderView voiceRecorderView;

    protected List<Msg> msgList;

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

        String toIdString = fragmentArgs.getString(Constant.EXTRA_CHAT_TO_ID);

        if (!StringUtils.isEmpty(toIdString)) {
            toId = Integer.parseInt(toIdString);
        }

        int allMsgCount = fragmentArgs.getInt(Constant.EXTRA_MESSAGE_COUNT);
        allPages = (allMsgCount + pageSize - 1) / pageSize;

        Log.d(Tag, "the chat id is:" + chatId);
        chatHelper = ChatHelper.getInstance();

        currentUserId = chatHelper.getCurrentUserId();

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void initView() {
        // message list layout
        voiceRecorderView = (EaseVoiceRecorderView) getView().findViewById(R.id.voice_recorder);

        messageList = getView().findViewById(R.id.message_list);
        if (chatType != Constant.CHATTYPE_SINGLE)
            messageList.setShowUserNick(true);
        listView = messageList.getListView();
        inputMenu = getView().findViewById(R.id.input_menu);

        inputMenu.init();

        extendMenuItemClickListener = new MyItemClickListener();

        registerExtendMenuItem();

        inputMenu.setChatInputMenuListener(new EaseChatInputMenu.ChatInputMenuListener() {

            @Override
            public void onSendMessage(String content) {
                sendTextMessage(content);
            }

            @Override
            public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
                return voiceRecorderView.onPressToSpeakBtnTouch(v, event, new EaseVoiceRecorderView.EaseVoiceRecorderCallback() {

                    @Override
                    public void onVoiceRecordComplete(String voiceFilePath, int voiceTimeLength) {
//                        sendVoiceMessage(voiceFilePath, voiceTimeLength);
                    }
                });
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

        msgList = chatHelper.getChatMsgList(currentPageIndex, pageSize, chatId);
        messageList.setList(msgList);

        chatHelper.setMsgRead(chatId);//设为已读
    }

    protected void setRefreshLayoutListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (listView.getFirstVisiblePosition() == 0 && haveMoreData) {
                            List<Msg> messages = null;
                            Log.d(Tag, "refresh message ....");
                            if (currentPageIndex < allPages) {
                                currentPageIndex++;
                            } else {
                                currentPageIndex = 1;
                                Toast.makeText(getActivity(), getResources().getString(R.string.no_more_messages),
                                        Toast.LENGTH_SHORT).show();
                                swipeRefreshLayout.setRefreshing(false);
                                return;
                            }
                            try {
                                if (chatType == Constant.CHATTYPE_SINGLE) {
                                    messages = chatHelper.getChatMsgList(currentPageIndex, pageSize, chatId);
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
                                msgList.addAll(messages);
                                messageList.setList(msgList);
                                int position = 0;
                                messageList.refreshSeekTo(position);
                                if (messages.size() != pageSize) {
                                    haveMoreData = false;
                                }
                            }
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 600);
            }
        });
    }

    protected void onMessageListInit() {
        messageList.init(toChatUsername, chatType, chatFragmentHelper != null ? chatFragmentHelper.onSetCustomChatRowProvider() : null);
        setListItemClickListener();

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
        if (toId < 1) {
            Log.w(Tag, "the to id is:" + toId);
            return;
        }
        MsgInfo info = new MsgInfo(content, MsgType.Text, currentUserId, toId, MsgToType.User);

        sendMsg(info);
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

    public void refresh(Msg msg) {
        msgList.add(msg);
        messageList.setList(msgList);
        messageList.refreshSelectLast();
    }

    protected void setListItemClickListener() {
        messageList.setItemClickListener(new EaseChatMessageList.MessageListItemClickListener() {

            @Override
            public void onUserAvatarClick(String username) {
                if (chatFragmentHelper != null) {
                    chatFragmentHelper.onAvatarClick(username);
                }
            }

            @Override
            public void onUserAvatarLongClick(String username) {
                if (chatFragmentHelper != null) {
                    chatFragmentHelper.onAvatarLongClick(username);
                }
            }

            @Override
            public void onResendClick(final Msg message) {
//                new EaseAlertDialog(getActivity(), R.string.resend, R.string.confirm_resend, null, new AlertDialogUser() {
//                    @Override
//                    public void onResult(boolean confirmed, Bundle bundle) {
//                        if (!confirmed) {
//                            return;
//                        }
//                        resendMessage(message);
//                    }
//                }, true).show();
            }

            @Override
            public void onBubbleLongClick(Msg message) {
                //contextMenuMessage = message;
                if (chatFragmentHelper != null) {
                    chatFragmentHelper.onMessageBubbleLongClick(message);
                }
            }

            @Override
            public boolean onBubbleClick(Msg message) {
                if (chatFragmentHelper != null) {
                    return chatFragmentHelper.onMessageBubbleClick(message);
                }
                return false;
            }
        });
    }

    /**
     * register extend menu, item id need > 3 if you override this method and keep exist item
     */
    protected void registerExtendMenuItem() {
        for (int i = 0; i < itemStrings.length; i++) {
            inputMenu.registerExtendMenuItem(itemStrings[i], itemdrawables[i], itemIds[i], extendMenuItemClickListener);
        }
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

    /**
     * handle the click event for extend menu
     */
    class MyItemClickListener implements EaseChatExtendMenu.EaseChatExtendMenuItemClickListener {

        @Override
        public void onClick(int itemId, View view) {
            if (chatFragmentHelper != null) {
                if (chatFragmentHelper.onExtendMenuItemClick(itemId, view)) {
                    return;
                }
            }
            switch (itemId) {
                case ITEM_TAKE_PICTURE:
                    selectPicFromCamera();
                    break;
                case ITEM_PICTURE:
                    selectPicFromLocal();
                    break;
                case ITEM_LOCATION:
//                    startActivityForResult(new Intent(getActivity(), EaseBaiduMapActivity.class), REQUEST_CODE_MAP);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * capture new image
     */
    protected void selectPicFromCamera() {
        if (!CommonUtils.isSdcardExist()) {
            Toast.makeText(getActivity(), R.string.sd_card_does_not_exist, Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = chatHelper.getCurrentUserName() + "_" + System.currentTimeMillis() + ".jpg";
        try {
            cameraFile = new File(PathUtil.getInstance().getImagePath(), fileName);
            cameraFile.getParentFile().mkdirs();
            startActivityForResult(
                    new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
                    REQUEST_CODE_CAMERA);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * select local image
     */
    protected void selectPicFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

        } else {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_LOCAL);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) { // capture new image
                if (cameraFile != null && cameraFile.exists())
                    sendImageMessage(cameraFile.getAbsolutePath());
            } else if (requestCode == REQUEST_CODE_LOCAL) { // send local image
                if (data != null) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        sendPicByUri(selectedImage);
                    }
                }
            } else if (requestCode == REQUEST_CODE_MAP) { // location
//                double latitude = data.getDoubleExtra("latitude", 0);
//                double longitude = data.getDoubleExtra("longitude", 0);
//                String locationAddress = data.getStringExtra("address");
//                if (locationAddress != null && !locationAddress.equals("")) {
//                    sendLocationMessage(latitude, longitude, locationAddress);
//                } else {
//                    Toast.makeText(getActivity(), R.string.unable_to_get_loaction, 0).show();
//                }
            }
        }
    }

    /**
     * send file
     *
     * @param uri
     */
    protected void sendFileByUri(Uri uri) {
        String filePath = null;
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] filePathColumn = {MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.TITLE, MediaStore.Files.FileColumns.DATA};
            Cursor cursor = null;
            try {
                cursor = getActivity().getContentResolver().query(uri, filePathColumn, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(column_index);
                }
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            filePath = uri.getPath();
        }
        File file = new File(filePath);
        if (file == null || !file.exists()) {
            Toast.makeText(getActivity(), R.string.File_does_not_exist, Toast.LENGTH_SHORT).show();
            return;
        }
        //limit the size < 10M
        if (file.length() > 10 * 1024 * 1024) {
            Toast.makeText(getActivity(), R.string.The_file_is_not_greater_than_10_m, Toast.LENGTH_SHORT).show();
            return;
        }
        sendFileMessage(filePath);
    }

    protected void sendFileMessage(String path) {
        File oldFile = new File(path);
        byte[] bytes = FileUtils.File2Bytes(oldFile);

        File filePath = PathUtil.getInstance().getFilePath();
        File newFile = FileUtils.saveFile(filePath.getAbsolutePath(), oldFile.getName(), bytes);
        if (newFile == null) {
            return;
        }

        FileInfo fileInfo = new FileInfo(newFile, "");

        MsgInfo info = new MsgInfo(JSON.toJSONString(fileInfo), MsgType.File, currentUserId, toId, MsgToType.User, bytes);
        sendMsg(info);
    }

    protected void sendImageMessage(String path) {
        File file = new File(path);
        byte[] bytes = FileUtils.File2Bytes(file);

        File imagePath = PathUtil.getInstance().getImagePath();
        File imageFile = FileUtils.saveFile(imagePath.getAbsolutePath(), file.getName(), bytes);

        if (imageFile == null) {
            return;
        }

        String thumbPath = ImageUtils.saveThumbImage(imageFile, ImageUtils.ThumbWidth, ImageUtils.ThumbHeight);
        FileInfo fileInfo = new FileInfo(imageFile, thumbPath);
        String json = JSON.toJSONString(fileInfo);

        MsgInfo info = new MsgInfo(json, MsgType.Image, currentUserId, toId, MsgToType.User, bytes);
        //info.MsgBase64 = CommonUtils.encodeBase64(bytes);
        sendMsg(info);
    }

    public void sendMsg(MsgInfo info) {
        Msg msg = new Msg();
        msg.setChatId(chatId);
        msg.setContent(info.Msg);
        msg.setSender(info.From);
        msg.setSendTime(DateUtils.getFormatDate(new Date()));
        msg.setType(info.Type);

        chatHelper.saveMsg(msg);
        chatHelper.sendMsg(CmdType.SendMsg, info);
        refresh(msg);
    }

    /**
     * send image
     *
     * @param selectedImage
     */
    protected void sendPicByUri(Uri selectedImage) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            if (StringUtils.isEmpty(picturePath)) {
                Toast toast = Toast.makeText(getActivity(), R.string.cant_find_pictures, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            Log.d(Tag, "the picture path is:" + picturePath);
            sendImageMessage(picturePath);
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(getActivity(), R.string.cant_find_pictures, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            Log.d(Tag, "the file getAbsolutePath is:" + file.getAbsolutePath());
            sendImageMessage(file.getAbsolutePath());
        }
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
