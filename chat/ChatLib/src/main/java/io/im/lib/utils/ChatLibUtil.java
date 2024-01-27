package io.im.lib.utils;

import android.content.Context;

import com.google.gson.Gson;

/**
 * author : JFZ
 * date : 2024/1/27 11:40
 * description :
 */
public class ChatLibUtil {

    public static final Gson gson = new Gson();

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
