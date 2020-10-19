package com.sayweee.core.http;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.collection.ArrayMap;

import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okgo.request.PostRequest;
import com.lzy.okgo.request.PutRequest;
import com.lzy.okgo.request.base.BodyRequest;
import com.lzy.okgo.request.base.Request;
import com.sayweee.core.HttpConfig;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Author:  winds
 * Data:    2020/10/19
 * Version: 1.0
 * Desc:
 */
public class HttpManager {

    private static class Builder {
        private static HttpManager instance = new HttpManager();
    }

    public static HttpManager get() {
        return Builder.instance;
    }

    /**
     * Get请求
     *
     * @param callback 数据加载成功后的回调方法
     */
    public void get(RequestInfo requestInfo, final Callback callback) {
        GetRequest request = OkGo.get(requestInfo.url);
        ArrayMap<String, Serializable> params = requestInfo.requestParams;
        if (params != null && params.size() > 0) {
            for (Map.Entry<String, Serializable> entry : params.entrySet()) {
                request.params(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        setHeaderParams(requestInfo.headerParams, request);
        enqueue(request, new ResponseCallback(callback, requestInfo), callback);
    }

    /**
     * Post请求
     *
     * @param requestInfo 请求体
     * @param callback    数据加载成功后的回调方法
     */
    public void post(RequestInfo requestInfo, final Callback callback) {
        PostRequest request = OkGo.post(requestInfo.url);
        ArrayMap<String, Serializable> params = requestInfo.getRequestParams();
        switch (requestInfo.getRequestType()) {
            case RequestInfo.REQUEST_POST:
                setParams(params, request);
                break;
            case RequestInfo.REQUEST_POST_JSON:
                request.upJson(toJson(params));
                break;
        }
        setHeaderParams(requestInfo.headerParams, request);
        enqueue(request, new ResponseCallback(callback, requestInfo), callback);
    }

    /**
     * Put请求
     *
     * @param callback 数据加载成功后的回调方法
     */
    public void put(RequestInfo requestInfo, final Callback callback) {
        PutRequest request = OkGo.put(requestInfo.url);
        ArrayMap<String, Serializable> params = requestInfo.requestParams;
        switch (requestInfo.getRequestType()) {
            case RequestInfo.REQUEST_PUT:
                setParams(params, request);
                break;
            case RequestInfo.REQUEST_PUT_JSON:
                request.upJson(toJson(params));
                break;
        }
        setHeaderParams(requestInfo.headerParams, request);
        enqueue(request, new ResponseCallback(callback, requestInfo), callback);
    }

    /**
     * 定制请求 着重定制 requestInfo中request对象 分别设置请求url、params、header
     *
     * @param requestInfo
     * @param callback
     */
    public void custom(RequestInfo requestInfo, final Callback callback) {
        if (requestInfo.request != null) {
            enqueue(requestInfo.request, new ResponseCallback(callback, requestInfo), callback);
        }
    }

    /**
     * 上传文件
     *
     * @param url
     * @param params
     * @param callback
     */
    public void upload(String url, ArrayMap<String, Serializable> params, FileCallback callback) {
        enqueue(setParams(params, OkGo.post(url)), callback, null);
    }

    /**
     * 下载
     *
     * @param url
     * @param params
     * @param callback
     */
    public void download(String url, ArrayMap<String, Serializable> params, FileCallback callback) {
        enqueue(setParams(params, OkGo.post(url)), callback, null);
    }

    /**
     * 入队请求 并统一网络判断
     *
     * @param request
     * @param requestCallback
     */
    private void enqueue(Request request, AbsCallback requestCallback, Callback responseCallback) {
        if (isNetworkConnected()) {
            request.execute(requestCallback);
        } else {
            if (responseCallback != null) {
                ResponseInfo responseInfo = new ResponseInfo(ResponseInfo.NO_INTERNET_ERROR);
                responseInfo.url = request.getUrl();
                responseCallback.onError(responseInfo);
            } else {
                Response response = new Response();
                response.setException(new IllegalStateException("无网络，请检查网络状况"));
                requestCallback.onError(response);
            }
        }
    }

    /**
     * 把参数转成json字符串
     *
     * @param params
     * @return
     */
    private String toJson(ArrayMap<String, Serializable> params) {
        JSONObject json = new JSONObject();
        if (params != null && params.size() > 0) {
            for (Map.Entry<String, Serializable> entry : params.entrySet()) {
                json.put(entry.getKey(), entry.getValue());
            }
        }
        return json.toJSONString();
    }

    /**
     * 设置请求参数
     *
     * @param params 请求参数
     * @param post   请求类型
     * @return
     */
    private BodyRequest setParams(ArrayMap<String, Serializable> params, BodyRequest post) {
        if (params != null && params.size() > 0) {
            for (Map.Entry<String, Serializable> entry : params.entrySet()) {
                if (entry.getValue() instanceof File) {
                    post.params(entry.getKey(), (File) entry.getValue());
                } else if (entry.getValue() instanceof List) {
                    post.addFileParams(entry.getKey(), (List<File>) entry.getValue());
                    post.isMultipart(true);
                } else {
                    post.params(entry.getKey(), String.valueOf(entry.getValue()));
                }
            }
        }
        return post;
    }

    /**
     * 设置请求头参数
     *
     * @param params
     * @param request
     * @return
     */
    private Request setHeaderParams(ArrayMap<String, String> params, Request request) {
        if (params != null && params.size() > 0) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                request.headers(entry.getKey(), entry.getValue());
            }
        }
        return request;
    }

    /**
     * 检查网络是否已经连接
     *
     * @return
     */
    public boolean isNetworkConnected() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) HttpConfig.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission")
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        return mNetworkInfo != null && mNetworkInfo.isConnected();
    }

    /**
     * 取消指定Tag的请求
     *
     * @param tag
     */
    public void cancelAll(Object tag) {
        if (tag != null) {
            OkGo.getInstance().cancelTag(tag);
        }
    }

    public void cancelAll() {
        OkGo.getInstance().cancelAll();
    }

}
