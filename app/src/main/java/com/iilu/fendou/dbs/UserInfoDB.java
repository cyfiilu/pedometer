package com.iilu.fendou.dbs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.iilu.fendou.modules.entity.UserInfo;

public class UserInfoDB extends RootDB {

    private final String TABLE_NAME = "user_info";

    public UserInfoDB(Context context) {
        super(context);
    }

    public synchronized UserInfo queryUserInfo(String userid) {
        UserInfo userInfo = null;
        SQLiteDatabase dbReadable = null;
        try {
            dbReadable = mDBHelper.getReadableDatabase();

            String sql = "select * from " + TABLE_NAME + " where userid = ?";
            String[] selectionArgs = { userid };
            Cursor cursor = dbReadable.rawQuery(sql, selectionArgs);
            if (cursor != null && cursor.moveToFirst()) {
                userInfo = new UserInfo();
                userInfo.setSex(cursor.getString(cursor.getColumnIndex("sex")));
                userInfo.setCity(cursor.getString(cursor.getColumnIndex("city")));
                userInfo.setHeight(cursor.getInt(cursor.getColumnIndex("height")));
                userInfo.setWeight(cursor.getInt(cursor.getColumnIndex("weight")));
                userInfo.setStepWidth(cursor.getInt(cursor.getColumnIndex("stepwidth")));
                userInfo.setDayGoalNum(cursor.getInt(cursor.getColumnIndex("daygoalnum")));
                userInfo.setZzGoalNum(cursor.getInt(cursor.getColumnIndex("zzgoalnum")));
                userInfo.setMmGoalNum(cursor.getInt(cursor.getColumnIndex("mmgoalnum")));

                userInfo.setZzStartTime(cursor.getInt(cursor.getColumnIndex("zzstarttime")));
                userInfo.setZzEndTime(cursor.getInt(cursor.getColumnIndex("zzendtime")));
                userInfo.setMmStartTime(cursor.getInt(cursor.getColumnIndex("mmstarttime")));
                userInfo.setMmEndTime(cursor.getInt(cursor.getColumnIndex("mmendtime")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dbReadable != null) {
                dbReadable.close();
            }
        }
        return userInfo;
    }

    public synchronized void saveUserInfo(String userid, UserInfo userInfo) {
        UserInfo oldUserInfo = queryUserInfo(userid);
        if (oldUserInfo == null) {
            insertUserInfo(userid, userInfo);
        } else {
            updateUserInfo(userid, userInfo);
        }
    }

    public synchronized void insertUserInfo(String userid, UserInfo userInfo) {
        SQLiteDatabase dbWritable = null;
        try {
            dbWritable = mDBHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("userid", userid);
            values.put("sex", userInfo.getSex());
            values.put("city", userInfo.getCity());
            if (userInfo.getHeight() > 0) {
                values.put("height", userInfo.getHeight());
            } else {
                values.put("height", 170);
            }
            if (userInfo.getWeight() > 0) {
                values.put("weight", userInfo.getWeight());
            } else {
                values.put("weight", 60);
            }
            if (userInfo.getStepWidth() > 0) {
                values.put("stepwidth", userInfo.getStepWidth());
            } else {
                values.put("stepwidth", 70);
            }
            if (userInfo.getDayGoalNum() > 0) {
                values.put("daygoalnum", userInfo.getDayGoalNum());
            } else {
                values.put("daygoalnum", 10000);
            }
            if (userInfo.getZzGoalNum() > 0) {
                values.put("zzgoalnum", userInfo.getZzGoalNum());
            } else {
                values.put("zzgoalnum", 3000);
            }
            if (userInfo.getMmGoalNum() > 0) {
                values.put("mmgoalnum", userInfo.getMmGoalNum());
            } else {
                values.put("mmgoalnum", 4000);
            }
            if (userInfo.getZzStartTime() > 0) {
                values.put("zzstarttime", userInfo.getZzStartTime());
            } else {
                values.put("zzstarttime", 6);
            }
            if (userInfo.getZzEndTime() > 0) {
                values.put("zzendtime", userInfo.getZzEndTime());
            } else {
                values.put("zzendtime", 9);
            }
            if (userInfo.getMmStartTime() > 0) {
                values.put("mmstarttime", userInfo.getMmStartTime());
            } else {
                values.put("mmstarttime", 18);
            }
            if (userInfo.getMmEndTime() > 0) {
                values.put("mmendtime", userInfo.getMmEndTime());
            } else {
                values.put("mmendtime", 21);
            }

            dbWritable.insert(TABLE_NAME, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dbWritable != null) {
                dbWritable.close();
            }
        }
    }

    public synchronized void updateUserInfo(String userid, UserInfo userInfo) {
        SQLiteDatabase dbWritable = null;
        try {
            dbWritable = mDBHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            if (!TextUtils.isEmpty(userInfo.getSex())) {
                values.put("sex", userInfo.getSex());
            }
            if (!TextUtils.isEmpty(userInfo.getCity())) {
                values.put("city", userInfo.getCity());
            }
            if (userInfo.getHeight() > 0) {
                values.put("height", userInfo.getHeight());
            }
            if (userInfo.getWeight() > 0) {
                values.put("weight", userInfo.getWeight());
            }
            if (userInfo.getStepWidth() > 0) {
                values.put("stepwidth", userInfo.getStepWidth());
            }
            if (userInfo.getDayGoalNum() > 0) {
                values.put("daygoalnum", userInfo.getDayGoalNum());
            }
            if (userInfo.getZzGoalNum() > 0) {
                values.put("zzgoalnum", userInfo.getZzGoalNum());
            }
            if (userInfo.getMmGoalNum() > 0) {
                values.put("mmgoalnum", userInfo.getMmGoalNum());
            }
            if (userInfo.getZzStartTime() > 0) {
                values.put("zzstarttime", userInfo.getZzStartTime());
            }
            if (userInfo.getZzEndTime() > 0) {
                values.put("zzendtime", userInfo.getZzEndTime());
            }
            if (userInfo.getMmStartTime() > 0) {
                values.put("mmstarttime", userInfo.getMmStartTime());
            }
            if (userInfo.getMmEndTime() > 0) {
                values.put("mmendtime", userInfo.getMmEndTime());
            }

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
