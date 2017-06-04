package com.iilu.fendou.nets.http.result;

import android.content.Context;

import com.iilu.fendou.R;
import com.iilu.fendou.dialogs.SimpleHUD;
import com.iilu.fendou.utils.ToastUtil;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import rx.Subscriber;

public class CallBack<T> extends Subscriber<T> {

    private Context context;

    public CallBack(Context context) {
        this.context = context;
    }

    @Override
    public void onCompleted() {
        SimpleHUD.dismiss();
    }

    @Override
    public void onError(Throwable tr) {
        if (tr instanceof SocketTimeoutException) {
            ToastUtil.showCenter(context, R.string.net_not_good);
        } else if (tr instanceof ConnectException) {
            ToastUtil.showCenter(context, R.string.net_not_available);
        } else if (tr instanceof NullPointerException) {

        } else {

        }
        SimpleHUD.dismiss();
    }

    @Override
    public void onNext(T t) {
        SimpleHUD.dismiss();
    }
}
