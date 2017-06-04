package com.iilu.fendou.dbs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.iilu.fendou.modules.entity.UserInfo;
import com.iilu.fendou.modules.sport.entity.DayData;
import com.iilu.fendou.utils.NumberUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class UserStepDB extends RootDB {

    private final String TABLE_NAME = "user_step_data";

    public UserStepDB(Context context) {
        super(context);
    }

    public synchronized List<DayData> queryDayData_30(String userid, String dayDate) {
        List<DayData> list = new ArrayList<>();
        SQLiteDatabase dbReadable = null;
        Cursor cursor = null;
        try {
            dbReadable = mDBHelper.getReadableDatabase();

            String sql = "select * from " + TABLE_NAME + " where userid = ? and daydate <= ? order by daydate desc limit 30";

            String[] selectionArgs = { userid, dayDate };
            cursor = dbReadable.rawQuery(sql, selectionArgs);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    DayData dayData = new DayData();
                    dayData.setUserid(userid);
                    dayData.setDayDate(cursor.getString(cursor.getColumnIndex("daydate")));
                    dayData.setDayStepCount(cursor.getInt(cursor.getColumnIndex("daystepcount")));
                    dayData.setDayGoalNum(cursor.getInt(cursor.getColumnIndex("daygoalnum")));
                    dayData.setKm(cursor.getFloat(cursor.getColumnIndex("km")));
                    dayData.setKcal(cursor.getFloat(cursor.getColumnIndex("kcal")));
                    dayData.setZzStartTime(cursor.getInt(cursor.getColumnIndex("zzstarttime")));
                    dayData.setZzEndTime(cursor.getInt(cursor.getColumnIndex("zzendtime")));
                    dayData.setZzStepCount(cursor.getInt(cursor.getColumnIndex("zzstepcount")));
                    dayData.setZzGoalNum(cursor.getInt(cursor.getColumnIndex("zzgoalnum")));
                    dayData.setMmStartTime(cursor.getInt(cursor.getColumnIndex("mmstarttime")));
                    dayData.setMmEndTime(cursor.getInt(cursor.getColumnIndex("mmendtime")));
                    dayData.setMmGoalNum(cursor.getInt(cursor.getColumnIndex("mmgoalnum")));
                    dayData.setMmStepCount(cursor.getInt(cursor.getColumnIndex("mmstepcount")));
                    int[] hours = new int[24];
                    for (int i = 0; i < 24; i++) {
                        hours[i] = cursor.getInt(cursor.getColumnIndex("hour" + i));
                    }
                    dayData.setHours(hours);

                    list.add(dayData);
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

    public synchronized DayData queryDayData(String userid, String dayDate) {
        SQLiteDatabase dbReadable = null;
        Cursor cursor = null;
        DayData dayData = null;
        try {
            dbReadable = mDBHelper.getReadableDatabase();

            String sql = "select * from " + TABLE_NAME + " where userid = ? and dayDate = ?";

            String[] selectionArgs = {userid, dayDate};
            cursor = dbReadable.rawQuery(sql, selectionArgs);
            if (cursor != null && cursor.moveToFirst()) {
                dayData = new DayData();
                dayData.setUserid(userid);
                dayData.setDayDate(dayDate);
                dayData.setDayStepCount(cursor.getInt(cursor.getColumnIndex("daystepcount")));
                dayData.setDayGoalNum(cursor.getInt(cursor.getColumnIndex("daygoalnum")));
                dayData.setKm(cursor.getFloat(cursor.getColumnIndex("km")));
                dayData.setKcal(cursor.getFloat(cursor.getColumnIndex("kcal")));
                dayData.setZzStartTime(cursor.getInt(cursor.getColumnIndex("zzstarttime")));
                dayData.setZzEndTime(cursor.getInt(cursor.getColumnIndex("zzendtime")));
                dayData.setZzStepCount(cursor.getInt(cursor.getColumnIndex("zzstepcount")));
                dayData.setZzGoalNum(cursor.getInt(cursor.getColumnIndex("zzgoalnum")));
                dayData.setMmStartTime(cursor.getInt(cursor.getColumnIndex("mmstarttime")));
                dayData.setMmEndTime(cursor.getInt(cursor.getColumnIndex("mmendtime")));
                dayData.setMmGoalNum(cursor.getInt(cursor.getColumnIndex("mmgoalnum")));
                dayData.setMmStepCount(cursor.getInt(cursor.getColumnIndex("mmstepcount")));
                int[] hours = new int[24];
                for (int i = 0; i < 24; i++) {
                    hours[i] = cursor.getInt(cursor.getColumnIndex("hour" + i));
                }
                dayData.setHours(hours);
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
        return dayData;
    }

    public synchronized void saveStep(String userid, String dayDate, int currHour, int currHourStep, UserInfo userInfo) {
        DayData dayData = queryDayData(userid, dayDate);
        if (dayData == null) { // 表明表中还没有此项记录，则需插入
            insertDayData(userid, dayDate, currHour, currHourStep, userInfo);
        } else { // 表明表中已有此项记录，则需更新
            updateDayData(dayData, userid, dayDate, currHour, currHourStep, userInfo);
        }
    }

    public synchronized void insertDayData(String userid, String dayDate, int currHour, int currHourStep, UserInfo userInfo) {
        SQLiteDatabase dbWritable = null;
        try {
            dbWritable = mDBHelper.getWritableDatabase();

            int stepWidth = userInfo.getStepWidth();
            int weight = userInfo.getWeight();
            float kmValue = NumberUtil.round(currHourStep * stepWidth / 100000f, 1, BigDecimal.ROUND_DOWN);
            float kcalValue = NumberUtil.round(1.05f * weight * (4 * currHourStep * 0.5f / 3600), 1, BigDecimal.ROUND_DOWN);

            ContentValues values = new ContentValues();
            values.put("userid", userid);
            values.put("daydate", dayDate);
            values.put("daystepcount", currHourStep);
            values.put("daygoalnum", userInfo.getDayGoalNum());
            values.put("km", kmValue);
            values.put("kcal", kcalValue);
            values.put("zzstarttime", userInfo.getZzStartTime());
            values.put("zzendtime", userInfo.getZzEndTime());
            values.put("zzstepcount", 0);
            values.put("zzgoalnum", userInfo.getZzGoalNum());
            values.put("mmstarttime", userInfo.getMmStartTime());
            values.put("mmendtime", userInfo.getMmEndTime());
            values.put("mmgoalnum", userInfo.getMmGoalNum());
            values.put("mmstepcount", 0);
            for (int i = 0; i < 24; i++) {
                values.put("hour" + i, 0);
            }
            values.put("hour" + currHour, currHourStep);

            int zzStartTime = userInfo.getZzStartTime();
            int zzEndTime = userInfo.getZzEndTime();
            int mmStartTime = userInfo.getMmStartTime();
            int mmEndTime = userInfo.getMmEndTime();
            if (currHour >= zzStartTime && currHour < zzEndTime + 1) {
                values.put("zzstepcount", currHourStep);
            }
            if (currHour >= mmStartTime && currHour < mmEndTime + 1) {
                values.put("mmstepcount", currHourStep);
            }

            dbWritable.insert(TABLE_NAME, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dbWritable != null && dbWritable.isOpen()) {
                dbWritable.close();
            }
        }
    }

    public synchronized void updateDayData(DayData dayData, String userid, String dayDate, int currHour, int currHourStep, UserInfo userInfo) {
        SQLiteDatabase dbWritable = null;
        try {
            dbWritable = mDBHelper.getWritableDatabase();

            int oldHourStep = dayData.getHours()[currHour];
            int oldDayStepCount = dayData.getDayStepCount();
            int zzStartTime = dayData.getZzStartTime();
            int zzEndTime = dayData.getZzEndTime();
            int mmStartTime = dayData.getMmStartTime();
            int mmEndTime = dayData.getMmEndTime();

            int newHourStep = currHourStep + oldHourStep;
            int newDayStepCount = currHourStep + oldDayStepCount;

            int stepWidth = userInfo.getStepWidth();
            int weight = userInfo.getWeight();

            float kmValue = NumberUtil.round(newDayStepCount * stepWidth / 100000f, 1, BigDecimal.ROUND_DOWN);
            float kcalValue = NumberUtil.round(1.05f * weight * (4 * newDayStepCount * 0.5f / 3600), 1, BigDecimal.ROUND_DOWN);

            ContentValues values = new ContentValues();

            values.put("hour" + currHour, newHourStep);
            values.put("daystepcount", newDayStepCount);
            values.put("km", kmValue);
            values.put("kcal", kcalValue);

            if (currHour >= zzStartTime && currHour < zzEndTime + 1) {
                int oldZzStepCount = dayData.getZzStepCount();
                int newZzStepCount = currHourStep + oldZzStepCount;
                values.put("zzstepcount", newZzStepCount);
            }

            if (currHour >= mmStartTime && currHour < mmEndTime + 1) {
                int oldMmStepCount = dayData.getMmStepCount();
                int newMmStepCount = currHourStep + oldMmStepCount;
                values.put("mmstepcount", newMmStepCount);
            }

            String whereClause = "userid = ? and dayDate = ?";
            String[] whereArgs = new String[]{userid, dayDate};

            dbWritable.update(TABLE_NAME, values, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dbWritable != null && dbWritable.isOpen()) {
                dbWritable.close();
            }
        }
    }

    public synchronized  Map<String, Integer> querySportInfo(String userid) {
        ConcurrentMap<String, Integer> map = new ConcurrentHashMap();
        SQLiteDatabase dbWritable = null;
        Cursor cursor = null;
        try {
            dbWritable = mDBHelper.getWritableDatabase();

            // 根据userid查询完成目标步数的天数
            String sql_1 = "select count(*) from " + TABLE_NAME + " where userid = ? and daystepcount >= daygoalnum";
            String[] selectionArgs_1 = { userid };
            cursor = dbWritable.rawQuery(sql_1, selectionArgs_1);
            if (cursor != null && cursor.moveToFirst()) {
                int totalFinishDayCount = cursor.getInt(cursor.getColumnIndex("count(*)"));
                map.put("totalFinishDayCount", totalFinishDayCount);
            }

            // 根据userid查询总步数
            String sql_2 = "select sum(daystepcount) from " + TABLE_NAME + " where userid = ?";
            String[] selectionArgs_2 = { userid };
            cursor = dbWritable.rawQuery(sql_2, selectionArgs_2);
            if (cursor != null && cursor.moveToFirst()) {
                int totalDayStepCount = cursor.getInt(cursor.getColumnIndex("sum(daystepcount)"));
                map.put("totalDayStepCount", totalDayStepCount);
            }

        } catch (Exception e) {
            e.printStackTrace();
            map.put("totalFinishDayCount", 0);
            map.put("totalDayStepCount", 0);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (dbWritable != null && dbWritable.isOpen()) {
                dbWritable.close();
            }
        }
        return map;
    }
}
