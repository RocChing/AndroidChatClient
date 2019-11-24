package com.roc.chatclient.model;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.roc.chatclient.db.DbManager;
import com.roc.chatclient.db.InviteMessgeDao;
import com.roc.chatclient.db.UserDao;
import com.roc.chatclient.entity.User;
import com.roc.chatclient.receiver.IMsgCallback;
import com.roc.chatclient.receiver.MsgString;
import com.roc.chatclient.receiver.ReceiveMsgReceiver;
import com.roc.chatclient.service.MsgService;
import com.roc.chatclient.util.PreferenceManager;
import com.roc.chatclient.util.StringUtils;

import java.util.Hashtable;
import java.util.Map;

public class ChatHelper {

    protected static final String TAG = "ChatHelper";
    private Map<String, UserExtInfo> contactList;

//    private Map<String, RobotUser> robotList;
//    private UserProfileManager userProManager;

    private static ChatHelper instance = null;

    private ChatModel demoModel = null;
    private Context appContext;

    private ReceiveMsgReceiver msgReceiver;

    //private CallReceiver callReceiver;

    private ChatHelper() {
    }

    /**
     * data sync listener
     */
    static public interface DataSyncListener {
        /**
         * sync complete
         *
         * @param success true：data sync successful，false: failed to sync data
         */
        public void onSyncComplete(boolean success);
    }

    public synchronized static ChatHelper getInstance() {
        if (instance == null) {
            instance = new ChatHelper();
        }
        return instance;
    }

    public void init(Context context) {
        appContext = context;
        initReceiver();
        demoModel = new ChatModel(context);
    }

    private void initReceiver() {
        msgReceiver = new ReceiveMsgReceiver();
        IntentFilter filter = new IntentFilter(MsgString.ReceiveMsg);
        LocalBroadcastManager.getInstance(appContext).registerReceiver(msgReceiver, filter);
    }

    public void setMsgCallback(IMsgCallback msgCallback) {
        msgReceiver.setMsgCallback(msgCallback);
    }

    public ChatModel getModel() {
        return demoModel;
    }

    /**
     * get current user's id
     */
    public String getCurrentUsernName() {
        return demoModel.getCurrentUsernName();
    }

    public boolean isLogin() {
        String name = PreferenceManager.getInstance().getCurrentUsername();
        return !StringUtils.isEmpty(name);
    }

    /**
     * get contact list
     *
     * @return
     */
    public Map<String, UserExtInfo> getContactList() {
        if (isLogin() && contactList == null) {
            contactList = demoModel.getContactList();
        }

        // return a empty non-null object to avoid app crash
        if (contactList == null) {
            return new Hashtable<String, UserExtInfo>();
        }

        return contactList;
    }

    public synchronized void resetData() {
        DbManager.getInstance().closeDB();//切换数据库
        PreferenceManager.getInstance().removeCurrentUserInfo();//移除用户信息

        contactList = null;
    }

    synchronized void reset() {
//        isSyncingGroupsWithServer = false;
//        isSyncingContactsWithServer = false;
//        isSyncingBlackListWithServer = false;
        LocalBroadcastManager.getInstance(appContext).unregisterReceiver(msgReceiver);

        demoModel.setGroupsSynced(false);
        demoModel.setContactSynced(false);
        demoModel.setBlacklistSynced(false);

//        isGroupsSyncedWithServer = false;
//        isContactsSyncedWithServer = false;
//        isBlackListSyncedWithServer = false;
//
//        isGroupAndContactListenerRegisted = false;

//        setContactList(null);
//        setRobotList(null);
//        getUserProfileManager().reset();
        DbManager.getInstance().closeDB();
        instance = null;
    }
}
