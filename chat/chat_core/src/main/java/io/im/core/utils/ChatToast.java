package io.im.core.utils;

import android.content.Context;
import android.widget.Toast;

public final class ChatToast {
    public static void toast(Context mContext, String s) {
        if (mContext == null) {
            return;
        }
        Toast.makeText(mContext.getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    public static void toast(Context mContext, int s) {
        if (mContext == null) {
            return;
        }
        Toast.makeText(mContext.getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
}
