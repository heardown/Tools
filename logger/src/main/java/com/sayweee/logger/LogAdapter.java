package com.sayweee.logger;


/**
 * Author:  winds
 * Email:   heardown@163.com
 * Date:    2019/8/29.
 * Desc:
 */
public interface LogAdapter {

    boolean isLoggable(int priority, String tag);

    void log(int priority, String tag, String extraTag, String message, String extraMessage);

}
