package com.iilu.fendou.modules.sport;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.iilu.fendou.MainApplication;
import com.iilu.fendou.dbs.StepSensorInfoDB;
import com.iilu.fendou.dbs.UserInfoDB;
import com.iilu.fendou.dbs.UserStepDB;
import com.iilu.fendou.modules.entity.UserInfo;
import com.iilu.fendou.modules.sport.entity.StepSensor;
import com.iilu.fendou.modules.sport.listeners.OnStepListener;
import com.iilu.fendou.utils.DateUtil;
import com.iilu.fendou.utils.ServiceUtil;
import com.iilu.fendou.utils.SystemUtil;

import org.apache.log4j.Logger;

public class StepService  extends Service implements SensorEventListener {

    private final Logger mlog = Logger.getLogger(StepService.class.getSimpleName());

    private Context mContext = MainApplication.getAppContext();

    private UserInfoDB mUserInfoDB;
    private UserStepDB mUserStepDB;
    private StepSensorInfoDB mStepSensorInfoDB;
    private SensorManager mSensorManager;
    private OnStepListener mOnStepListener;

    private static UserInfo mUserInfo;

    private int mLastValue = 0;
    private long mSensorTime = 0;

    public void registerListener(OnStepListener listener) {
        this.mOnStepListener = listener;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mUserInfoDB = new UserInfoDB(mContext);
        mUserStepDB = new UserStepDB(mContext);
        mStepSensorInfoDB = new StepSensorInfoDB(mContext);

        mUserInfo = mUserInfoDB.queryUserInfo(MainApplication.getCurrLoginUsername());

        initRegister();

        return START_STICKY;
    }

    private void initRegister() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensorStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        Sensor sensorStepDetector = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        Sensor sensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        boolean isSupportStep = mSensorManager.registerListener(this, sensorStepCounter, SensorManager.SENSOR_DELAY_NORMAL);
        //boolean isSupportStep = mSensorManager.registerListener(this, sensorStepDetector, SensorManager.SENSOR_DELAY_NORMAL);
        if (!(isKitkat() && isSupportStep)) {
            mSensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        switch (sensorType) {
            case Sensor.TYPE_STEP_COUNTER:
                if (mLastValue == 0) {
                    // 第一次注册计步传感器，保存此步数，并进行步数分配
                    mLastValue = (int) event.values[0];
                    prepare(mLastValue);
                } else {
                    // 获取步数差
                    int newValue = (int) event.values[0];
                    int step = newValue - mLastValue;
                    // 如果UserInfo数据库对象为null，再次查询数据库获取
                    if (mUserInfo == null) {
                        mUserInfo = mUserInfoDB.queryUserInfo(MainApplication.getCurrLoginUsername());
                    }
                    // 实时存储步数
                    mUserStepDB.saveStep(MainApplication.getCurrLoginUsername(), DateUtil.getCurrDayDate(), DateUtil.getCurrHour(), step, mUserInfo);
                    // 保存计步传感器信息：步数、当前时间、开机时间
                    saveStepSensorInfo(newValue);
                    // 将新值赋给旧值
                    mLastValue = newValue;
                    // 回调通知界面刷新
                    if (mOnStepListener != null) {
                        mOnStepListener.onStepNum();
                    }
                }
                break;
            case Sensor.TYPE_STEP_DETECTOR:

                break;
            case Sensor.TYPE_ACCELEROMETER:

                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * 主要作用：注册上计步传感器后，进行步数分配
     * @param value
     */
    private void prepare(int value) {
        // 查询上次保存的计步传感器信息
        StepSensor stepSensor = mStepSensorInfoDB.queryStepSensorInfo(MainApplication.getCurrLoginUsername());
        int sensorCount = 0;
        long bootTime;
        if (stepSensor == null) {
            // 如果没查到，表明新安装应用，此时将开机时间置为0
            bootTime = 0;
        } else {
            sensorCount = stepSensor.getSensorCount();
            mSensorTime = stepSensor.getSensorTime();
            bootTime = stepSensor.getBootTime();
        }

        int distributeStep = 0;
        if (mSensorTime != 0 && bootTime < mSensorTime) { // 正常逻辑 或 覆盖安装
            if (sensorCount < value) {
                distributeStep = value - sensorCount;
            } else {
                mlog.debug("sensorCount = " + sensorCount + ", value = " + value);
                return;
            }
        } else { // 第一次安装分配数据
            distributeStep = value;
            mSensorTime = 0;
        }

        // 进行步数分配
        distributeValues(distributeStep);
        // 回调通知界面刷新
        if (mOnStepListener != null) {
            mOnStepListener.onStepNum();
        }
        // 保存计步传感器信息
        saveStepSensorInfo(value);
    }

    /**
     * 分配步数
     * @param value
     */
    private void distributeValues(int value) {
        // 判断是否是当天
        boolean today = isToday();
        if (today) {
            distributeToday(value); //当天数据处理
        } else {
            distributeOtherDay(value); //隔天数据处理
        }
    }

    /**
     * 对比上次计步传感器时间，判断是否是同一天
     * @return
     */
    private boolean isToday() {
        long lastTime;
        if (mSensorTime != 0) {
            lastTime = mSensorTime;
        } else {
            lastTime = SystemUtil.getBoottime();
        }

        String lastDate = DateUtil.formatDate("yyyyMMdd", lastTime);
        String currDate = DateUtil.formatDate("yyyyMMdd", System.currentTimeMillis());

        return lastDate.contentEquals(currDate);
    }

    /**
     * 当天数据分配，上次注册计步传感器也是在当前天，即不跨天
     * 当前天，分配到当前小时
     * @param value
     */
    private void  distributeToday(int value) {
        mUserStepDB.saveStep(MainApplication.getCurrLoginUsername(), DateUtil.getCurrDayDate(), DateUtil.getCurrHour(), value, mUserInfo);
    }

    /**
     * 其他天数据分配，上次注册计步传感器在之前天，即跨天
     * @param value
     */
    private void distributeOtherDay(int value) {
        long lastSensorTimeZeroTime; // 上次计步传感器时间的凌晨零点
        int lastHour;
        if (mSensorTime != 0) {
            lastSensorTimeZeroTime = DateUtil.convertToZeroTime(mSensorTime);
            lastHour = DateUtil.getHour(mSensorTime);
        } else {
            lastSensorTimeZeroTime = DateUtil.convertToZeroTime(SystemUtil.getBoottime());
            lastHour = DateUtil.getHour(SystemUtil.getBoottime());
        }

        // 注意：最小的跨天，即昨天 跨到 今天，dayCount最小为1
        int dayCount_1 = (int) ((System.currentTimeMillis() - lastSensorTimeZeroTime) / DateUtil.ONE_DAY_MILLIS);

        dayCount_1 += 1; // 总待分配天数

        float dayCount_2 = dayCount_1;

        dayCount_2 -= 2; // 去掉当前天、最后一天的天数；最小值为0

        // 考虑步数是否小于要分配的天数
        // 规则：1) 当前天 >12 点，则分配一天数据；否则，分配半天数据
        // 2) 最后一天，lastHour>zzEndTime，则分配半天数据；否则，分配一天数据
        // 3) 中间天，分配一天数据，朝朝/暮暮 = 3/4 分配
        if (DateUtil.getCurrHour() > 12) {
            dayCount_2 += 1;
        } else {
            dayCount_2 += 0.5f;
        }

        int zzStartTime = 0;
        int zzEndTime = 0;
        int mmStartTime = 0;
        if (mUserInfo != null) {
            zzStartTime = mUserInfo.getZzStartTime();
            zzEndTime = mUserInfo.getZzEndTime();
            mmStartTime = mUserInfo.getMmStartTime();
        }
        if (lastHour > zzEndTime) {
            dayCount_2 += 0.5f;
        } else {
            dayCount_2 += 1;
        }

        if (value > dayCount_1 * 7) {
            int disValue = (int) (value / dayCount_2);

            String currDate = DateUtil.getCurrDayDate();
            for (int i = 0; i < dayCount_1; i++) {
                String date = DateUtil.offsetDate("yyyyMMdd", currDate, -i);

                if (i == 0) {
                    // 1. 当前天，分配到当前小时
                    if (DateUtil.getCurrHour() < 12) {
                        mUserStepDB.saveStep(MainApplication.getCurrLoginUsername(), date, DateUtil.getCurrHour(), disValue / 2, mUserInfo);
                    } else {
                        mUserStepDB.saveStep(MainApplication.getCurrLoginUsername(), date, DateUtil.getCurrHour(), disValue, mUserInfo);
                    }
                } else if (i == dayCount_1 - 1) {
                    // 3. 最后一天，如果lastHour > zzEndTime(朝朝结束时间)，则分配到暮暮第一个小时；
                    // 否则，分别分配到朝朝第一个小时 和 暮暮第一个小时，3/4比例分配
                    if (lastHour > zzEndTime) {
                        mUserStepDB.saveStep(MainApplication.getCurrLoginUsername(), date, mmStartTime, disValue / 2, mUserInfo);
                    } else {
                        // 保存到朝朝第一个小时
                        mUserStepDB.saveStep(MainApplication.getCurrLoginUsername(), date, zzStartTime, disValue * 3 / 7, mUserInfo);
                        // 保存到暮暮第一个小时
                        mUserStepDB.saveStep(MainApplication.getCurrLoginUsername(), date, mmStartTime, disValue * 4 / 7, mUserInfo);
                    }
                } else {
                    // 2. 中间天，分配到朝朝/暮暮的第一个小时，按3/4分配一天步数
                    // 保存到朝朝第一个小时
                    mUserStepDB.saveStep(MainApplication.getCurrLoginUsername(), date, zzStartTime, disValue * 3 / 7, mUserInfo);
                    // 保存到暮暮第一个小时
                    mUserStepDB.saveStep(MainApplication.getCurrLoginUsername(), date, mmStartTime, disValue * 4 / 7, mUserInfo);
                }
            }
        } else {
            // 待分配步数小于要分配的天数
            // 考虑到步数比较少，直接都分配到当前天，当前小时
            mUserStepDB.saveStep(MainApplication.getCurrLoginUsername(), DateUtil.getCurrDayDate(), DateUtil.getCurrHour(), value, mUserInfo);
        }
    }

    /**
     * 保存 计步传感器 信息
     * @param value
     */
    private void saveStepSensorInfo(int value) {
        StepSensor stepSensor = new StepSensor();
        stepSensor.setSensorCount(value);
        stepSensor.setSensorTime(System.currentTimeMillis());
        stepSensor.setBootTime(SystemUtil.getBoottime());
        if (mStepSensorInfoDB != null) {
            mStepSensorInfoDB.saveStepSensorInfo(MainApplication.getCurrLoginUsername(), stepSensor);
        }
    }

    private boolean isKitkat() {
        int currSdkVersion = android.os.Build.VERSION.SDK_INT;
        return currSdkVersion >= android.os.Build.VERSION_CODES.KITKAT;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (!ServiceUtil.isServiceRunning(mContext, StepService.class.getSimpleName())) {
            Intent service = new Intent(mContext, StepService.class);
            startService(service);
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (!ServiceUtil.isServiceRunning(mContext, StepService.class.getSimpleName())) {
            Intent service = new Intent(mContext, StepService.class);
            startService(service);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new StepBinder();
    }

    public class StepBinder extends Binder {
        public StepService getService() {
            return StepService.this;
        }
    }
}
