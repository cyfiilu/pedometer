package com.iilu.fendou.nets.http.result;

import android.util.Log;

import com.iilu.fendou.configs.TagConfig;

/**
 * 用于定义一些网络回调的error信息
 */
public class ApiException extends RuntimeException {

    private static final String TAG = TagConfig.HTTP + ApiException.class.getSimpleName();

    public ApiException(String resultCode, String resultMsg) {
        super(getApiExceptionMessage(resultCode, resultMsg));
    }

    private static String getApiExceptionMessage(String resultCode, String resultMsg) {
        Log.v(TAG, "resultCode:" + resultCode);
        return resultMsg+"#"+resultCode;
    }
}
