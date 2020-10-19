package com.sayweee.core.http;

import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

/**
 * Author:  winds
 * Data:    2020/10/19
 * Version: 1.0
 * Desc:
 */
public class ResponseCallback extends StringCallback {
    public Callback callback;
    public RequestInfo requestInfo;

    public ResponseCallback(Callback callback, RequestInfo requestInfo) {
        this.callback = callback;
        this.requestInfo = requestInfo;
    }

    @Override
    public void onSuccess(Response<String> response) {
        ResponseParser.parseSuccess(response, requestInfo, callback);
    }

    @Override
    public void onError(Response<String> response) {
        super.onError(response);
        ResponseParser.parseError(requestInfo, response, callback);
    }


}
