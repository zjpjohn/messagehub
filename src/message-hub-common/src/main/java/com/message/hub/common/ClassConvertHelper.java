package com.message.hub.common;


/**
 * Created by shi on 7/8/2016.
 */
public class ClassConvertHelper {

    public static <T> T convert(Object obj,Class<T> c){
        String s = JsonConverter.toJson(obj);
        return JsonConverter.fromJson(s,c);
    }

}
