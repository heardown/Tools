package com.sayweee.logger;


/**
 * Author:  winds
 * Email:   heardown@163.com
 * Date:    2019/8/29.
 * Desc:
 */
public interface Printer {

    /**
     * 针对下一次输出设置TAG
     *
     * @param tag
     * @return
     */
    Printer t(String tag);

    /**
     * 针对下一次输出，强制通过文件方式，并指定文件名
     *
     * @param fileName 文件名  支持log/name1/name2的目录格式
     * @return
     */
    Printer f(String fileName);

    /**˙
     * 设置输出连接格式 默认多个对象同时输出时以换行格式打印
     *
     * @param space
     * @return
     */
    Printer s(String space);

    /**
     * 设置下一次能否打印
     * @param enable
     * @return
     */
    Printer enable(boolean enable);
    /**
     * 使用当前适配器输出
     *
     * @param adapter
     * @return
     */
    Printer adapter(LogAdapter adapter);

    Printer addAdapter(LogAdapter adapter);

    Printer removeAdapter(LogAdapter adapter);

    Printer clearAdapters();

    void v(Object arg);

    void v(String tag, Object... args);

    void d(Object arg);

    void d(String tag, Object... args);

    void i(Object arg);

    void i(String tag, Object... args);

    void w(Object arg);

    void w(String tag, Object... args);

    void error(Throwable throwable);

    void error(String tag, Throwable throwable);

    void e(Object arg);

    void e(String tag, Object... args);

    void json(Object arg);

    /**
     * 把对象转成json字符串并输出
     *
     * @param tag
     * @param args
     */
    void json(String tag, Object... args);

    void toJson(Object obj);

    /**
     * 把对象先转成json字符串，然后以json格式输出
     *
     * @param tag
     * @param obj
     */
    void toJson(String tag, Object obj);

    void xml(Object obj);
}
