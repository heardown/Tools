package com.sayweee.core.http;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.model.Response;
import com.sayweee.core.HttpConfig;
import com.sayweee.core.bean.BaseBean;
import com.sayweee.core.bean.N;

import org.json.JSONException;

import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.Call;

/**
 * Author:  winds
 * Email:   heardown@163.com
 * Date:    2019/5/26.
 * Desc:
 */
public class ResponseParser {

    public final static String KEY_CODE = "code";
    public final static String KEY_MSG = "msg";
    public final static String KEY_DATA = "data";

    public final static String TYPE_JSON = "json";

    public static void parseError(RequestInfo requestInfo, Response<String> response, Callback callback) {
        ResponseInfo responseInfo;
        Throwable error = response.getException();
        if (error instanceof SocketTimeoutException) {
            responseInfo = new ResponseInfo(ResponseInfo.TIME_OUT);
        } else {
            responseInfo = new ResponseInfo(ResponseInfo.FAILURE);
            responseInfo.setMsg("服务器连接失败");
            responseInfo.setErrorObject(error);
            if (response != null && response.getRawResponse() != null) {
                responseInfo.setUrl(response.getRawResponse().request().url().url().getPath());
            }
        }

        postCallback(callback, requestInfo, responseInfo);
    }

    public static void parseSuccess(Response<String> response, RequestInfo requestInfo, Callback callback) {
        parseSuccess(response.body(), response.getRawCall(), response.getRawResponse(), requestInfo, callback);
    }

    public static void parseSuccess(String content, Call call, okhttp3.Response response, RequestInfo requestInfo, Callback callback) {
        ResponseInfo responseInfo;
        if (response.isSuccessful()) {
            String type = response.body().contentType().subtype();
            if (type.equals(TYPE_JSON)) {
                dispatchJsonResult(requestInfo, content, response.request().url().url().getPath(), type, callback);
            } else {
                responseInfo = new ResponseInfo(ResponseInfo.FAILURE);
                responseInfo.setResponseType(type);
                responseInfo.setUrl(response.request().url().url().getPath());
                responseInfo.setMsg("无法解析请求结果");
                try {
                    responseInfo.setRawData(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                postCallback(callback, requestInfo, responseInfo);
            }
        } else {
            responseInfo = new ResponseInfo(ResponseInfo.FAILURE);
            postCallback(callback, requestInfo, responseInfo);
        }
    }

    /**
     * 处理请求结果
     *
     * @param requestInfo
     * @param response
     * @param url
     * @param type
     * @param callback
     */
    private static void dispatchJsonResult(RequestInfo requestInfo, String response, String url, String type, Callback callback) {
        ResponseInfo responseInfo;
        try {
            if (N.class.isAssignableFrom(requestInfo.getDataClass())) { //不需要解析
                responseInfo = new ResponseInfo(ResponseInfo.SUCCESS);
                responseInfo.setResponseType(type);
                url = url.substring(1);
                responseInfo.setUrl(url);
                responseInfo.rawData = response;
                Class<? extends BaseBean> clazz = requestInfo.getDataClass();
                BaseBean bean = clazz.newInstance();
                ((N)bean).setRawData(response);
                responseInfo.setDataVo(bean);
                postCallback(callback, requestInfo, responseInfo);
                return;
            }

            JSONObject jsonObject = JSON.parseObject(response);
            int code = jsonObject.getIntValue(KEY_CODE);
            String msg = jsonObject.getString(KEY_MSG);
            String data = jsonObject.getString(KEY_DATA);
            responseInfo = new ResponseInfo(parseResultStatus(code), msg);
            responseInfo.setRawData(response);
            responseInfo.setResponseType(type);
            url = url.substring(1);
            responseInfo.setUrl(url);
            //返回状态非成功
            if (responseInfo.getState() != ResponseInfo.SUCCESS) {
                postCallback(callback, requestInfo, responseInfo);
                return;
            }

            //解析失败
            BaseBean baseVo = BaseBean.parseDataVo(data, requestInfo.getDataClass());
            if (baseVo == null) {
                responseInfo.setState(ResponseInfo.JSON_PARSE_ERROR);
                responseInfo.setMsg("请求结果数据解析失败！");
            }
            responseInfo.setDataVo(baseVo);
            postCallback(callback, requestInfo, responseInfo);

        } catch (JSONException ex) {
            responseInfo = new ResponseInfo(ResponseInfo.JSON_PARSE_ERROR);
            responseInfo.setMsg(ex.getMessage());
            postCallback(callback, requestInfo, responseInfo);
        } catch (Exception e) {
            responseInfo = new ResponseInfo(ResponseInfo.LOGIC_ERROR);
            responseInfo.setResponseType(type);
            postCallback(callback, requestInfo, responseInfo);
        }
    }

    private static void postCallback(final Callback callback, final RequestInfo requestInfo, final ResponseInfo responseInfo) {
        if (callback == null) {
            return;
        }
        if (HttpConfig.getInstance().isRunMainThread()) {
            if (responseInfo.getState() == ResponseInfo.SUCCESS) {
                callback.onSuccess(requestInfo, responseInfo);
            } else {
                callback.onError(responseInfo);
            }
            return;
        }

        HttpConfig.getInstance().runMainThread(new Runnable() {
            @Override
            public void run() {
                if (responseInfo.getState() == ResponseInfo.SUCCESS) {
                    callback.onSuccess(requestInfo, responseInfo);
                } else {
                    callback.onError(responseInfo);
                }
            }
        });
    }

    /**
     * 此处自定义解析返回状态
     *
     * @param status
     * @return
     */
    public static int parseResultStatus(int status) {
        if (ResponseInfo.SUCCESS == status) {
            return ResponseInfo.SUCCESS;
        } else if (ResponseInfo.FAILURE == status) {
            return ResponseInfo.FAILURE;
        } else if (ResponseInfo.UN_LOGIN == status) {
            return ResponseInfo.UN_LOGIN;
        }
        return status;
    }

}
