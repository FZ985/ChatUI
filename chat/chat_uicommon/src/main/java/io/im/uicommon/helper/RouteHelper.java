package io.im.uicommon.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * author : JFZ
 * date : 2024/1/26 11:23
 * description :
 */
public class RouteHelper {

    public static void go(Context context, Intent intent) {
        if (context instanceof Activity) {
            context.startActivity(intent);
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
