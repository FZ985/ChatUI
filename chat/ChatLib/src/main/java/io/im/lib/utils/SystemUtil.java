package io.im.lib.utils;

import android.app.ActivityManager;
import android.content.Context;

/**
 * author : JFZ
 * date : 2024/1/26 11:10
 * description :
 */
public class SystemUtil {

    public static String getProcessName(Context context) {
        if (context == null) return "";
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == android.os.Process.myPid()) {
                return processInfo.processName;
            }
        }
        return "";
    }
}
