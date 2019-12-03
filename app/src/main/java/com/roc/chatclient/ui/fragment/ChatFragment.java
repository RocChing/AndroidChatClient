package com.roc.chatclient.ui.fragment;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.roc.chatclient.entity.Msg;
import com.roc.chatclient.model.Constant;
import com.roc.chatclient.model.MsgType;
import com.roc.chatclient.widget.chatrow.EaseChatRow;
import com.roc.chatclient.widget.chatrow.EaseCustomChatRowProvider;
import com.roc.chatclient.R;

import java.io.File;
import java.io.FileOutputStream;

public class ChatFragment extends EaseChatFragment implements EaseChatFragment.EaseChatFragmentHelper {
    private static final int ITEM_VIDEO = 11;
    private static final int ITEM_FILE = 12;
    private static final int ITEM_VOICE_CALL = 13;
    private static final int ITEM_VIDEO_CALL = 14;
    private static final int ITEM_RED_PACKET = 16;

    private static final int REQUEST_CODE_SELECT_VIDEO = 11;
    private static final int REQUEST_CODE_SELECT_FILE = 12;
    private static final int REQUEST_CODE_GROUP_DETAIL = 13;
    private static final int REQUEST_CODE_CONTEXT_MENU = 14;
    private static final int REQUEST_CODE_SELECT_AT_USER = 15;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void setUpView() {
        setChatFragmentListener(this);
        super.setUpView();
    }

    @Override
    protected void registerExtendMenuItem() {
        //use the menu in base class
        super.registerExtendMenuItem();
        //extend menu items
//        inputMenu.registerExtendMenuItem(R.string.attach_video, R.drawable.em_chat_video_normal, ITEM_VIDEO, extendMenuItemClickListener);
        inputMenu.registerExtendMenuItem(R.string.attach_file, R.drawable.chat_tool_send_file, ITEM_FILE, extendMenuItemClickListener);
        if (chatType == Constant.CHATTYPE_SINGLE) {
            inputMenu.registerExtendMenuItem(R.string.attach_voice_call, R.drawable.chat_tool_audio, ITEM_VOICE_CALL, extendMenuItemClickListener);
            inputMenu.registerExtendMenuItem(R.string.attach_video_call, R.drawable.chat_tool_video, ITEM_VIDEO_CALL, extendMenuItemClickListener);
        }
//        //no red packet in chatroom
//        if (chatType != Constant.CHATTYPE_CHATROOM) {
//            inputMenu.registerExtendMenuItem(R.string.attach_red_packet, R.drawable.em_chat_red_packet_selector, ITEM_RED_PACKET, extendMenuItemClickListener);
//        }
    }


    @Override
    public void onSetMessageAttributes(Msg message) {

    }

    @Override
    public void onEnterToChatDetails() {

    }

    @Override
    public void onAvatarClick(String username) {

    }

    @Override
    public void onAvatarLongClick(String username) {

    }

    @Override
    public boolean onMessageBubbleClick(Msg message) {
        return false;
    }

    @Override
    public void onMessageBubbleLongClick(Msg message) {

    }

    @Override
    public boolean onExtendMenuItemClick(int itemId, View view) {
        switch (itemId) {
            case ITEM_VIDEO:
//                Intent intent = new Intent(getActivity(), ImageGridActivity.class);
//                startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO);
                break;
            case ITEM_FILE: //file
                selectFileFromLocal();
                break;
            case ITEM_VOICE_CALL:
//                startVoiceCall();
                break;
            case ITEM_VIDEO_CALL:
//                startVideoCall();
                break;
            case ITEM_RED_PACKET:
//                RedPacketUtil.startRedPacketActivityForResult(this, chatType, toChatUsername, REQUEST_CODE_SEND_MONEY);
                break;
            default:
                break;
        }
        //keep exist extend menu
        return false;
    }

    /**
     * select file
     */
    protected void selectFileFromLocal() {
        Intent intent = null;
        if (Build.VERSION.SDK_INT < 19) { //api 19 and later, we can't use this way, demo just select from images
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Files.getContentUri("external"));
        }
        startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
//            switch (resultCode) {
//                case ContextMenuActivity.RESULT_CODE_COPY: // copy
//                    clipboard.setPrimaryClip(ClipData.newPlainText(null,
//                            ((EMTextMessageBody) contextMenuMessage.getBody()).getMessage()));
//                    break;
//                case ContextMenuActivity.RESULT_CODE_DELETE: // delete
//                    conversation.removeMessage(contextMenuMessage.getMsgId());
//                    messageList.refresh();
//                    break;
//
//                case ContextMenuActivity.RESULT_CODE_FORWARD: // forward
//                    Intent intent = new Intent(getActivity(), ForwardMessageActivity.class);
//                    intent.putExtra("forward_msg_id", contextMenuMessage.getMsgId());
//                    startActivity(intent);
//
//                    break;
//
//                default:
//                    break;
//            }
        }
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_SELECT_VIDEO: //send the video
//                    if (data != null) {
//                        int duration = data.getIntExtra("dur", 0);
//                        String videoPath = data.getStringExtra("path");
//                        File file = new File(PathUtil.getInstance().getImagePath(), "thvideo" + System.currentTimeMillis());
//                        try {
//                            FileOutputStream fos = new FileOutputStream(file);
//                            Bitmap ThumbBitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 3);
//                            ThumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//                            fos.close();
//                            sendVideoMessage(videoPath, file.getAbsolutePath(), duration);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
                    break;
                case REQUEST_CODE_SELECT_FILE: //send the file
                    if (data != null) {
                        Uri uri = data.getData();
                        if (uri != null) {
                            sendFileByUri(uri);
                        }
                    }
                    break;
                case REQUEST_CODE_SELECT_AT_USER:
//                    if (data != null) {
////                        String username = data.getStringExtra("username");
////                        inputAtUsername(username, false);
////                    }
                    break;

//                case REQUEST_CODE_SEND_MONEY:
//                    if (data != null) {
//                        sendMessage(RedPacketUtil.createRPMessage(getActivity(), data, toChatUsername));
//                    }
//                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
        return new CustomChatRowProvider();
    }

    /**
     * chat row provider
     */
    private final class CustomChatRowProvider implements EaseCustomChatRowProvider {
        @Override
        public int getCustomChatRowTypeCount() {
            //here the number is the message type in EMMessage::Type
            //which is used to count the number of different chat row
            return 8;
        }

        @Override
        public int getCustomChatRowType(Msg message) {
            MsgType type = MsgType.getType(message.getType());
            if (type == MsgType.Text) {
                //voice call
//                if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
//                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE_CALL : MESSAGE_TYPE_SENT_VOICE_CALL;
//                } else if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
//                    //video call
//                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO_CALL : MESSAGE_TYPE_SENT_VIDEO_CALL;
//                } else if (message.getBooleanAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE, false)) {
//                    //sent redpacket message
//                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_MONEY : MESSAGE_TYPE_SEND_MONEY;
//                } else if (message.getBooleanAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE, false)) {
//                    //received redpacket message
//                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_LUCKY : MESSAGE_TYPE_SEND_LUCKY;
//                }
            }
            return 0;
        }

        @Override
        public EaseChatRow getCustomChatRow(Msg message, int position, BaseAdapter adapter) {
            MsgType type = MsgType.getType(message.getType());
            if (type == MsgType.Text) {
                // voice call or video call
//                if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false) ||
//                        message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
//                    return new ChatRowVoiceCall(getActivity(), message, position, adapter);
//                } else if (message.getBooleanAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_MESSAGE, false)) {//send redpacket
//                    return new ChatRowRedPacket(getActivity(), message, position, adapter);
//                } else if (message.getBooleanAttribute(RedPacketConstant.MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE, false)) {//open redpacket message
//                    return new ChatRowRedPacketAck(getActivity(), message, position, adapter);
//                }
            }
            return null;
        }

    }
}
