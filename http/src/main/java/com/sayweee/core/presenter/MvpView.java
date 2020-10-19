package com.sayweee.core.presenter;

import com.sayweee.core.bean.BaseBean;
import com.sayweee.core.http.ResponseInfo;


public interface MvpView {
    /**
     * 开始加载数据时回调此方法，用以显示加载ProgressDialog或者其他的的操作
     */
    void onLoading();

    /**
     * 加载数据完成回调方法
     */
    void onStopLoading();

    /**
     * 数据加载成功后的回调
     */
    void beforeSuccess();

    /**
     * 默认请求数据解析成功后，将数据填充到View，并显示View
     *
     * @param url  请求的url
     * @param bean 解析成功后返回VO对象
     */
    void onResponse(String url, BaseBean bean);

    /**
     * 加载失败回调
     * @param response
     * @return  是否自行处理
     */
    boolean onError(ResponseInfo response);
}
