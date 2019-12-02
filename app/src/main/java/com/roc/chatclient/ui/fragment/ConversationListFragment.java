package com.roc.chatclient.ui.fragment;

import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.roc.chatclient.R;
import com.roc.chatclient.db.InviteMessageDao;
import com.roc.chatclient.entity.Msg;
import com.roc.chatclient.model.ChatHelper;
import com.roc.chatclient.model.CmdInfo;
import com.roc.chatclient.model.Constant;
import com.roc.chatclient.model.EMConversation;
import com.roc.chatclient.model.FileInfo;
import com.roc.chatclient.model.MsgType;
import com.roc.chatclient.model.ReceiveMsgInfo;
import com.roc.chatclient.receiver.IMsgCallback;
import com.roc.chatclient.ui.ChatActivity;
import com.roc.chatclient.util.CommonUtils;
import com.roc.chatclient.util.FileUtils;
import com.roc.chatclient.util.ImageUtils;
import com.roc.chatclient.util.PathUtil;

import java.io.File;

public class ConversationListFragment extends EaseConversationListFragment {
    private TextView errorText;

    @Override
    protected void initView() {
        super.initView();
        View errorView = View.inflate(getActivity(), R.layout.em_chat_neterror_item, null);
        errorItemContainer.addView(errorView);
        errorText = errorView.findViewById(R.id.tv_connect_errormsg);
        search_bar_view.setVisibility(View.GONE);

        final ChatHelper chatHelper = ChatHelper.getInstance();

        chatHelper.setSendMsgCallback(new IMsgCallback() {
            @Override
            public void HandleMsg(CmdInfo info, String json) {
                ReceiveMsgInfo receiveMsgInfo = info.of(ReceiveMsgInfo.class);
                MsgType type = MsgType.getType(receiveMsgInfo.Type);
                String userName = receiveMsgInfo.From.Name;
                if (chatHelper.isFriendUser(userName)) {
                    File file = null;
                    PathUtil pathUtil = PathUtil.getInstance();
                    String msgJson = "";
                    switch (type) {
                        case File:
                            file = pathUtil.getFilePath();
                            msgJson = getFileInfoJson(file, receiveMsgInfo);
                            break;
                        case Image:
                            file = pathUtil.getImagePath();
                            msgJson = getFileInfoJson(file, receiveMsgInfo);
                            break;
                        default:
                            msgJson = receiveMsgInfo.Msg;
                            break;
                    }
                    receiveMsgInfo.Msg = msgJson;
                    Msg msg = chatHelper.saveMsg(receiveMsgInfo);
                    handler.sendEmptyMessage(2);
                    chatHelper.sendMsg(msg, json);
                }
            }

            @Override
            public void HandleError(CmdInfo info, String msg) {
                CommonUtils.showLongToast("发生错误-" + info.Data.toString());
            }
        });
    }

    @Override
    protected void setUpView() {
        super.setUpView();
        // 注册上下文菜单
        registerForContextMenu(conversationListView);
        conversationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EMConversation conversation = conversationListView.getItem(position);
                String username = conversation.getName();
                String currentUserName = ChatHelper.getInstance().getCurrentUserName();

                if (currentUserName.equalsIgnoreCase(username))
                    Toast.makeText(getActivity(), R.string.Cant_chat_with_yourself, Toast.LENGTH_LONG).show();
                else {
                    // 进入聊天页面
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra(Constant.EXTRA_CHAT_ID, conversation.getChatId());
                    intent.putExtra(Constant.EXTRA_USER_ID, username);
                    intent.putExtra(Constant.EXTRA_MESSAGE_COUNT, conversation.getAllMsgCount());
                    intent.putExtra(Constant.EXTRA_CHAT_TO_ID, conversation.getToId());
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.push_left_in,
                            R.anim.push_left_out);
                }
            }
        });

        setErrorText(View.GONE);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        boolean deleteMessage = false;
        if (item.getItemId() == R.id.delete_message) {
            deleteMessage = true;
        } else if (item.getItemId() == R.id.delete_conversation) {
            deleteMessage = false;
        }
        EMConversation tobeDeleteCons = conversationListView.getItem(((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position);
        if (tobeDeleteCons == null) {
            return true;
        }
        try {
            int chatId = tobeDeleteCons.getChatId();
            ChatHelper.getInstance().deleteChat(chatId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        refresh();

        // 更新消息未读数
//        ((MainActivity) getActivity()).updateUnreadLabel();
        return true;
    }

    @Override
    protected void onConnectionDisconnected() {
        super.onConnectionDisconnected();
        if (CommonUtils.hasNetwork(getActivity())) {
            errorText.setText(R.string.can_not_connect_chat_server_connection);
        } else {
            errorText.setText(R.string.the_current_network);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.em_delete_message, menu);
    }

    public void setErrorText(int visibility) {
        if (errorItemContainer != null) {
            errorItemContainer.setVisibility(visibility);
        }
    }

    private String getFileInfoJson(File file, ReceiveMsgInfo info) {
        String json = "";
        try {
            FileInfo oldFile = JSON.parseObject(info.Msg, FileInfo.class);
            if (oldFile == null) return json;

            byte[] bytes = info.MsgOfBytes;
            if (bytes == null || bytes.length < 1) return json;
            File imageFile = FileUtils.saveFile(file.getAbsolutePath(), oldFile.getName(), bytes);
            if (imageFile == null) return json;

            Log.d(Tag, "the path is:" + file.getAbsolutePath());

            String thumbPath = ImageUtils.saveThumbImage(imageFile, ImageUtils.ThumbWidth, ImageUtils.ThumbHeight);
            FileInfo fileInfo = new FileInfo(imageFile, thumbPath);
            json = JSON.toJSONString(fileInfo);
            Log.d(Tag, "the json value is:" + json);
            return json;
        } catch (Exception e) {
            e.printStackTrace();
            return json;
        }
    }
}
