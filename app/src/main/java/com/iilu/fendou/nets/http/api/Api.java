package com.iilu.fendou.nets.http.api;

import com.iilu.fendou.modules.login.entity.LoginRequest;
import com.iilu.fendou.modules.login.entity.LoginResponse;
import com.iilu.fendou.modules.register.entity.RegisterRequest;
import com.iilu.fendou.modules.register.entity.RegisterResponse;

import rx.Subscriber;

public interface Api {

    /**
     * 登录
     * @param subscriber
     * @param loginRequest
     */
    void login(Subscriber<LoginResponse> subscriber, LoginRequest loginRequest);

    /**
     * 注册接口
     * @param subscriber
     * @param registerRequest
     */
    void Register(Subscriber<RegisterResponse> subscriber, RegisterRequest registerRequest);

}
