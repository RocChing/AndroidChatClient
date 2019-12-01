package com.roc.chatclient.widget.chatrow;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
        if (fileInfo == null) {
            return;
        }
        setImageView(imageView, fileInfo.getThumbPath());
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

    private void setImageView(ImageView imageView, String key) {
        if (imageView == null) return;
        Bitmap bitmap = EaseImageCache.getInstance().get(key);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            bitmap = BitmapFactory.decodeFile(key);
            if (bitmap == null) return;
            EaseImageCache.getInstance().put(key, bitmap);
            imageView.setImageBitmap(bitmap);
        }
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
        Log.d("aaa", "the thumbernail Path is:" + thumbernailPath);
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
