package com.sayweee.core.http;

import androidx.collection.ArrayMap;
import java.io.Serializable;

/**
 * Author:  winds
 * Data:    2020/10/19
 * Version: 1.0
 * Desc:
 */
public class RequestParams {
    ArrayMap<String, Serializable> params;

    public RequestParams() {
        this.params = new ArrayMap<>();
    }

    public RequestParams put(String key, Serializable value) {
        params.put(key, value);
        return this;
    }

    /**
     * 去除null 空字符串 -1 加入集合
     *
     * @param key
     * @param value
     * @return
     */
    public RequestParams putNonNull(String key, Serializable value) {
        if (value != null) {
            if (value instanceof String) {
                if ((((String) value).trim().length() == 0)) {
                    return this;
                }
            }

            if (value instanceof Integer) {
                if (((Integer) value) == -1) {
                    return this;
                }
            }
            params.put(key, value);
        }
        return this;
    }


    public Serializable get(String key) {
        return params.get(key);
    }

    public ArrayMap<String, Serializable> get() {
        return params;
    }
}
