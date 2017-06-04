package com.iilu.fendou.nets.http.result;

import rx.Observable;
import rx.functions.Func1;

/**
 * 根据服务器的返回码判断，非“0000”，代表访问出错，抛出异常。
 */
public class HttpResultFunc<T> implements Func1<HttpResult<T>, Observable<T>> {

    //成功返回码
    public static final String SUCCESS = "0000";

    @Override
    public Observable<T> call(HttpResult<T> tHttpResult) {
        if(!SUCCESS.equals(tHttpResult.getResultCode())){
            throw new ApiException(tHttpResult.getResultCode(), tHttpResult.getRespMsg());
        }
        return Observable.from(tHttpResult.getData());
    }
}
