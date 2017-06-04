package com.iilu.fendou.dbs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.iilu.fendou.modules.login.entity.UserLogin;

public class LoginDB extends RootDB {

    private String TABLE_NAME = "login";

    public LoginDB(Context context) {
        super(context);
    }

    public synchronized UserLogin queryUserLoginInfo(String username) {
        UserLogin userLogin = null;
        Cursor cursor = null;
        SQLiteDatabase dbReadable = null;
        try {
            dbReadable = mDBHelper.getReadableDatabase();
            String sql = "select * from " + TABLE_NAME + " where username = ?";
            String[] selectionArgs = { username };
            cursor = dbReadable.rawQuery(sql, selectionArgs);
            if (cursor != null && cursor.moveToFirst()) {
                userLogin = new UserLogin();
                userLogin.setUserid(cursor.getString(cursor.getColumnIndex("userid")));
                userLogin.setUsername(cursor.getString(cursor.getColumnIndex("username")));
                userLogin.setPassword(cursor.getString(cursor.getColumnIndex("passworld")));
                userLogin.setTime(cursor.getInt(cursor.getColumnIndex("time")));
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
        return userLogin;
    }

    public synchronized void saveUserLoginInfo(String username, String password) {
        UserLogin userLogin = queryUserLoginInfo(username);
        if (userLogin == null) {
            inserUserLoginInfo(username, password);
        } else {
            updateUserLoginInfo(username, password);
        }
    }

    private synchronized void inserUserLoginInfo(String username, String password) {
        SQLiteDatabase dbWritable = null;
        try {
            dbWritable = mDBHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("userid", username);
            values.put("username", username);
            values.put("passworld", password);
            values.put("time", System.currentTimeMillis());
            dbWritable.insert(TABLE_NAME, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dbWritable != null && dbWritable.isOpen()) {
                dbWritable.close();
            }
        }
    }

    private synchronized void updateUserLoginInfo(String username, String password) {
        SQLiteDatabase dbWritable = null;
        try {
            dbWritable = mDBHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("userid", username);
            values.put("passworld", password);
            values.put("time", System.currentTimeMillis());
            String whereClause = "username = ?";
            String[] whereArgs = { username };
            dbWritable.update(TABLE_NAME, values, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dbWritable != null && dbWritable.isOpen()) {
                dbWritable.close();
            }
        }
    }

}
