package com.iilu.fendou.nets.http.result;

import java.util.List;

/**
 * 统一封装http的回调，包括状态码、返回信息，返回体
 */
public class HttpResult<T> {

    private String resultCode;
    private String respMsg;

    private List<T> data;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getRespMsg() {
        return respMsg;
    }

    public void setRespMsg(String respMsg) {
        this.respMsg = respMsg;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
