package com.roc.chatclient.ui.fragment;

import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.roc.chatclient.R;
import com.roc.chatclient.db.InviteMessageDao;
import com.roc.chatclient.model.ChatHelper;
import com.roc.chatclient.model.CmdInfo;
import com.roc.chatclient.model.EMConversation;
import com.roc.chatclient.model.ReceiveMsgInfo;
import com.roc.chatclient.receiver.IMsgCallback;
import com.roc.chatclient.util.CommonUtils;

public class ConversationListFragment extends EaseConversationListFragment {
    private TextView errorText;

    @Override
    protected void initView() {
        super.initView();
        View errorView = View.inflate(getActivity(), R.layout.em_chat_neterror_item, null);
        errorItemContainer.addView(errorView);
        errorText = errorView.findViewById(R.id.tv_connect_errormsg);
        search_bar_view.setVisibility(View.GONE);

        ChatHelper.getInstance().setMsgCallback(new IMsgCallback() {
            @Override
            public void HandleMsg(CmdInfo info, String msg) {
                ReceiveMsgInfo receiveMsgInfo = info.of(ReceiveMsgInfo.class);
                ChatHelper.getInstance().saveMsg(receiveMsgInfo);
                handler.sendEmptyMessage(2);
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

                if (username.equals(currentUserName))
                    Toast.makeText(getActivity(), R.string.Cant_chat_with_yourself, Toast.LENGTH_LONG).show();
                else {
                    // 进入聊天页面
//                    Intent intent = new Intent(getActivity(), ChatActivity.class);
////                    if(conversation.isGroup()){
////                        if(conversation.getType() == EMConversationType.ChatRoom){
////                            // it's group chat
////                            intent.putExtra(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_CHATROOM);
////                        }else{
////                            intent.putExtra(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_GROUP);
////                        }
////                    }
//                    // it's single chat
//                    intent.putExtra(Constant.EXTRA_USER_ID, username);
//                    startActivity(intent);
//                    getActivity().overridePendingTransition(R.anim.push_left_in,
//                            R.anim.push_left_out);
                }
            }
        });
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
            // 删除此会话
            // EMClient.getInstance().chatManager().deleteConversation(tobeDeleteCons.getUserName(), deleteMessage);
            InviteMessageDao inviteMessgeDao = new InviteMessageDao(getActivity());
            inviteMessgeDao.deleteMessage(tobeDeleteCons.getName());
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
}
