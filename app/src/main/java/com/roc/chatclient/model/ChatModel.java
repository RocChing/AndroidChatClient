package com.roc.chatclient.model;

import android.content.Context;
import android.util.Log;

import com.roc.chatclient.db.MessageDao;
import com.roc.chatclient.entity.ChatMsg;
import com.roc.chatclient.entity.Msg;
import com.roc.chatclient.entity.User;
import com.roc.chatclient.util.DateUtils;
import com.roc.chatclient.util.PreferenceManager;
import com.roc.chatclient.db.UserDao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatModel {
    private UserDao dao = null;
    private MessageDao messageDao = null;
    private Context context = null;
    private Map<Key, Object> valueCache = new HashMap<Key, Object>();

    public ChatModel(Context ctx) {
        context = ctx;

        dao = new UserDao(ctx);
        messageDao = new MessageDao();
    }

    public boolean saveContactList(List<User> contactList) {
        dao.saveContactList(contactList);
        return true;
    }

    public Map<String, UserExtInfo> getContactList() {
        return dao.getContactList();
    }

    public boolean saveContact(UserExtInfo user) {
        UserExtInfo current = PreferenceManager.getInstance().getCurrentUser();
        if (current.Name.equalsIgnoreCase(user.Name)) return false;

        try {
            dao.saveContact(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getCurrentUserName() {
        return PreferenceManager.getInstance().getCurrentUsername();
    }

    public int getCurrentUserId() {
        return PreferenceManager.getInstance().getCurrentUserId();
    }

    public List<EMConversation> getEMConversationList() {
        List<ChatMsg> list = messageDao.getChatList();
        List<EMConversation> emConversations = new ArrayList<>();
        for (ChatMsg msg : list) {
            EMConversation conversation = new EMConversation();
            ReceiveMsgInfo info = new ReceiveMsgInfo();
            info.Type = 1;
            info.ReceiveTime = msg.getLastMsgTime();
            //info.From = new UserInfo(msg.getToId());
            info.Msg = msg.getLastMsg();
            conversation.setAllMsgCount(msg.getAllCount());
            conversation.setLastMsg(info);
            conversation.setName(msg.getName());
            conversation.setNickName(msg.getName());
            conversation.setToType(1);
            conversation.setUnreadMsgCount(msg.getUnReadCount());
            conversation.setChatId(msg.getId());
            conversation.setToId(msg.getToId());
            emConversations.add(conversation);
        }
//        Log.d("bbb", "the getEMConversationList list count is:" + emConversations.size());
        return emConversations;
    }

    public List<Msg> getChatMsgList(int pageIndex, int pageSize, int chatId) {
        return messageDao.getChatMsgList(pageIndex, pageSize, chatId);
    }

    public Msg saveMsg(ReceiveMsgInfo info) {
        return messageDao.saveMsg(info);
    }

    public void saveMsg(int chatId, MsgInfo info) {
        Msg msg = new Msg();
        msg.setChatId(chatId);
        msg.setContent(info.Msg);
        msg.setSender(info.From);
        msg.setSendTime(DateUtils.getFormatDate(new Date()));
        msg.setType(info.Type);
        messageDao.saveMsg(msg);
    }

    public ChatMsg saveChat(UserExtInfo user) {
        return messageDao.saveChat(user);
    }

    public void saveMsg(Msg info) {
        messageDao.saveMsg(info);
    }

    public void setMsgRead(int chatId) {
        messageDao.setMsgRead(chatId);
    }

    public void deleteChat(int chatId) {
        messageDao.deleteChat(chatId);
    }
//    public Map<String, RobotUser> getRobotList(){
//        UserDao dao = new UserDao(context);
//        return dao.getRobotUser();
//    }

//    public boolean saveRobotList(List<RobotUser> robotList){
//        UserDao dao = new UserDao(context);
//        dao.saveRobotUser(robotList);
//        return true;
//    }

    public void setSettingMsgNotification(boolean paramBoolean) {
        PreferenceManager.getInstance().setSettingMsgNotification(paramBoolean);
        valueCache.put(Key.VibrateAndPlayToneOn, paramBoolean);
    }

    public boolean getSettingMsgNotification() {
        Object val = valueCache.get(Key.VibrateAndPlayToneOn);

        if (val == null) {
            val = PreferenceManager.getInstance().getSettingMsgNotification();
            valueCache.put(Key.VibrateAndPlayToneOn, val);
        }

        return (Boolean) (val != null ? val : true);
    }

    public void setSettingMsgSound(boolean paramBoolean) {
        PreferenceManager.getInstance().setSettingMsgSound(paramBoolean);
        valueCache.put(Key.PlayToneOn, paramBoolean);
    }

    public boolean getSettingMsgSound() {
        Object val = valueCache.get(Key.PlayToneOn);

        if (val == null) {
            val = PreferenceManager.getInstance().getSettingMsgSound();
            valueCache.put(Key.PlayToneOn, val);
        }

        return (Boolean) (val != null ? val : true);
    }

    public void setSettingMsgVibrate(boolean paramBoolean) {
        PreferenceManager.getInstance().setSettingMsgVibrate(paramBoolean);
        valueCache.put(Key.VibrateOn, paramBoolean);
    }

    public boolean getSettingMsgVibrate() {
        Object val = valueCache.get(Key.VibrateOn);

        if (val == null) {
            val = PreferenceManager.getInstance().getSettingMsgVibrate();
            valueCache.put(Key.VibrateOn, val);
        }

        return (Boolean) (val != null ? val : true);
    }

    public void setSettingMsgSpeaker(boolean paramBoolean) {
        PreferenceManager.getInstance().setSettingMsgSpeaker(paramBoolean);
        valueCache.put(Key.SpakerOn, paramBoolean);
    }

    public boolean getSettingMsgSpeaker() {
        Object val = valueCache.get(Key.SpakerOn);

        if (val == null) {
            val = PreferenceManager.getInstance().getSettingMsgSpeaker();
            valueCache.put(Key.SpakerOn, val);
        }

        return (Boolean) (val != null ? val : true);
    }


//    public void setDisabledGroups(List<String> groups){
//        if(dao == null){
//            dao = new UserDao(context);
//        }
//
//        List<String> list = new ArrayList<String>();
//        list.addAll(groups);
//        //直接删除groups的内容，可能会有并发的错误
//        synchronized (list) {
//            for(int i = 0; i < list.size(); i++){
//                if(EaseAtMessageHelper.get().getAtMeGroups().contains(list.get(i))){
//                    list.remove(i);
//                    i--;
//                }
//            }
//        }
//
//        dao.setDisabledGroups(list);
//        valueCache.put(Key.DisabledGroups, list);
//    }

    public List<String> getDisabledGroups() {
        Object val = valueCache.get(Key.DisabledGroups);

        if (val == null) {
            val = dao.getDisabledGroups();
            valueCache.put(Key.DisabledGroups, val);
        }

        return (List<String>) val;
    }

    public void setDisabledIds(List<String> ids) {
        dao.setDisabledIds(ids);
        valueCache.put(Key.DisabledIds, ids);
    }

    public List<String> getDisabledIds() {
        Object val = valueCache.get(Key.DisabledIds);

        if (val == null) {
            val = dao.getDisabledIds();
            valueCache.put(Key.DisabledIds, val);
        }

        return (List<String>) val;
    }

    public void setGroupsSynced(boolean synced) {
        PreferenceManager.getInstance().setGroupsSynced(synced);
    }

    public boolean isGroupsSynced() {
        return PreferenceManager.getInstance().isGroupsSynced();
    }

    public void setContactSynced(boolean synced) {
        PreferenceManager.getInstance().setContactSynced(synced);
    }

    public boolean isContactSynced() {
        return PreferenceManager.getInstance().isContactSynced();
    }

    public void setBlacklistSynced(boolean synced) {
        PreferenceManager.getInstance().setBlacklistSynced(synced);
    }

    public boolean isBacklistSynced() {
        return PreferenceManager.getInstance().isBacklistSynced();
    }

    public void allowChatroomOwnerLeave(boolean value) {
        PreferenceManager.getInstance().setSettingAllowChatroomOwnerLeave(value);
    }

    public boolean isChatroomOwnerLeaveAllowed() {
        return PreferenceManager.getInstance().getSettingAllowChatroomOwnerLeave();
    }

    public void setDeleteMessagesAsExitGroup(boolean value) {
        PreferenceManager.getInstance().setDeleteMessagesAsExitGroup(value);
    }

    public boolean isDeleteMessagesAsExitGroup() {
        return PreferenceManager.getInstance().isDeleteMessagesAsExitGroup();
    }

    public void setAutoAcceptGroupInvitation(boolean value) {
        PreferenceManager.getInstance().setAutoAcceptGroupInvitation(value);
    }

    public boolean isAutoAcceptGroupInvitation() {
        return PreferenceManager.getInstance().isAutoAcceptGroupInvitation();
    }


    public void setAdaptiveVideoEncode(boolean value) {
        PreferenceManager.getInstance().setAdaptiveVideoEncode(value);
    }

    public boolean isAdaptiveVideoEncode() {
        return PreferenceManager.getInstance().isAdaptiveVideoEncode();
    }

    enum Key {
        VibrateAndPlayToneOn,
        VibrateOn,
        PlayToneOn,
        SpakerOn,
        DisabledGroups,
        DisabledIds
    }
}
