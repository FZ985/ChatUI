package io.im.lib.utils;

import android.util.Log;

/**
 * Created by JFZ on 2017/11/3.
 * <p>
 * 日志打印类
 */

public class JLog {
    private static final String TAG = "##DOG##";
    private static final String SUFFIX = ".java";
    public static boolean DEBUG = true;
    private static int LOG_MAX_LENGTH = 800;

    public static void i(String value) {
        if (DEBUG) {
            i(TAG, value);
        }
    }

    public static void i(String tag, String value) {
        if (DEBUG) {
            int strLength = value.length();
            int start = 0;
            int end = LOG_MAX_LENGTH;
            for (int i = 0; i < 100; i++) {
                if (strLength > end) {
                    Log.i(tag + i, value.substring(start, end));
                    start = end;
                    end = end + LOG_MAX_LENGTH;
                } else {
                    Log.i(tag + i, value.substring(start, strLength));
                    break;
                }
            }
        }
    }

    public static void e(String value) {
        if (DEBUG) {
            e(TAG, value);
        }
    }

    public static void e(String tag, String value, Throwable e) {
        e(tag, value + "," + e.getMessage());
    }

    public static void e(String tag, String value) {
        if (DEBUG) {
            String result = logDetail();
            int strLength = (result + value).length();
            int start = 0;
            int end = LOG_MAX_LENGTH;
            for (int i = 0; i < 100; i++) {
                if (strLength > end) {
                    Log.e(tag + i, (result + value).substring(start, end));
                    start = end;
                    end = end + LOG_MAX_LENGTH;
                } else {
                    Log.e(tag + i, (result + value).substring(start, strLength));
                    break;
                }
            }
        }
    }

    public static String logDetail() {
        String result = "\n";
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

//        StackTraceElement thisMethodStack = (new Exception()).getStackTrace()[1];
        StackTraceElement thisMethodStack = stackTrace[5];

        String className = thisMethodStack.getClassName();
        String[] classNameInfo = className.split("\\.");
        if (classNameInfo.length > 0) {
            className = classNameInfo[classNameInfo.length - 1] + SUFFIX;
        }
        if (className.contains("$")) {
            className = className.split("\\$")[0] + SUFFIX;
        }
        result += className + "."; // 当前的类名（全名）
        result += thisMethodStack.getMethodName();
        result += "(" + thisMethodStack.getFileName();
        result += ":" + thisMethodStack.getLineNumber() + ")  \n";
        return result;
    }

}
