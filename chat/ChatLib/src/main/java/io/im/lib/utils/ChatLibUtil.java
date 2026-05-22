package io.im.lib.utils;

import android.content.Context;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

/**
 * author : JFZ
 * date : 2024/1/27 11:40
 * description :
 */
public class ChatLibUtil {

    public static final Gson gson = new Gson();


    public static String toJson(Object src) {
        String json = "";
        if (src != null && src instanceof String) {
            json = (String) src;
        } else {
            if (src == null) {
            } else {
                json = gson.toJson(src);
            }
        }
        return json;
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

    public static int randomNumber(int min, int max) {
        Random rand = new Random();
        return rand.nextInt(max - min) + min;
    }

    public static boolean isJson(String text) {
        try {
            new JSONObject(text);
            return true;
        } catch (JSONException e) {
            try {
                new JSONArray(text);
                return true;
            } catch (JSONException ex) {
                return false;
            }
        }
    }
}
