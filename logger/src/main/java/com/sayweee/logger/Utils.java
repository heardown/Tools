package com.sayweee.logger;


import android.os.Bundle;

import com.alibaba.fastjson.JSON;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * Author:  winds
 * Email:   heardown@163.com
 * Date:    2019/8/29.
 * Desc:
 */
public class Utils {

    /**
     * Copied from "android.util.Log.getStackTraceString()" in order to avoid usage of Android stack
     * in unit tests.
     *
     * @return Stack trace in form of String
     */
    public static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "e is null";
        }
        try {
            // This is to reduce the amount of log spew that apps do in the non-error
            // condition of the network being unavailable.
            Throwable t = tr;
            while (t != null) {
                if (t instanceof UnknownHostException) {
                    return "UnknownHostException";
                }
                t = t.getCause();
            }

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            tr.printStackTrace(pw);
            pw.flush();
            return sw.toString();
        } catch (Exception e) {
            return "parse failed to print";
        }
    }

    public static String toString(Object obj) {
        if (obj == null) {
            return "Object is null ";
        }
        if(obj instanceof Throwable) {
            return getStackTraceString((Throwable) obj);
        }
        if (!obj.getClass().isArray()) {
            return obj.toString();
        }
        if (obj instanceof boolean[]) {
            return Arrays.toString((boolean[]) obj);
        }
        if (obj instanceof byte[]) {
            return Arrays.toString((byte[]) obj);
        }
        if (obj instanceof char[]) {
            return Arrays.toString((char[]) obj);
        }
        if (obj instanceof short[]) {
            return Arrays.toString((short[]) obj);
        }
        if (obj instanceof int[]) {
            return Arrays.toString((int[]) obj);
        }
        if (obj instanceof long[]) {
            return Arrays.toString((long[]) obj);
        }
        if (obj instanceof float[]) {
            return Arrays.toString((float[]) obj);
        }
        if (obj instanceof double[]) {
            return Arrays.toString((double[]) obj);
        }
        if (obj instanceof Object[]) {
            return Arrays.deepToString((Object[]) obj);
        }
        return obj.toString();
    }

    public static String toJsonString(Object obj) {
        if (obj == null) {
            return "Object is null ";
        }
        try {
            if(obj instanceof Throwable) {
                return getStackTraceString((Throwable) obj);
            }

            if(obj instanceof Bundle) {
                return obj.toString();
            }
            return JSON.toJSONString(obj);
        } catch (Exception e) {
            return obj.toString();
        }
    }

    public static String toJson(Object obj) {
        if(obj != null) {
            try {
                String json = Utils.toJsonString(obj);
                if (json.startsWith("{")) {
                    JSONObject jsonObject = new JSONObject(json);
                    String message = jsonObject.toString(2);
                    return message;
                }
                if (json.startsWith("[")) {
                    JSONArray jsonArray = new JSONArray(json);
                    String message = jsonArray.toString(2);
                    return message;
                }
                return json;
            } catch (Exception e) {
                return "parse failed to print";
            }
        }
        return "Object is null";
    }

    public static boolean isEmpty(String msg) {
        return msg == null || msg.length() == 0;
    }
}
