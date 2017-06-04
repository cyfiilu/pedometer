package com.iilu.fendou.dbs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.iilu.fendou.modules.sport.entity.StepSensor;

public class StepSensorInfoDB extends RootDB {

    private final String TABLE_NAME = "step_sensor_info";

    public StepSensorInfoDB(Context context) {
        super(context);
    }

    public synchronized StepSensor queryStepSensorInfo(String userid) {
        StepSensor stepSensor = null;
        SQLiteDatabase dbReadable = null;
        Cursor cusor = null;
        try {
            dbReadable = mDBHelper.getReadableDatabase();

            String sql = "select * from " + TABLE_NAME + " where userid = ?";

            String[] selectionArgs = { userid };
            cusor = dbReadable.rawQuery(sql, selectionArgs);
            if (cusor != null && cusor.moveToFirst()) {
                stepSensor = new StepSensor();
                stepSensor.setUserid(userid);
                stepSensor.setSensorCount(cusor.getInt(cusor.getColumnIndex("sensorcount")));
                stepSensor.setSensorTime(cusor.getLong(cusor.getColumnIndex("sensortime")));
                stepSensor.setBootTime(cusor.getLong(cusor.getColumnIndex("boottime")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cusor != null && !cusor.isClosed()) {
                cusor.close();
            }
            if (dbReadable != null && dbReadable.isOpen()) {
                dbReadable.close();
            }
        }
        return stepSensor;
    }

    public synchronized void saveStepSensorInfo(String userid, StepSensor stepSensor) {
        StepSensor oldStepSensor = queryStepSensorInfo(userid);
        if (oldStepSensor == null) {
            insertStepSensorInfo(userid, stepSensor);
        } else {
            updateStepSensorInfo(userid, stepSensor);
        }
    }

    public synchronized void insertStepSensorInfo(String userid, StepSensor stepSensor) {
        SQLiteDatabase dbWritable = null;
        try {
            dbWritable = mDBHelper.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put("userid", userid);
            values.put("sensorcount", stepSensor.getSensorCount());
            values.put("sensortime", stepSensor.getSensorTime());
            values.put("boottime", stepSensor.getBootTime());

            dbWritable.insert(TABLE_NAME, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dbWritable != null && dbWritable.isOpen()) {
                dbWritable.close();
            }
        }
    }

    public synchronized void updateStepSensorInfo(String userid, StepSensor stepSensor) {
        SQLiteDatabase dbWritable = null;
        try {
            dbWritable = mDBHelper.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put("sensorcount", stepSensor.getSensorCount());
            values.put("sensortime", stepSensor.getSensorTime());
            values.put("boottime", stepSensor.getBootTime());

            String whereClause = "userid = ?";
            String[] whereArgs = new String[]{ userid };
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
