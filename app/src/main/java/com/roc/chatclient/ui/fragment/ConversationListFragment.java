package com.roc.chatclient.ui.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.roc.chatclient.R;

public class ConversationListFragment extends EaseConversationListFragment {
    private TextView errorText;

    @Override
    protected void initView() {
        super.initView();
        View errorView = (LinearLayout) View.inflate(getActivity(), R.layout.em_chat_neterror_item, null);
        errorItemContainer.addView(errorView);
        errorText = (TextView) errorView.findViewById(R.id.tv_connect_errormsg);
        search_bar_view.setVisibility(View.GONE);

    }

    @Override
    protected void setUpView() {
        super.setUpView();
        // 注册上下文菜单
        registerForContextMenu(conversationListView);
//        conversationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                EMConversation conversation = conversationListView.getItem(position);
//                String username = conversation.getUserName();
//                if (username.equals(EMClient.getInstance().getCurrentUser()))
//                    Toast.makeText(getActivity(), R.string.Cant_chat_with_yourself, 0).show();
//                else {
//                    // 进入聊天页面
//                    Intent intent = new Intent(getActivity(), ChatActivity.class);
//                    if(conversation.isGroup()){
//                        if(conversation.getType() == EMConversationType.ChatRoom){
//                            // it's group chat
//                            intent.putExtra(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_CHATROOM);
//                        }else{
//                            intent.putExtra(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_GROUP);
//                        }
//
//                    }
//                    // it's single chat
//                    intent.putExtra(Constant.EXTRA_USER_ID, username);
//                    startActivity(intent);
//                    getActivity().overridePendingTransition(R.anim.push_left_in,
//                            R.anim.push_left_out);
//                }
//            }
//        });
    }
}
