package com.sayweee.core;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.lzy.okgo.BuildConfig;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.DBCookieStore;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.utils.HttpUtils;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.OkHttpClient;

/**
 * Author:  winds
 * Data:    2020/10/19
 * Version: 1.0
 * Desc:
 */
public class HttpConfig {

    private Application context;
    private String host;
    private Handler mMainHandler;

    private HttpConfig() {
        mMainHandler = new Handler(Looper.getMainLooper());
    }


    private static class ConfigBuilder {
        private static HttpConfig holder = new HttpConfig();
    }

    /**
     * 获取实例化的方法  第一次实例化 请在application   同时调用init方法
     *
     * @return
     */
    public static HttpConfig getInstance() {
        return ConfigBuilder.holder;
    }

    /**
     * 第一次实例化调用
     *
     * @param app
     * @return
     */
    public HttpConfig init(Application app) {
        context = app;
        return this;
    }

    /**
     * 设置通用的host 如不设置  默认为空
     *
     * @param host
     * @return
     */
    public HttpConfig setBaseHost(String host) {
        this.host = host;
        return this;
    }

    /**
     * 获取Application对象
     *
     * @return
     */
    public Application getApplicationContext() {
        HttpUtils.checkNotNull(context, "please call ManagerConfig.getInstance().init() first in application!");
        return context;
    }

    /**
     * 获取主线程handler
     *
     * @return
     */
    public Handler getMainThreadHandler() {
        HttpUtils.checkNotNull(context, "please call ManagerConfig.getInstance().init() first in application!");
        return mMainHandler;
    }

    /**
     * 获取主线程threadId
     *
     * @return
     */
    public long getMainThreadId() {
        return getMainThreadHandler().getLooper().getThread().getId();
    }

    /**
     * 获取通用host 在未设置时返回空
     *
     * @return
     */
    public String getBaseHost() {
        return host == null ? "" : host;
    }

    /**
     * 初始化okgo 默认提供通用实现
     *
     * @return
     */
    public HttpConfig initHttpClient() {
        HttpUtils.checkNotNull(context, "please call ManagerConfig.getInstance().init() first in application!");
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(" -- Response -- ");
            loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
            loggingInterceptor.setColorLevel(Level.INFO);
            builder.addInterceptor(loggingInterceptor);
        }
        //全局的读取超时时间
        builder.readTimeout(15 * 1000, TimeUnit.MILLISECONDS);
        //全局的写入超时时间
        builder.writeTimeout(15 * 1000, TimeUnit.MILLISECONDS);
        //全局的连接超时时间
        builder.connectTimeout(15 * 1000, TimeUnit.MILLISECONDS);

        //使用数据库保持cookie，如果cookie不过期，则一直有效
        builder.cookieJar(new CookieJarImpl(new DBCookieStore(context)));
        return initHttpClient(builder.build());
    }

    /**
     * 初始化OkGo  根据需求定制httpclient
     *
     * @param client
     * @return
     */
    public HttpConfig initHttpClient(OkHttpClient client) {
        HttpUtils.checkNotNull(context, "please call ManagerConfig.getInstance().init() first in application!");
        OkGo.getInstance().init(context)
                .setOkHttpClient(client)
                .setRetryCount(0);
        return this;
    }

    //判断当前的线程是不是在主线程
    public boolean isRunMainThread() {
        return android.os.Process.myTid() == getMainThreadId();
    }

    public void runMainThread(Runnable runnable) {
        if (isRunMainThread()) {
            runnable.run();
        } else {
            getMainThreadHandler().post(runnable);
        }
    }
}
