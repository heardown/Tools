package com.sayweee.core.presenter;


import android.util.Log;

import androidx.collection.ArrayMap;

import com.sayweee.core.bean.BaseBean;
import com.sayweee.core.http.Callback;
import com.sayweee.core.http.HttpManager;
import com.sayweee.core.http.RequestInfo;
import com.sayweee.core.http.ResponseInfo;

import java.io.Serializable;


public class BaseMvpPresenter extends MvpPresenter<MvpView> implements Callback {

    public final static String TAG = "MvpBasePresenter";

    protected boolean needDialog = true;
    public int requestCount;

    /**
     * 请求成功，回调View层方法处理成功的结果
     *
     * @param responseInfo 包含的返回数据的BaseVo子类对象
     */
    @Override
    public void onSuccess(RequestInfo requestInfo, ResponseInfo responseInfo) {
        if (isViewAttached()) {
            processSuccessResponse(requestInfo, responseInfo);
        } else {
            Log.e(TAG, "View已被销毁，onSuccess方法无法回调showContentView方法 ==> " + viewClassName);
        }
    }

    protected void processSuccessResponse(RequestInfo requestInfo, ResponseInfo responseInfo) {
        requestCount--;
        getView().beforeSuccess();
        getView().onResponse(responseInfo.url, responseInfo.dataVo == null ? new BaseBean() : responseInfo.dataVo);
        if (requestCount <= 0) {
            getView().onStopLoading();
        }
    }

    /**
     * 请求失败，回调View层的方法处理错误信息
     *
     * @param responseInfo 包含错误码和错误信息的BaseVo子类对象
     */
    @Override
    public void onError(ResponseInfo responseInfo) {
        if (isViewAttached()) {
            processErrorResponse(responseInfo);
        } else {
            Log.e(TAG, "MvpView已销毁，onError方法无法回调MvpView层的方法 ==> " + viewClassName);
        }
    }

    protected void processErrorResponse(ResponseInfo responseInfo) {
        requestCount--;
        getView().onStopLoading();
        boolean process = getView().onError(responseInfo);
        if(!process) {
            switch (responseInfo.getState()) {
                case ResponseInfo.FAILURE:  //请求出错
                case ResponseInfo.CACHE_PARSE_ERROR://缓存数据解析错误
                case ResponseInfo.JSON_PARSE_ERROR://Json数据解析错误
                    showToast(responseInfo.msg);
                    break;
                case ResponseInfo.TIME_OUT: //请求超时
                    showToast("网络连接不稳定，请检查网络设置");
                    break;
                case ResponseInfo.NO_INTERNET_ERROR:    //无网络连接
                    showToast("没有可用的网络，请检查您的网络设置");
                    break;
                case ResponseInfo.SERVER_UNAVAILABLE:   //服务器无法访问
                    showToast("接口访问失败");
                    break;
                case ResponseInfo.UN_LOGIN: //未登录或登录失效
                    login();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void detachView() {
        super.detachView();
        //取消默认的还未完成的请求
        HttpManager.get().cancelAll(getView());
    }


    /**
     * Get请求
     *
     * @param url
     * @param params 参数
     */
    public void getData(String url, ArrayMap<String, Serializable> params) {
        getData(url, params, null);
    }

    /**
     * Get请求
     *
     * @param url
     * @param dataClass
     */
    public void getData(String url, Class<? extends BaseBean> dataClass) {
        getData(url, null, dataClass);
    }

    /**
     * Get请求
     *
     * @param url
     * @param params    参数
     * @param dataClass 对象类型
     */
    public void getData(String url, ArrayMap<String, Serializable> params, Class<? extends BaseBean> dataClass) {
        if (isViewAttached() && needDialog && requestCount >= 0) {
            getView().onLoading();
        }

        RequestInfo requestInfo = new RequestInfo(url, dataClass);
        requestInfo.setRequestType(RequestInfo.REQUEST_GET);
        requestInfo.setRequestParams(params);
        HttpManager.get().get(requestInfo, this);
        requestCount++;
    }

    /**
     * Post请求
     *
     * @param url
     * @param params 参数
     */
    public void postData(String url, ArrayMap<String, Serializable> params) {
        postData(url, params, null);
    }

    /**
     * Post请求
     *
     * @param url
     * @param dataClass 对象类型
     */
    public void postData(String url, Class<? extends BaseBean> dataClass) {
        postData(url, null, dataClass);
    }

    /**
     * Post请求
     *
     * @param url
     * @param params    参数
     * @param dataClass 对象类型
     */
    public void postData(String url, ArrayMap<String, Serializable> params, Class<? extends BaseBean> dataClass) {
        if (isViewAttached() && needDialog && requestCount >= 0) {
            getView().onLoading();
        }
        RequestInfo requestInfo = new RequestInfo(url, dataClass);
        requestInfo.setRequestType(RequestInfo.REQUEST_POST);
        requestInfo.setRequestParams(params);
        HttpManager.get().post(requestInfo, this);
        requestCount++;
    }


    public void postJson(String url, ArrayMap<String, Serializable> params, Class<? extends BaseBean> clazz) {
        postJson(false, url, null, params, clazz);
    }

    /**
     * Post方式上传Json数据
     *
     * @param isDirect     是否为直连方式   是的情况的 不再拼接host
     * @param url          请求的url
     * @param headerParams 请求头参数
     * @param params
     * @param clazz        要解析的数据
     */
    public void postJson(boolean isDirect, String url, ArrayMap<String, String> headerParams, ArrayMap<String, Serializable> params, Class<? extends BaseBean> clazz) {
        if (isViewAttached() && needDialog && requestCount >= 0) {
            getView().onLoading();
        }
        RequestInfo requestInfo = new RequestInfo(isDirect, url, clazz);
        requestInfo.setRequestType(RequestInfo.REQUEST_POST_JSON);
        requestInfo.setRequestParams(params);
        requestInfo.setHeaderParams(headerParams);
        HttpManager.get().post(requestInfo, this);
        requestCount++;
    }

    /**
     * 判断请求时是否需要Dialog
     *
     * @return 默认true
     */
    public boolean isNeedDialog() {
        return needDialog;
    }

    /**
     * 设置请求时是否需要加载进度条   需要在请求前设置
     *
     * @param needDialog 默认true
     */
    public void setNeedDialog(boolean needDialog) {
        this.needDialog = needDialog;
    }

    /**
     * 设置客户端类型
     */
    public void setClientType() {

    }

    public int getRequestCount() {
        return requestCount;
    }

    /**
     * 登录
     */
    protected void login() {

    }

    /**
     * 提示
     *
     * @param msg
     */
    protected void showToast(String msg) {

    }
}
