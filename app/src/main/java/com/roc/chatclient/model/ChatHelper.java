package com.roc.chatclient.model;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.alibaba.fastjson.JSON;
import com.roc.chatclient.R;
import com.roc.chatclient.db.DbManager;
import com.roc.chatclient.entity.Msg;
import com.roc.chatclient.receiver.IMsgCallback;
import com.roc.chatclient.receiver.MsgString;
import com.roc.chatclient.receiver.ReceiveMsgReceiver;
import com.roc.chatclient.util.PreferenceManager;
import com.roc.chatclient.util.StringUtils;

import java.util.Hashtable;
import java.util.List;
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

    private EaseEmojiconInfoProvider emojiconInfoProvider;
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
        PreferenceManager.init(context);
        demoModel = new ChatModel(appContext);
        setEmojiconInfoProvider();
    }

    private void initReceiver() {
        msgReceiver = new ReceiveMsgReceiver();
        IntentFilter filter = new IntentFilter(MsgString.ReceiveMsg);
        LocalBroadcastManager.getInstance(appContext).registerReceiver(msgReceiver, filter);
    }

    public void setMsgCallback(IMsgCallback msgCallback) {
        msgReceiver.setMsgCallback(msgCallback);
    }

    public void setSendMsgCallback(IMsgCallback sendMsgCallback) {
        msgReceiver.setSendMsgCallback(sendMsgCallback);
    }

    public void setCheckMsgCallback(IMsgCallback checkMsgCallback) {
        msgReceiver.setCheckMsgCallback(checkMsgCallback);
    }

    public void sendMsg(CmdType type, Object data) {
        String validString = appContext.getString(R.string.validString);
        String json = JSON.toJSONString(new CmdInfo(validString, type, data));

        Log.d(TAG, validString);
        Log.d(TAG, json);

        Intent intent = new Intent();
        intent.setAction(MsgString.SendMsg);
        intent.putExtra(MsgString.Default_Args, json);
        LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
    }

    public ChatModel getModel() {
        return demoModel;
    }

    /**
     * get current user's id
     */
    public String getCurrentUserName() {
        return demoModel.getCurrentUserName();
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
            contactList = new Hashtable<String, UserExtInfo>();
        }

        //add me
        UserExtInfo user = PreferenceManager.getInstance().getCurrentUser();
        if (!contactList.containsKey(user.Name)) {
            contactList.put(user.Name, user);
        }
        return contactList;
    }

    public UserExtInfo getUserInfo(String name) {
        if (contactList != null) {
            if (contactList.containsKey(name)) {
                return contactList.get(name);
            }
        }
        return new UserExtInfo();
    }

    public List<EMConversation> getEMConversationList() {
        return demoModel.getEMConversationList();
    }

    public List<Msg> getChatMsgList(int pageIndex, int pageSize, int chatId) {
        return demoModel.getChatMsgList(pageIndex, pageSize, chatId);
    }

    public void saveMsg(ReceiveMsgInfo info) {
        demoModel.saveMsg(info);
    }

    public synchronized void resetData() {
        DbManager.getInstance().closeDB();//切换数据库
        PreferenceManager.getInstance().removeCurrentUserInfo();//移除用户信息

        contactList = null;
    }

    public synchronized void resetOnlyData() {
        contactList = null;
        msgReceiver.setMsgCallback(null);
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

    private void setEmojiconInfoProvider() {
        this.emojiconInfoProvider = new EaseEmojiconInfoProvider() {

            @Override
            public EaseEmojicon getEmojiconInfo(String emojiconIdentityCode) {
                EaseEmojiconGroupEntity data = EmojiconExampleGroupData.getData();
                for (EaseEmojicon emojicon : data.getEmojiconList()) {
                    if (emojicon.getIdentityCode().equals(emojiconIdentityCode)) {
                        return emojicon;
                    }
                }
                return null;
            }

            @Override
            public Map<String, Object> getTextEmojiconMapping() {
                return null;
            }
        };
    }

    /**
     * Emojicon provider
     *
     * @return
     */
    public EaseEmojiconInfoProvider getEmojiconInfoProvider() {
        return emojiconInfoProvider;
    }

    /**
     * set Emojicon provider
     *
     * @param emojiconInfoProvider
     */
    public void setEmojiconInfoProvider(EaseEmojiconInfoProvider emojiconInfoProvider) {
        this.emojiconInfoProvider = emojiconInfoProvider;
    }

    /**
     * Emojicon provider
     */
    public interface EaseEmojiconInfoProvider {
        /**
         * return EaseEmojicon for input emojiconIdentityCode
         *
         * @param emojiconIdentityCode
         * @return
         */
        EaseEmojicon getEmojiconInfo(String emojiconIdentityCode);

        /**
         * get Emojicon map, key is the text of emoji, value is the resource id or local path of emoji icon(can't be URL on internet)
         *
         * @return
         */
        Map<String, Object> getTextEmojiconMapping();
    }
}
