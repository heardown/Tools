package com.sayweee.logger;

import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Author:  winds
 * Email:   heardown@163.com
 * Date:    2019/8/29.
 * Desc:
 */
public class Logger {

    public static final int VERBOSE = Log.VERBOSE;
    public static final int DEBUG = Log.DEBUG;
    public static final int INFO = Log.INFO;
    public static final int WARN = Log.WARN;
    public static final int ERROR = Log.ERROR;
    public static final int ASSERT = Log.ASSERT;

    public static boolean sEnable = true;    //全局log开关
    public static boolean sLogEnable = true;   //控制台日志打印开关
    public static boolean sWritable = true;   //全局打印开关
    public static String sPath = Environment.getExternalStorageDirectory() + File.separator + Logger.class.getSimpleName(); //此路径受权限影响可能，可能无法打印

    public final static String TAG = Logger.class.getSimpleName();  //默认tag

    public static void setPath(String path) {
        sPath = path;
    }

    public static void setEnable(boolean enable) {
        sEnable = enable;
    }

    public static void setLogEnable(boolean logEnable){
        sLogEnable = logEnable;
    }

    public static void setWritable(boolean writable){
        sLogEnable = writable;
    }

    public static void config(String path, boolean enable) {
        sPath = path;
        sEnable = enable;
        sLogEnable = sEnable;
        sWritable = sEnable;
    }

    public static void config(String path, boolean logEnable, boolean writable) {
        sPath = path;
        sEnable = true;
        sLogEnable = logEnable;
        sWritable = writable;
    }

    private static Printer printer = new LoggerPrinter();

    /**
     * 为下一次打印设置tag
     *
     * @param tag
     * @return
     */
    public static Printer t(String tag) {
        return printer.t(tag);
    }

    /**
     * 设置下一次要写入的文件名称
     *
     * @param fileName
     * @return
     */
    public static Printer f(String fileName) {
        return printer.f(fileName);
    }

    /**
     * 设置下一次打印拼接方式
     *
     * @param space
     * @return
     */
    public static Printer s(String space) {
        return printer.s(space);
    }

    /**
     * 设置下一次能否打印
     *
     * @param enable
     * @return
     */
    public static Printer enable(boolean enable) {
        return printer.enable(enable);
    }

    /**
     * 强制下一次输入使用本次设置的适配器
     *
     * @param adapter
     * @return
     */
    public static Printer adapter(LogAdapter adapter) {
        return printer.adapter(adapter);
    }

    /**
     * 添加输出适配器
     *
     * @param adapter
     * @return
     */
    public static Printer addAdapter(LogAdapter adapter) {
        return printer.addAdapter(adapter);
    }

    /**
     * 移除适配器
     *
     * @param adapter
     * @return
     */
    public static Printer removeAdapter(LogAdapter adapter) {
        return printer.removeAdapter(adapter);
    }

    /**
     * 清空适配器
     *
     * @return
     */
    public static Printer clearAdapters() {
        return printer.clearAdapters();
    }

    public static void v(Object obj) {
        printer.v(obj);
    }

    public static void v(String tag, Object... args) {
        printer.v(tag, args);
    }

    public static void d(Object obj) {
        printer.d(obj);
    }

    public static void d(String tag, Object... args) {
        printer.d(tag, args);
    }

    public static void i(Object obj) {
        printer.i(obj);
    }

    public static void i(String tag, Object... args) {
        printer.i(tag, args);
    }

    public static void w(Object obj) {
        printer.w(obj);
    }

    public static void w(String tag, Object... args) {
        printer.w(tag, args);
    }

    public static void error(Throwable throwable) {
        printer.error(throwable);
    }

    public static void error(String tag, Throwable throwable) {
        printer.error(tag, throwable);
    }

    public static void e(Object obj) {
        if(obj instanceof Throwable) {
            printer.error((Throwable) obj);
        } else {
            printer.e(obj);
        }
    }

    public static void e(String tag, Object... args) {
        if(args != null && args.length == 1 && args[0] instanceof Throwable) {
            printer.error((Throwable) args[0]);
        }else{
            printer.e(tag, args);
        }
    }

    /**
     * 把对象转成json字符串并输出
     * 当打印的内容为对象/数组/集合时，会转成json字符串打印出来
     * 基本数据类型会以toString的方式打印
     *
     * @param obj
     */
    public static void json(Object obj) {
        printer.json(obj);
    }

    /**
     * 把对象转成json字符串并输出
     * 当打印的内容为对象/数组/集合时，会转成json字符串打印出来
     * 基本数据类型会以toString的方式打印
     *
     * @param tag
     * @param args
     */
    public static void json(String tag, Object... args) {
        printer.json(tag, args);
    }

    /**
     * 把对象先转成json字符串，然后以json格式输出
     * 支持对象/数组/集合/json字符串
     *
     * @param obj
     */
    public static void toJson(Object obj) {
        printer.toJson(obj);
    }

    /**
     * 把对象先转成json字符串，然后以json格式输出
     * 支持对象/数组/集合/json字符串
     *
     * @param tag
     * @param obj
     */
    public static void toJson(String tag, Object obj) {
        printer.toJson(tag, obj);
    }
}
