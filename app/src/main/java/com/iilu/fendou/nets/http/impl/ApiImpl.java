package com.iilu.fendou.nets.http.impl;

import com.iilu.fendou.configs.TagConfig;
import com.iilu.fendou.modules.login.entity.LoginRequest;
import com.iilu.fendou.modules.login.entity.LoginResponse;
import com.iilu.fendou.modules.register.entity.RegisterRequest;
import com.iilu.fendou.modules.register.entity.RegisterResponse;
import com.iilu.fendou.nets.http.api.Api;
import com.iilu.fendou.nets.http.api.HttpService;
import com.iilu.fendou.nets.http.engine.HttpEngine;
import com.iilu.fendou.nets.http.result.HttpResultFunc;
import com.iilu.fendou.utils.GsonUtil;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ApiImpl implements Api {
    
    private static final String TAG = TagConfig.HTTP + "ApiImpl";
    private HttpService mHttpService = null;

    public ApiImpl() {
        mHttpService = HttpEngine.getInstance().getHttpService();
    }

    @Override
    public void login(Subscriber<LoginResponse> subscriber, LoginRequest loginRequest) {
        String json  = GsonUtil.toJson(loginRequest);
        mHttpService.login(json)
                .flatMap(new HttpResultFunc<LoginResponse>())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    @Override
    public void Register(Subscriber<RegisterResponse> subscriber, RegisterRequest registerRequest) {
        String json  = GsonUtil.toJson(registerRequest);
        mHttpService.Register(json)
                .flatMap(new HttpResultFunc<RegisterResponse>())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

}
