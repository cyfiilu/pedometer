package com.iilu.fendou.nets.http.api;

import com.iilu.fendou.modules.login.entity.LoginResponse;
import com.iilu.fendou.modules.register.entity.RegisterResponse;
import com.iilu.fendou.nets.http.UrlContanier;
import com.iilu.fendou.nets.http.result.HttpResult;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

public interface HttpService {

    /**
     * 登录
     * @param reqMessageBody
     * @return
     */
    @FormUrlEncoded
    @POST(UrlContanier.GetUserLogin)
    Observable<HttpResult<LoginResponse>> login(@Field("ReqMessageBody") String reqMessageBody);

    /**
     * 注册接口
     * @param reqMessageBody
     * @return
     */
    @FormUrlEncoded
    @POST(UrlContanier.Register)
    Observable<HttpResult<RegisterResponse>> Register(@Field("ReqMessageBody") String reqMessageBody);

}
