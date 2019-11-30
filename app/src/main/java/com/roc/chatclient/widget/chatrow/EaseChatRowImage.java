package com.roc.chatclient.widget.chatrow;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.roc.chatclient.R;
import com.roc.chatclient.entity.Msg;
import com.roc.chatclient.model.FileInfo;
import com.roc.chatclient.ui.EaseShowBigImageActivity;
import com.roc.chatclient.util.EaseImageCache;
import com.roc.chatclient.util.ImageUtils;

import java.io.File;

public class EaseChatRowImage extends EaseChatRowFile {

    protected ImageView imageView;
    //    private EMImageMessageBody imgBody;


    public EaseChatRowImage(Context context, Msg message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflatView() {
        inflater.inflate(isSendMsg() ? R.layout.ease_row_sent_picture : R.layout.ease_row_received_picture, this);
    }

    @Override
    protected void onFindViewById() {
        percentageView = findViewById(R.id.percentage);
        imageView = findViewById(R.id.image);
        fileInfo = getFileInfo();
    }


    @Override
    protected void onSetUpView() {
//        imgBody = (EMImageMessageBody) message.getBody();
//         received messages
        if (fileInfo == null) {
            return;
        }
        if (!isSendMsg()) {
            progressBar.setVisibility(View.GONE);
            percentageView.setVisibility(View.GONE);
            imageView.setImageResource(R.drawable.ease_default_image);
            String thumbPath = fileInfo.getThumbPath();
            if (!new File(thumbPath).exists()) {
                // to make it compatible with thumbnail received in previous version
                thumbPath = ImageUtils.getThumbnailImagePath(fileInfo.getPath());
            }
            showImageView(thumbPath, imageView, fileInfo.getPath(), message);

            return;
        }

        String filePath = fileInfo.getPath();
        String thumbPath = fileInfo.getThumbPath();
        showImageView(thumbPath, imageView, filePath, message);
        handleSendMessage();
    }

    @Override
    protected void onUpdateView() {
        super.onUpdateView();
    }

    @Override
    protected void onBubbleClick() {
        Intent intent = new Intent(context, EaseShowBigImageActivity.class);
        File file = new File(fileInfo.getPath());
        if (file.exists()) {
            Uri uri = Uri.fromFile(file);
            intent.putExtra("uri", uri);
            context.startActivity(intent);
        }
//        else {
//            // The local full size pic does not exist yet.
//            // ShowBigImage needs to download it from the server
//            // first
//            intent.putExtra("secret", imgBody.getSecret());
//            intent.putExtra("remotepath", imgBody.getRemoteUrl());
//            intent.putExtra("localUrl", imgBody.getLocalUrl());
//        }
//        if (message != null && message.direct() == EMMessage.Direct.RECEIVE && !message.isAcked()
//                && message.getChatType() == ChatType.Chat) {
//            try {
//                EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

    }

    /**
     * load image into image view
     *
     * @param thumbernailPath
     * @param iv
     * @param
     * @return the image exists or not
     */
    private boolean showImageView(final String thumbernailPath, final ImageView iv, final String localFullSizePath, final Msg message) {
        // first check if the thumbnail image already loaded into cache
        Bitmap bitmap = EaseImageCache.getInstance().get(thumbernailPath);
        if (bitmap != null) {
            // thumbnail image is already loaded, reuse the drawable
            iv.setImageBitmap(bitmap);
            return true;
        } else {
            new AsyncTask<Object, Void, Bitmap>() {

                int width = ImageUtils.ThumbWidth;
                int height = ImageUtils.ThumbHeight;

                @Override
                protected Bitmap doInBackground(Object... args) {
                    File file = new File(thumbernailPath);
                    if (file.exists()) {
                        return ImageUtils.decodeScaleImage(thumbernailPath, width, height);
                    } else {
                        if (currentUserId == message.getSender()) {
                            if (localFullSizePath != null && new File(localFullSizePath).exists()) {
                                return ImageUtils.decodeScaleImage(localFullSizePath, width, height);
                            }
                        }
                        return null;
                    }
                }

                protected void onPostExecute(Bitmap image) {
                    if (image != null) {
                        iv.setImageBitmap(image);
                        EaseImageCache.getInstance().put(thumbernailPath, image);
                    } else {
//                        if (message.status() == EMMessage.Status.FAIL) {
//                            if (EaseCommonUtils.isNetWorkConnected(activity)) {
//                                new Thread(new Runnable() {
//
//                                    @Override
//                                    public void run() {
////                                        EMClient.getInstance().chatManager().downloadThumbnail(message);
//                                    }
//                                }).start();
//                            }
//                        }
                    }
                }
            }.execute();

            return true;
        }
    }
}
