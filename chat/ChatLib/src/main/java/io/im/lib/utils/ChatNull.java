package io.im.lib.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * author : JFZ
 * date : 2024/1/26 16:37
 * description :
 */
public class ChatNull {


    public static String compat(String value) {
        if (value == null) {
            value = "";
        }
        return value;
    }

    public static String compat(String value, String defaultVal) {
        if (value == null) {
            value = compat(defaultVal);
        }
        return value;
    }

    public static <T> List<T> compatList(List<T> list) {
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }
}
