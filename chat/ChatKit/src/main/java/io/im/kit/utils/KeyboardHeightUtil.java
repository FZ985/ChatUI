package io.im.kit.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * author : JFZ
 * date : 2024/1/29 15:52
 * description :
 */
public class KeyboardHeightUtil {

    private static int TEMP_KEYBOARD_HEIGHT = -1;
    private static int TEMP_KEYBOARD_ORIENTATION = -1;

    private static final String IM_KIT = "IM_KIT";

    public static int getSaveKeyBoardHeight(Context context, int orientation) {
        if (TEMP_KEYBOARD_HEIGHT == -1 || orientation != TEMP_KEYBOARD_ORIENTATION) {
            SharedPreferences sharedPreferences =
                    context.getSharedPreferences(IM_KIT, Context.MODE_PRIVATE);
            int height = sharedPreferences.getInt(getKeyboardHeightKey(orientation), 0);
            TEMP_KEYBOARD_HEIGHT = height;
            TEMP_KEYBOARD_ORIENTATION = orientation;
            return height;
        } else {
            return TEMP_KEYBOARD_HEIGHT;
        }
    }

    public static void saveKeyboardHeight(Context context, int orientation, int height) {
        if (TEMP_KEYBOARD_HEIGHT != height || orientation != TEMP_KEYBOARD_ORIENTATION) {
            TEMP_KEYBOARD_HEIGHT = height;
            TEMP_KEYBOARD_ORIENTATION = orientation;
            SharedPreferences sharedPreferences =
                    context.getSharedPreferences(IM_KIT, Context.MODE_PRIVATE);
            sharedPreferences
                    .edit()
                    .putInt(getKeyboardHeightKey(orientation), height)
                    .apply();
        }
    }

    private static String getKeyboardHeightKey(int orientation) {
        return "KEY_BROADCAST_HEIGHT_" + orientation;
    }
}
