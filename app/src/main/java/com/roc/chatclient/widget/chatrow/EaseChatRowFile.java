package com.roc.chatclient.widget.chatrow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.roc.chatclient.entity.Msg;
import com.roc.chatclient.R;
import com.roc.chatclient.model.FileInfo;
import com.roc.chatclient.util.FileUtils;

import java.io.File;

public class EaseChatRowFile extends EaseChatRow {

    protected TextView fileNameView;
    protected TextView fileSizeView;
    protected TextView fileStateView;

    protected FileInfo fileInfo;

//    protected EMCallBack sendfileCallBack;

    protected boolean isNotifyProcessed;
//    private EMNormalFileMessageBody fileMessageBody;

    public EaseChatRowFile(Context context, Msg message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflatView() {
        inflater.inflate(isSendMsg() ?
                R.layout.ease_row_sent_file : R.layout.ease_row_received_file, this);
    }

    @Override
    protected void onFindViewById() {
        fileNameView = findViewById(R.id.tv_file_name);
        fileSizeView = findViewById(R.id.tv_file_size);
        fileStateView = findViewById(R.id.tv_file_state);
        percentageView = findViewById(R.id.percentage);

        fileInfo = getFileInfo();
    }

    protected FileInfo getFileInfo() {
        FileInfo fileInfo;
        try {
            fileInfo = JSON.parseObject(message.getContent(), FileInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
            fileInfo = null;
        }
        return fileInfo;
    }

    @Override
    protected void onSetUpView() {
//        fileMessageBody = (EMNormalFileMessageBody) message.getBody();
        String filePath = fileInfo.getPath(); //fileMessageBody.getLocalUrl();
        fileNameView.setText(fileInfo.getName());
        fileSizeView.setText(fileInfo.getStringSize());
        if (!isSendMsg()) {
            File file = new File(filePath);
            if (file != null && file.exists()) {
                fileStateView.setText(R.string.Have_downloaded);
            } else {
                fileStateView.setText(R.string.Did_not_download);
            }
            return;
        }

        // until here, to sending message
        handleSendMessage();
    }

    /**
     * handle sending message
     */
    protected void handleSendMessage() {
        if (progressBar != null) progressBar.setVisibility(View.INVISIBLE);
        if (percentageView != null) percentageView.setVisibility(View.INVISIBLE);
        if (statusView != null) statusView.setVisibility(View.INVISIBLE);
//        setMessageSendCallback();
//        switch (message.status()) {
//            case SUCCESS:
//                progressBar.setVisibility(View.INVISIBLE);
//                if (percentageView != null)
//                    percentageView.setVisibility(View.INVISIBLE);
//                statusView.setVisibility(View.INVISIBLE);
//                break;
//            case FAIL:
//                progressBar.setVisibility(View.INVISIBLE);
//                if (percentageView != null)
//                    percentageView.setVisibility(View.INVISIBLE);
//                statusView.setVisibility(View.VISIBLE);
//                break;
//            case INPROGRESS:
//                progressBar.setVisibility(View.VISIBLE);
//                if (percentageView != null) {
//                    percentageView.setVisibility(View.VISIBLE);
//                    percentageView.setText(message.progress() + "%");
//                }
//                statusView.setVisibility(View.INVISIBLE);
//                break;
//            default:
//                progressBar.setVisibility(View.INVISIBLE);
//                if (percentageView != null)
//                    percentageView.setVisibility(View.INVISIBLE);
//                statusView.setVisibility(View.VISIBLE);
//                break;
//        }
    }


    @Override
    protected void onUpdateView() {
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onBubbleClick() {
        String filePath = fileInfo.getPath();
        File file = new File(filePath);
        if (file != null && file.exists()) {
            // open files if it exist
            FileUtils.openFile(file, (Activity) context);
        } else {
            // download the file
//            context.startActivity(new Intent(context, EaseShowNormalFileActivity.class).putExtra("msgbody", message.getBody()));
        }
//        if (message.direct() == EMMessage.Direct.RECEIVE && !message.isAcked() && message.getChatType() == ChatType.Chat) {
//            try {
//                EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
//            } catch (HyphenateException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
    }
}
