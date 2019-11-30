package com.roc.chatclient.ui;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.roc.chatclient.R;
import com.roc.chatclient.util.EaseImageCache;
import com.roc.chatclient.util.ImageUtils;
import com.roc.chatclient.widget.EaseLoadLocalBigImgTask;
import com.roc.chatclient.widget.photoview.EasePhotoView;

import java.io.File;


public class EaseShowBigImageActivity extends EaseBaseActivity {

    private EasePhotoView image;
    private ProgressBar loadLocalPb;
    private int default_res = R.drawable.ease_default_image;
    private Bitmap bitmap;

    private String TAG = "EaseShowBigImageActivity";
    @Override
    protected void onCreate(Bundle arg0) {
        setContentView(R.layout.ease_activity_show_big_image);
        super.onCreate(arg0);

        image = (EasePhotoView) findViewById(R.id.image);
        loadLocalPb = (ProgressBar) findViewById(R.id.pb_load_local);
        Uri uri = getIntent().getParcelableExtra("uri");
//        default_res = getIntent().getIntExtra("default_image", R.drawable.ease_default_avatar);
//        String remotepath = getIntent().getExtras().getString("remotepath");
//        localFilePath = getIntent().getExtras().getString("localUrl");
//        String secret = getIntent().getExtras().getString("secret");
//        EMLog.d(TAG, "show big image uri:" + uri + " remotepath:" + remotepath);

        if (uri != null && new File(uri.getPath()).exists()) {
            Log.d(TAG, "showbigimage file exists. directly show it");
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            // int screenWidth = metrics.widthPixels;
            // int screenHeight =metrics.heightPixels;
            bitmap = EaseImageCache.getInstance().get(uri.getPath());
            if (bitmap == null) {
                EaseLoadLocalBigImgTask task = new EaseLoadLocalBigImgTask(this, uri.getPath(), image, loadLocalPb, ImageUtils.SCALE_IMAGE_WIDTH,
                        ImageUtils.SCALE_IMAGE_HEIGHT);
                if (android.os.Build.VERSION.SDK_INT > 10) {
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    task.execute();
                }
            } else {
                image.setImageBitmap(bitmap);
            }

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
