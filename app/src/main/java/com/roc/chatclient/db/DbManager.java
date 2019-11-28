package com.roc.chatclient.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.roc.chatclient.ChatApplication;
import com.roc.chatclient.entity.InviteMessage;
import com.roc.chatclient.entity.InviteMessage.InviteMessageStatus;
import com.roc.chatclient.entity.User;
import com.roc.chatclient.model.Constant;
import com.roc.chatclient.model.UserExtInfo;
import com.roc.chatclient.util.CommonUtils;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class DbManager {
    static private DbManager dbMgr = new DbManager();
    private DbOpenHelper dbHelper;

    private DbManager() {
        dbHelper = DbOpenHelper.getInstance(ChatApplication.getInstance());
    }

    public static synchronized DbManager getInstance() {
        if (dbMgr == null) {
            dbMgr = new DbManager();
        }
        return dbMgr;
    }

    public synchronized DbOpenHelper getDb() {
        if (dbHelper == null) {
            dbHelper = DbOpenHelper.getInstance(ChatApplication.getInstance());
        }
        return dbHelper;
    }

    /**
     * save contact list
     *
     * @param contactList
     */
    synchronized public void saveContactList(List<User> contactList) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(UserDao.TABLE_NAME, null, null);
            for (User user : contactList) {
                ContentValues values = new ContentValues();
                values.put(UserDao.COLUMN_NAME_ID, user.Name);
                if (user.NickName != null)
                    values.put(UserDao.COLUMN_NAME_NICK, user.NickName);
                if (user.Avatar != null)
                    values.put(UserDao.COLUMN_NAME_AVATAR, user.Avatar);
                if (user.Id > 0)
                    values.put(UserDao.COLUMN_ID, user.Id);
                db.replace(UserDao.TABLE_NAME, null, values);
            }
        }
    }

    /**
     * get contact list
     *
     * @return
     */
    synchronized public Map<String, UserExtInfo> getContactList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Map<String, UserExtInfo> users = new Hashtable<String, UserExtInfo>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + UserDao.TABLE_NAME /* + " desc" */, null);
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(UserDao.COLUMN_ID));
                String username = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_ID));
                String nick = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_NICK));
                String avatar = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_AVATAR));
                UserExtInfo user = new UserExtInfo();

                user.NickName = nick;
                user.Avatar = avatar;
                user.Name = username;
                user.Id = id;
                if (username.equals(Constant.NEW_FRIENDS_USERNAME) || username.equals(Constant.GROUP_USERNAME)
                        || username.equals(Constant.CHAT_ROOM) || username.equals(Constant.CHAT_ROBOT)) {
                    user.setInitialLetter("");
                } else {
                    CommonUtils.setUserInitialLetter(user);
                }
                users.put(username, user);
            }
            cursor.close();
        }
        return users;
    }

    /**
     * delete a contact
     *
     * @param username
     */
    synchronized public void deleteContact(String username) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(UserDao.TABLE_NAME, UserDao.COLUMN_NAME_ID + " = ?", new String[]{username});
        }
    }

    /**
     * save a contact
     *
     * @param user
     */
    synchronized public void saveContact(UserExtInfo user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserDao.COLUMN_NAME_ID, user.Name);
        if (user.NickName != null)
            values.put(UserDao.COLUMN_NAME_NICK, user.NickName);
        if (user.Avatar != null)
            values.put(UserDao.COLUMN_NAME_AVATAR, user.Avatar);
        if (user.Id > 0) {
            values.put(UserDao.COLUMN_ID, user.Id);
        }
        if (db.isOpen()) {
            db.replace(UserDao.TABLE_NAME, null, values);
        }
    }

    public void setDisabledGroups(List<String> groups) {
        setList(UserDao.COLUMN_NAME_DISABLED_GROUPS, groups);
    }

    public List<String> getDisabledGroups() {
        return getList(UserDao.COLUMN_NAME_DISABLED_GROUPS);
    }

    public void setDisabledIds(List<String> ids) {
        setList(UserDao.COLUMN_NAME_DISABLED_IDS, ids);
    }

    public List<String> getDisabledIds() {
        return getList(UserDao.COLUMN_NAME_DISABLED_IDS);
    }

    synchronized private void setList(String column, List<String> strList) {
        StringBuilder strBuilder = new StringBuilder();

        for (String hxid : strList) {
            strBuilder.append(hxid).append("$");
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(column, strBuilder.toString());

            db.update(UserDao.PREF_TABLE_NAME, values, null, null);
        }
    }

    synchronized private List<String> getList(String column) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select " + column + " from " + UserDao.PREF_TABLE_NAME, null);
        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }

        String strVal = cursor.getString(0);
        if (strVal == null || strVal.equals("")) {
            return null;
        }

        cursor.close();

        String[] array = strVal.split("$");

        if (array != null && array.length > 0) {
            List<String> list = new ArrayList<String>();
            for (String str : array) {
                list.add(str);
            }

            return list;
        }

        return null;
    }

    /**
     * save a message
     *
     * @param message
     * @return return cursor of the message
     */
    public synchronized Integer saveMessage(InviteMessage message) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int id = -1;
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(InviteMessageDao.COLUMN_NAME_FROM, message.getFrom());
            values.put(InviteMessageDao.COLUMN_NAME_GROUP_ID, message.getGroupId());
            values.put(InviteMessageDao.COLUMN_NAME_GROUP_Name, message.getGroupName());
            values.put(InviteMessageDao.COLUMN_NAME_REASON, message.getReason());
            values.put(InviteMessageDao.COLUMN_NAME_TIME, message.getTime());
            values.put(InviteMessageDao.COLUMN_NAME_STATUS, message.getStatus().ordinal());
            values.put(InviteMessageDao.COLUMN_NAME_GROUPINVITER, message.getGroupInviter());
            db.insert(InviteMessageDao.TABLE_NAME, null, values);

            Cursor cursor = db.rawQuery("select last_insert_rowid() from " + InviteMessageDao.TABLE_NAME, null);
            if (cursor.moveToFirst()) {
                id = cursor.getInt(0);
            }

            cursor.close();
        }
        return id;
    }

    /**
     * update message
     *
     * @param msgId
     * @param values
     */
    synchronized public void updateMessage(int msgId, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.update(InviteMessageDao.TABLE_NAME, values, InviteMessageDao.COLUMN_NAME_ID + " = ?", new String[]{String.valueOf(msgId)});
        }
    }

    /**
     * get messges
     *
     * @return
     */
    synchronized public List<InviteMessage> getMessagesList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<InviteMessage> msgs = new ArrayList<InviteMessage>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + InviteMessageDao.TABLE_NAME + " order by id desc", null);
            while (cursor.moveToNext()) {
                InviteMessage msg = new InviteMessage();
                int id = cursor.getInt(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_ID));
                String from = cursor.getString(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_FROM));
                String groupid = cursor.getString(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_GROUP_ID));
                String groupname = cursor.getString(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_GROUP_Name));
                String reason = cursor.getString(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_REASON));
                long time = cursor.getLong(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_TIME));
                int status = cursor.getInt(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_STATUS));
                String groupInviter = cursor.getString(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_GROUPINVITER));

                msg.setId(id);
                msg.setFrom(from);
                msg.setGroupId(groupid);
                msg.setGroupName(groupname);
                msg.setReason(reason);
                msg.setTime(time);
                msg.setGroupInviter(groupInviter);

                if (status == InviteMessageStatus.BEINVITEED.ordinal())
                    msg.setStatus(InviteMessageStatus.BEINVITEED);
                else if (status == InviteMessageStatus.BEAGREED.ordinal())
                    msg.setStatus(InviteMessageStatus.BEAGREED);
                else if (status == InviteMessageStatus.BEREFUSED.ordinal())
                    msg.setStatus(InviteMessageStatus.BEREFUSED);
                else if (status == InviteMessageStatus.AGREED.ordinal())
                    msg.setStatus(InviteMessageStatus.AGREED);
                else if (status == InviteMessageStatus.REFUSED.ordinal())
                    msg.setStatus(InviteMessageStatus.REFUSED);
                else if (status == InviteMessageStatus.BEAPPLYED.ordinal())
                    msg.setStatus(InviteMessageStatus.BEAPPLYED);
                else if (status == InviteMessageStatus.GROUPINVITATION.ordinal())
                    msg.setStatus(InviteMessageStatus.GROUPINVITATION);
                else if (status == InviteMessageStatus.GROUPINVITATION_ACCEPTED.ordinal())
                    msg.setStatus(InviteMessageStatus.GROUPINVITATION_ACCEPTED);
                else if (status == InviteMessageStatus.GROUPINVITATION_DECLINED.ordinal())
                    msg.setStatus(InviteMessageStatus.GROUPINVITATION_DECLINED);

                msgs.add(msg);
            }
            cursor.close();
        }
        return msgs;
    }

    /**
     * delete invitation message
     *
     * @param from
     */
    synchronized public void deleteMessage(String from) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(InviteMessageDao.TABLE_NAME, InviteMessageDao.COLUMN_NAME_FROM + " = ?", new String[]{from});
        }
    }

    synchronized int getUnreadNotifyCount() {
        int count = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select " + InviteMessageDao.COLUMN_NAME_UNREAD_MSG_COUNT + " from " + InviteMessageDao.TABLE_NAME, null);
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }

    synchronized void setUnreadNotifyCount(int count) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(InviteMessageDao.COLUMN_NAME_UNREAD_MSG_COUNT, count);

            db.update(InviteMessageDao.TABLE_NAME, values, null, null);
        }
    }

    synchronized public void closeDB() {
        if (dbHelper != null) {
            dbHelper.closeDB();
        }
        dbMgr = null;
    }


    /**
     * Save Robot list
     */
//    synchronized public void saveRobotList(List<RobotUser> robotList) {
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        if (db.isOpen()) {
//            db.delete(UserDao.ROBOT_TABLE_NAME, null, null);
//            for (RobotUser item : robotList) {
//                ContentValues values = new ContentValues();
//                values.put(UserDao.ROBOT_COLUMN_NAME_ID, item.getUsername());
//                if (item.getNick() != null)
//                    values.put(UserDao.ROBOT_COLUMN_NAME_NICK, item.getNick());
//                if (item.getAvatar() != null)
//                    values.put(UserDao.ROBOT_COLUMN_NAME_AVATAR, item.getAvatar());
//                db.replace(UserDao.ROBOT_TABLE_NAME, null, values);
//            }
//        }
//    }

    /**
     * load robot list
     */
//    synchronized public Map<String, RobotUser> getRobotList() {
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        Map<String, RobotUser> users = null;
//        if (db.isOpen()) {
//            Cursor cursor = db.rawQuery("select * from " + UserDao.ROBOT_TABLE_NAME, null);
//            if (cursor.getCount() > 0) {
//                users = new Hashtable<String, RobotUser>();
//            }
//            ;
//            while (cursor.moveToNext()) {
//                String username = cursor.getString(cursor.getColumnIndex(UserDao.ROBOT_COLUMN_NAME_ID));
//                String nick = cursor.getString(cursor.getColumnIndex(UserDao.ROBOT_COLUMN_NAME_NICK));
//                String avatar = cursor.getString(cursor.getColumnIndex(UserDao.ROBOT_COLUMN_NAME_AVATAR));
//                RobotUser user = new RobotUser(username);
//                user.setNick(nick);
//                user.setAvatar(avatar);
//                String headerName = null;
//                if (!TextUtils.isEmpty(user.getNick())) {
//                    headerName = user.getNick();
//                } else {
//                    headerName = user.getUsername();
//                }
//                if (Character.isDigit(headerName.charAt(0))) {
//                    user.setInitialLetter("#");
//                } else {
//                    user.setInitialLetter(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target
//                            .substring(0, 1).toUpperCase());
//                    char header = user.getInitialLetter().toLowerCase().charAt(0);
//                    if (header < 'a' || header > 'z') {
//                        user.setInitialLetter("#");
//                    }
//                }
//
//                users.put(username, user);
//            }
//            cursor.close();
//        }
//        return users;
//    }
}
