package com.sayweee.logger;

import android.text.TextUtils;
import android.util.Log;


public class DefaultLogAdapter implements LogAdapter {

    public final static String TAG = Logger.TAG;
    protected static final int SIZE = 3600;
    protected static final String TOP = "\n┌─────────────────────────────";
    protected static final String BOTTOM = "\n└─────────────────────────────";
    protected static DefaultLogAdapter adapter = new DefaultLogAdapter();

    public static DefaultLogAdapter newInstance() {
        return adapter;
    }

    @Override
    public boolean isLoggable(int priority, String tag) {
        return true;
    }

    @Override
    public void log(int priority, String tag, String extraTag, String message, String extraMessage) {
            tag = TextUtils.isEmpty(tag) ? TAG : tag;
            if (message != null && message.length() >= SIZE) {
                Log.println(priority, tag, TextUtils.isEmpty(extraMessage) ? "" : extraMessage + TOP);
                while (message.length() > SIZE) {// 循环分段打印日志
                    String content = message.substring(0, SIZE);
                    message = message.replace(content, "");
                    Log.println(priority, tag, TextUtils.isEmpty(extraMessage) ? "" : extraMessage + "\n" + message);
                }
                Log.println(priority, tag, TextUtils.isEmpty(extraMessage) ? "" : extraMessage + "\n" + message + BOTTOM);
            } else {
                Log.println(priority, tag, TextUtils.isEmpty(extraMessage) ? "" : extraMessage
                        + TOP
                        + "\r\n"
                        + message
                        + BOTTOM
                );
            }
    }

}
