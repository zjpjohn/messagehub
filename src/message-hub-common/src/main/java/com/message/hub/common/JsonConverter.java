
package com.message.hub.common;

import com.google.gson.Gson;

/**
 * Created by shi on 6/29/2016.
 */
public class JsonConverter {
    static Gson gson = new Gson();

    public static String toJson(Object object) {

        return gson.toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }
}
