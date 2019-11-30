package com.roc.chatclient.util;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.roc.chatclient.ChatApplication;
import com.roc.chatclient.entity.User;
import com.roc.chatclient.model.MsgType;
import com.roc.chatclient.model.ReceiveMsgInfo;
import com.roc.chatclient.model.UserExtInfo;

import java.util.ArrayList;
import java.util.List;

import com.roc.chatclient.R;

public final class CommonUtils {

    private static final Spannable.Factory spannableFactory = Spannable.Factory
            .getInstance();

    public static void showLongToast(String pMsg) {
        Toast.makeText(ChatApplication.getInstance(), pMsg, Toast.LENGTH_LONG).show();
    }

    public static void showShortToast(String pMsg) {
        Toast.makeText(ChatApplication.getInstance(), pMsg, Toast.LENGTH_SHORT).show();
    }

    /**
     * set initial letter of according user's nickname( username if no nickname)
     *
     * @param user
     */
    public static void setUserInitialLetter(User user) {
        final String DefaultLetter = "#";
        String letter = DefaultLetter;

        final class GetInitialLetter {
            String getLetter(String name) {
                if (TextUtils.isEmpty(name)) {
                    return DefaultLetter;
                }
                char char0 = name.toLowerCase().charAt(0);
                if (Character.isDigit(char0)) {
                    return DefaultLetter;
                }
                ArrayList<HanziToPinyin.Token> l = HanziToPinyin.getInstance().get(name.substring(0, 1));
                if (l != null && l.size() > 0 && l.get(0).target.length() > 0) {
                    HanziToPinyin.Token token = l.get(0);
                    String letter = token.target.substring(0, 1).toUpperCase();
                    char c = letter.charAt(0);
                    if (c < 'A' || c > 'Z') {
                        return DefaultLetter;
                    }
                    return letter;
                }
                return DefaultLetter;
            }
        }

        if (!TextUtils.isEmpty(user.NickName)) {
            letter = new GetInitialLetter().getLetter(user.NickName);
            user.setInitialLetter(letter);
            return;
        }
        if (letter == DefaultLetter && !TextUtils.isEmpty(user.Name)) {
            letter = new GetInitialLetter().getLetter(user.Name);
        }
        user.setInitialLetter(letter);
    }

    public static void setUserInitialLetter(UserExtInfo user) {
        final String DefaultLetter = "#";
        String letter = DefaultLetter;

        final class GetInitialLetter {
            String getLetter(String name) {
                if (TextUtils.isEmpty(name)) {
                    return DefaultLetter;
                }
                char char0 = name.toLowerCase().charAt(0);
                if (Character.isDigit(char0)) {
                    return DefaultLetter;
                }
                ArrayList<HanziToPinyin.Token> l = HanziToPinyin.getInstance().get(name.substring(0, 1));
                if (l != null && l.size() > 0 && l.get(0).target.length() > 0) {
                    HanziToPinyin.Token token = l.get(0);
                    String letter = token.target.substring(0, 1).toUpperCase();
                    char c = letter.charAt(0);
                    if (c < 'A' || c > 'Z') {
                        return DefaultLetter;
                    }
                    return letter;
                }
                return DefaultLetter;
            }
        }

        if (!TextUtils.isEmpty(user.NickName)) {
            letter = new GetInitialLetter().getLetter(user.NickName);
            user.setInitialLetter(letter);
            return;
        }
        if (letter == DefaultLetter && !TextUtils.isEmpty(user.Name)) {
            letter = new GetInitialLetter().getLetter(user.Name);
        }
        user.setInitialLetter(letter);
    }

    static String getString(Context context, int resId) {
        return context.getResources().getString(resId);
    }

    /**
     * Get digest according message type and content
     *
     * @param message
     * @param context
     * @return
     */
    public static String getMessageDigest(ReceiveMsgInfo message, Context context) {
        String digest = "";
        MsgType type = MsgType.getType(message.Type);

        switch (type) {
            case Text:
                digest = message.Msg;
                break;
        }
        return digest;
    }

    public static boolean hasNetwork(Context var0) {
        if (var0 != null) {
            ConnectivityManager var1 = (ConnectivityManager) var0.getSystemService("connectivity");
            NetworkInfo var2 = var1.getActiveNetworkInfo();
            return var2 != null ? var2.isAvailable() : false;
        } else {
            return false;
        }
    }

    public static boolean isSingleActivity(Context var0) {
        ActivityManager var1 = (ActivityManager) var0.getSystemService("activity");
        List var2 = var1.getRunningTasks(1);
        return ((ActivityManager.RunningTaskInfo) var2.get(0)).numRunning == 1;
    }

    /**
     * set user avatar
     *
     * @param avatar
     */
    public static void setUserAvatar(Context context, String avatar, ImageView imageView) {
        if (!StringUtils.isEmpty(avatar)) {
            try {
                int resId = ResourcesUtils.getDrawableId(context, avatar);
//                Log.d("avatar", "the resId value is:" + resId);
                Glide.with(context)
                        .load(resId)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ease_default_avatar)
                        .error(R.drawable.ease_default_avatar)
                        .bitmapTransform(new CropCircleTransformation(context))
                        .into(imageView);
            } catch (Exception e) {
                Log.d("", e.getMessage());
            }
        } else {
            Glide.with(context).load(R.drawable.ease_default_avatar).into(imageView);
        }
    }

    public static void setUserNick(UserExtInfo user, TextView textView) {
        if (textView != null) {
            if (user != null && !StringUtils.isEmpty(user.NickName)) {
                textView.setText(user.NickName);
            } else {
                textView.setText(user.Name);
            }
        }
    }

    public static void setUserNick(String nickName, String name, TextView textView) {
        if (textView != null) {
            if (!StringUtils.isEmpty(nickName)) {
                textView.setText(nickName);
            } else {
                textView.setText(name);
            }
        }
    }

    /**
     * check if sdcard exist
     *
     * @return
     */
    public static boolean isSdcardExist() {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }
}
