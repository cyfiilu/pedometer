package com.iilu.fendou.dbs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.iilu.fendou.modules.message.entity.EasemobAddFriend;

import java.util.ArrayList;
import java.util.List;

public class MsgAddFriendDB extends RootDB {

    private final String TABLE_NAME = "msg_add_friend";

    public MsgAddFriendDB(Context context) {
        super(context);
    }

    public synchronized int queryUnreadCount(String userid) {
        int count = 0;
        SQLiteDatabase dbReadable = null;
        Cursor cursor = null;
        try {
            dbReadable = mDBHelper.getReadableDatabase();

            String sql = "select count(*) from " + TABLE_NAME + " where userid = ? and readtag = 1";
            String[] selectionArgs = {userid};
            cursor = dbReadable.rawQuery(sql, selectionArgs);
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(cursor.getColumnIndex("count(*)"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (dbReadable != null && dbReadable.isOpen()) {
                dbReadable.close();
            }
        }
        return count;
    }

    public synchronized List<EasemobAddFriend> queryFriends(String userid) {
        EasemobAddFriend easemobAddFriend;
        SQLiteDatabase dbReadable = null;
        Cursor cursor = null;
        List<EasemobAddFriend> list = new ArrayList<>();

        try {
            dbReadable = mDBHelper.getReadableDatabase();

            String sql = "select * from " + TABLE_NAME + " where userid = ? order by receivetime desc";
            String[] selectionArgs = { userid };
            cursor = dbReadable.rawQuery(sql, selectionArgs);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    easemobAddFriend = new EasemobAddFriend();
                    easemobAddFriend.setUserid(cursor.getString(cursor.getColumnIndex("userid")));
                    easemobAddFriend.setFriendName(cursor.getString(cursor.getColumnIndex("friendname")));
                    easemobAddFriend.setReason(cursor.getString(cursor.getColumnIndex("reason")));
                    easemobAddFriend.setAccepte(cursor.getString(cursor.getColumnIndex("accepte")));
                    easemobAddFriend.setReadTag(cursor.getString(cursor.getColumnIndex("readtag")));
                    easemobAddFriend.setReceiveTime(cursor.getString(cursor.getColumnIndex("receivetime")));
                    easemobAddFriend.setHeadUrl(cursor.getString(cursor.getColumnIndex("headurl")));
                    list.add(easemobAddFriend);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (dbReadable != null && dbReadable.isOpen()) {
                dbReadable.close();
            }
        }

        return list;
    }

    public synchronized EasemobAddFriend queryFriends(String userid, String friendName) {
        EasemobAddFriend easemobAddFriend = null;
        SQLiteDatabase dbReadable = null;
        Cursor cursor = null;

        try {
            dbReadable = mDBHelper.getReadableDatabase();

            String sql = "select * from " + TABLE_NAME + " where userid = ? and friendName = ?";
            String[] selectionArgs = { userid, friendName };
            cursor = dbReadable.rawQuery(sql, selectionArgs);

            if (cursor != null && cursor.moveToFirst()) {
                easemobAddFriend = new EasemobAddFriend();
                easemobAddFriend.setUserid(cursor.getString(cursor.getColumnIndex("userid")));
                easemobAddFriend.setFriendName(cursor.getString(cursor.getColumnIndex("friendname")));
                easemobAddFriend.setReason(cursor.getString(cursor.getColumnIndex("reason")));
                easemobAddFriend.setAccepte(cursor.getString(cursor.getColumnIndex("accepte")));
                easemobAddFriend.setReadTag(cursor.getString(cursor.getColumnIndex("readtag")));
                easemobAddFriend.setReceiveTime(cursor.getString(cursor.getColumnIndex("receivetime")));
                easemobAddFriend.setHeadUrl(cursor.getString(cursor.getColumnIndex("headurl")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (dbReadable != null && dbReadable.isOpen()) {
                dbReadable.close();
            }
        }

        return easemobAddFriend;
    }

    public synchronized void saveFriend(String userid, EasemobAddFriend easemobAddFriend) {
        EasemobAddFriend addFriend = queryFriends(userid, easemobAddFriend.getFriendName());
        if (addFriend != null) {
            updateFriend(userid, easemobAddFriend);
        } else {
            insertFriend(userid, easemobAddFriend);
        }
    }

    public synchronized void insertFriend(String userid, EasemobAddFriend easemobAddFriend) {
        SQLiteDatabase dbWritable = null;
        try {
            dbWritable = mDBHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("userid", userid);
            values.put("friendname", easemobAddFriend.getFriendName());
            values.put("reason", easemobAddFriend.getReason());
            values.put("accepte", easemobAddFriend.getAccepte());
            values.put("readtag", easemobAddFriend.getReadTag());
            values.put("receivetime", easemobAddFriend.getReceiveTime());
            values.put("headurl", easemobAddFriend.getHeadUrl());

            dbWritable.insert(TABLE_NAME, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dbWritable != null) {
                dbWritable.close();
            }
        }
    }

    public synchronized void updateFriend(String userid, EasemobAddFriend easemobAddFriend) {
        SQLiteDatabase dbWritable = null;
        try {
            dbWritable = mDBHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("userid", userid);
            values.put("reason", easemobAddFriend.getReason());
            values.put("accepte", easemobAddFriend.getAccepte());
            values.put("readtag", easemobAddFriend.getReadTag());
            values.put("receivetime", easemobAddFriend.getReceiveTime());
            values.put("headurl", easemobAddFriend.getHeadUrl());

            String whereClause = "userid = ? and friendname = ?";
            String[] whereArgs = new String[]{ userid, easemobAddFriend.getFriendName() };

            dbWritable.update(TABLE_NAME, values, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dbWritable != null) {
                dbWritable.close();
            }
        }
    }

    public synchronized void markMessageAsRead(String userid) {
        SQLiteDatabase dbWritable = null;
        try {
            dbWritable = mDBHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("readtag", 0);
            String whereClause = "userid = ?";
            String[] whereArgs = new String[]{ userid };
            dbWritable.update(TABLE_NAME, values, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dbWritable != null) {
                dbWritable.close();
            }
        }
    }

}
