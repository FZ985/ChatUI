package io.im.kit.helper;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/**
 * by JFZ
 * 2024/3/12
 * desc：统一的信息弹窗管理类
 **/
public class IMAlertHelper {
    private static String getAppName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        CharSequence label = packageManager.getApplicationLabel(applicationInfo);
        return label.toString();
    }
}
