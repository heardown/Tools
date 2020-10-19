package com.sayweee.core.http;

/**
 * Author:  winds
 * Data:    2020/10/19
 * Version: 1.0
 * Desc:
 */
public interface Callback {

    /**
     * 请求成功回调方法
     */
    void onSuccess(RequestInfo requestInfo, ResponseInfo responseInfo);

    /**
     * 请求失败回调方法
     */
    void onError(ResponseInfo responseInfo);
}
