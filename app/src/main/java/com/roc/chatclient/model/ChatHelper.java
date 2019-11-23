package com.roc.chatclient.model;

import android.content.Context;

import com.roc.chatclient.db.DbManager;
import com.roc.chatclient.db.InviteMessgeDao;
import com.roc.chatclient.db.UserDao;
import com.roc.chatclient.entity.User;
import com.roc.chatclient.util.PreferenceManager;
import com.roc.chatclient.util.StringUtils;

import java.util.Map;

public class ChatHelper {
    protected static final String TAG = "ChatHelper";
    private Map<String, User> contactList;

//    private Map<String, RobotUser> robotList;

//    private UserProfileManager userProManager;

    private static ChatHelper instance = null;

    private ChatModel demoModel = null;
    private String username;

    private Context appContext;

    private InviteMessgeDao inviteMessgeDao;
    private UserDao userDao;

    //private CallReceiver callReceiver;

    private ChatHelper() {
    }

    public synchronized static ChatHelper getInstance() {
        if (instance == null) {
            instance = new ChatHelper();
        }
        return instance;
    }

    public void init(Context context) {
        //PreferenceManager.init(context);
        demoModel = new ChatModel(context);
        appContext = context;
        initDbDao();
    }

    private void initDbDao() {
        inviteMessgeDao = new InviteMessgeDao(appContext);
        userDao = new UserDao(appContext);
    }

    public ChatModel getModel() {
        return demoModel;
    }

    /**
     * set current username
     *
     * @param username
     */
    public void setCurrentUserName(String username) {
        this.username = username;
        demoModel.setCurrentUserName(username);
    }

    /**
     * get current user's id
     */
    public String getCurrentUsernName() {
        if (username == null) {
            username = demoModel.getCurrentUsernName();
        }
        return username;
    }

    public boolean isLogin() {
        String name = PreferenceManager.getInstance().getCurrentUsername();
        return !StringUtils.isEmpty(name);
    }


    synchronized void reset() {
//        isSyncingGroupsWithServer = false;
//        isSyncingContactsWithServer = false;
//        isSyncingBlackListWithServer = false;

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
    }
}
