package com.sayweee.core.http;


import com.sayweee.core.bean.BaseBean;

/**
 * Author:  winds
 * Data:    2020/10/19
 * Version: 1.0
 * Desc:
 */
public class ResponseInfo {
    public static final int SUCCESS = 0; //请求成功
    public static final int FAILURE = -1; //请求出错
    public static final int MOCK_ERROR = -1; //模拟数据出错
    public static final int SERVER_INTERNAL_ERROR = 500; //服务器出错
    public static final int SERVER_UNAVAILABLE = 404; //服务器接口无法访问
    public static final int TIME_OUT = -2; //请求超时
    public static final int NO_INTERNET_ERROR = -3; //无网络连接
    public static final int CACHE_PARSE_ERROR = -4; //缓存数据解析错误
    public static final int JSON_PARSE_ERROR = -5; //json数据解析错误
    public static final int LOGIC_ERROR = -6; //下载失败
    public static final int UN_LOGIN = 4003; //未登录或登录失效

    public String url;
    public String tag;
    public int state; //请求结果码
    public String msg; //错误消息内容
    public Throwable errorObject; //数据请求过程中抛出的错误
    public boolean isCacheData = false; //是否是缓存数据
    public String rawData; //原始的返回数据,默认只有在返回的数据类型不是json的情况下才有值
    public String responseType; //数据响应类型
    public BaseBean dataVo; //json解析出来的Vo对象

    public ResponseInfo(int state) {
        this.state = state;
    }

    public ResponseInfo(int state, String msg) {
        this(state);
        this.msg = msg;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Throwable getErrorObject() {
        return errorObject;
    }

    public void setErrorObject(Throwable errorObject) {
        this.errorObject = errorObject;
    }

    public boolean isCacheData() {
        return isCacheData;
    }

    public void setCacheData(boolean cacheData) {
        isCacheData = cacheData;
    }

    public String getRawData() {
        return rawData;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public BaseBean getDataVo() {
        return dataVo;
    }

    public void setDataVo(BaseBean dataVo) {
        this.dataVo = dataVo;
    }
}
