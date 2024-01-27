package io.im.lib.utils;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.os.Build;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * author : JFZ
 * date : 2024/1/26 10:32
 * description :
 */
public class ActivityFix {
    private static final String TAG = "ActivityFix";

    // 解决 Android 8.0 透明主题 Activity 崩溃问题
    public static void fixAndroid8ActivityCrash(Activity activity) {
        if (activity != null
                && Build.VERSION.SDK_INT == Build.VERSION_CODES.O
                && isTranslucentOrFloating(activity)) {
            fixOrientation(activity);
        }
    }

    private static boolean isTranslucentOrFloating(Activity activity) {
        boolean isTranslucentOrFloating = false;
        try {
            int[] styleableRes =
                    (int[]) Class.forName("com.android.internal.R$styleable")
                            .getField("Window")
                            .get(null);
            final TypedArray ta = activity.obtainStyledAttributes(styleableRes);
            Method m = ActivityInfo.class.getMethod("isTranslucentOrFloating", TypedArray.class);
            m.setAccessible(true);
            isTranslucentOrFloating = (boolean) m.invoke(null, ta);
            m.setAccessible(false);
        } catch (Exception e) {
            JLog.e(TAG, "isTranslucentOrFloating: " + "e = " + e.getMessage());
        }
        return isTranslucentOrFloating;
    }

    private static void fixOrientation(Activity activity) {
        try {
            Field field = Activity.class.getDeclaredField("mActivityInfo");
            field.setAccessible(true);
            ActivityInfo o = (ActivityInfo) field.get(activity);
            o.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
            field.setAccessible(false);
        } catch (Exception e) {
            JLog.e(TAG, "fixOrientation: " + "e = " + e.getMessage());
        }
    }
}
