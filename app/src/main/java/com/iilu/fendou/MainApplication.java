package com.iilu.fendou;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.iilu.fendou.configs.LogConfig;
import com.iilu.fendou.configs.PrefsConfig;
import com.iilu.fendou.dbs.DBHelper;
import com.iilu.fendou.modules.sport.StepService;
import com.iilu.fendou.utils.SPrefUtil_2;
import com.iilu.fendou.utils.ServiceUtil;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainApplication extends Application {

    private static Context mContext;
    private static String mAppIntroduceContent;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        new DBHelper(this).getWritableDatabase();
        //LiteOrmDB.createDb(mContext, DBConfig.DB_NAME);

        initImageLoader();

        // 初始化环信
        initEM();

        initAppIntroduce();
    }

    private void initImageLoader() {
        File cacheDir = StorageUtils.getOwnCacheDirectory(mContext, "fendou/iamgeloader");

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                // 设置图片解码类型
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .considerExifParams(false)// 不考虑iamge的翻转
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext)
                .denyCacheImageMultipleSizesInMemory()
                // 一个URL只缓存一张图片
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                // 文件名MD5加密
                .defaultDisplayImageOptions(defaultOptions)
                .discCache(new UnlimitedDiskCache(cacheDir))
                // 自定义缓存路径
                .discCacheSize(50 * 1024 * 1024)
                // 磁盘缓存大小
                .discCacheFileCount(100)
                // 缓存100图片
                .memoryCache(new WeakMemoryCache())
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024).memoryCacheSizePercentage(13) // default
                .threadPoolSize(2).writeDebugLogs().build();
        ImageLoader.getInstance().init(config);
    }

    private void initEM() {
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        // 初始化
        EMClient.getInstance().init(mContext, options);
        // 在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        //EMClient.getInstance().setDebugMode(true);
    }

    private void initAppIntroduce() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream is = mContext.getAssets().open("readme.txt");
                    InputStreamReader inputStreamReader = new InputStreamReader(is);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    StringBuilder sb = new StringBuilder();
                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                        sb.append("\n");
                    }
                    mAppIntroduceContent = sb.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static String getAppIntroduceContent() {
        return mAppIntroduceContent;
    }

    public static Context getAppContext() {
        return mContext;
    }

    public static String getCurrLoginUsername() {
        return SPrefUtil_2.get(mContext, PrefsConfig.USER_LOGIN, "curr_login_username", "");
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

}
