package com.roc.chatclient.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.roc.chatclient.entity.ChatMsg;
import com.roc.chatclient.entity.Msg;
import com.roc.chatclient.model.ReceiveMsgInfo;

import java.util.ArrayList;
import java.util.List;

public class MessageDao {
    static String TABLE_NAME = "message";

    static String CHAT_TABLE_TABLE = "chat_list";

    private String Tag = "MessageDao";
    //private DbOpenHelper dbHelper;

    public MessageDao() {
    }

    private DbOpenHelper getDbHelper() {
        return DbManager.getInstance().getDb();
    }

    public List<ChatMsg> getChatList() {
        List<ChatMsg> list = new ArrayList<>();
        SQLiteDatabase db = getDbHelper().getReadableDatabase();
        if (db.isOpen()) {
            String sql = "select * from " + CHAT_TABLE_TABLE + " order by id desc";
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                ChatMsg chatMsg = new ChatMsg();
                chatMsg.setId(cursor.getInt(cursor.getColumnIndex("id")));
                chatMsg.setAllCount(cursor.getInt(cursor.getColumnIndex("all_count")));
                chatMsg.setAvatar(cursor.getString(cursor.getColumnIndex("avatar")));
                chatMsg.setLastMsg(cursor.getString(cursor.getColumnIndex("last_msg")));
                chatMsg.setName(cursor.getString(cursor.getColumnIndex("name")));
                chatMsg.setToId(cursor.getString(cursor.getColumnIndex("to_id")));
                chatMsg.setUnReadCount(cursor.getInt(cursor.getColumnIndex("un_read_count")));
                chatMsg.setLastMsgTime(cursor.getString(cursor.getColumnIndex("last_msg_time")));
                list.add(chatMsg);
            }
            cursor.close();
        }
        return list;
    }

    public List<Msg> getChatMsgList(int pageIndex, int pageSize, int chatId) {
        List<Msg> list = new ArrayList<>();
        SQLiteDatabase db = getDbHelper().getReadableDatabase();
        if (db.isOpen()) {
            int offset = (pageIndex - 1) * pageSize;
            String sql = "select * from " + TABLE_NAME + " where chat_id=" + chatId + " order by id desc limit " + pageSize + " offset " + offset;
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                Msg msg = new Msg();
                msg.setId(cursor.getInt(cursor.getColumnIndex("id")));
                msg.setChatId(cursor.getInt(cursor.getColumnIndex("chat_id")));
                msg.setSender(cursor.getInt(cursor.getColumnIndex("sender")));
                msg.setContent(cursor.getString(cursor.getColumnIndex("content")));
                msg.setType(cursor.getInt(cursor.getColumnIndex("type")));
                msg.setSendTime(cursor.getString(cursor.getColumnIndex("send_time")));
                list.add(msg);
            }
            cursor.close();
        }
        return list;
    }

    public void saveMsg(Msg msg) {
        saveMsg(msg, true);
    }

    public void saveMsg(Msg msg, boolean update) {
        SQLiteDatabase db = getDbHelper().getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("chat_id", msg.getChatId());
        values.put("sender", msg.getSender());
        values.put("content", msg.getContent());
        values.put("type", msg.getType());
        values.put("send_time", msg.getSendTime());
        if (db.isOpen()) {
            db.insert(TABLE_NAME, null, values);

            if (update) {
                String sql = "update " + CHAT_TABLE_TABLE + " set all_count=all_count+1,un_read_count=un_read_count+1,last_msg='" + msg.getContent() + "',last_msg_time='" + msg.getSendTime() + "' where id=" + msg.getChatId();
                db.execSQL(sql);
//                Log.d(Tag, "the update chat_list");
            }
        }
    }

    public void saveMsg(ReceiveMsgInfo info) {
        DbOpenHelper dbOpenHelper = getDbHelper();
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        int id = 0;
        if (db.isOpen()) {
            String sql_query = "select * from " + CHAT_TABLE_TABLE + " where to_id=?";
            Cursor cursor = db.rawQuery(sql_query, new String[]{info.From.Id + ""});
            if (cursor.moveToFirst()) {
                id = cursor.getInt(cursor.getColumnIndex("id"));
            }
            cursor.close();
        }
//        Log.d(Tag, "the chat_list id is:" + id);
        Msg msg = new Msg();
        msg.setSendTime(info.ReceiveTime);
        msg.setType(info.Type);
        msg.setContent(info.Msg);
        msg.setSender(info.From.Id);

        boolean update = true;
        if (id == 0) {
            SQLiteDatabase db_insert = dbOpenHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("all_count", 1);
            values.put("un_read_count", 1);
            values.put("avatar", "");
            values.put("last_msg", info.Msg);
            values.put("last_msg_time", info.ReceiveTime);
            values.put("name", info.From.Name);
            values.put("to_id", info.From.Id);
            if (db_insert.isOpen()) {
                db_insert.insert(CHAT_TABLE_TABLE, null, values);

                Cursor cursor = db.rawQuery("select last_insert_rowid() from " + CHAT_TABLE_TABLE, null);
                if (cursor.moveToFirst()) {
                    id = cursor.getInt(0);
                }
                update = false;
            }
        }

        msg.setChatId(id);
//        Log.d(Tag, "the update value is:" + update);
        saveMsg(msg, update);
    }

    public void deleteChat(int chatId) {
        SQLiteDatabase db = getDbHelper().getWritableDatabase();
        db.execSQL("delete from " + CHAT_TABLE_TABLE + " where id=?", new Object[]{chatId});
        db.execSQL("delete from " + TABLE_NAME + " where chat_id=?", new Object[]{chatId});
    }
}
