package com.iilu.fendou.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.iilu.fendou.modules.login.LoginActivity;

import org.apache.log4j.Logger;

public class AppUtil {

    private static final Logger mlog = Logger.getLogger(AppUtil.class.getSimpleName());

    /**
     * 环信推出登陆
     * @param context
     */
    public static void easeLogout(final Context context) {
        EMClient.getInstance().logout(true, new EMCallBack() {
            @Override
            public void onSuccess() {
                mlog.info("环信退出登录成功 ");
                if (context != null) {
                    context.startActivity(new Intent(context, LoginActivity.class));
                    ((Activity) context).finish();
                }
            }

            @Override
            public void onError(int code, String error) {
                mlog.info("环信退出登录失败 code = " + code + ", error = " + error);
            }

            @Override
            public void onProgress(int progress, String status) {
                mlog.info("环信退出登录进度 progress = " + progress + ", status = " + status);
            }
        });
    }
}
