package com.roc.chatclient.ui.fragment;

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

public class ChatFragment extends EaseChatFragment implements EaseChatFragment.EaseChatFragmentHelper {
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
        return false;
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
