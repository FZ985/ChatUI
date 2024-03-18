package io.im.lib.utils;

import android.content.Context;

import com.google.gson.Gson;

import java.util.Random;

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

    public static int randomColor() {
        Random random = new Random();
        int ranColor = 0xff000000 | random.nextInt(0x00ffffff);
        return ranColor;
    }
}
