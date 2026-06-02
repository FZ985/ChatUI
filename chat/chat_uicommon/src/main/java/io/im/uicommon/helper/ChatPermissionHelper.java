package io.im.uicommon.helper;


import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import io.im.core.listener.ChatFun;
import io.im.uicommon.R;

/**
 * by DAD FZ
 * 2026/6/1
 * desc：
 **/
public class ChatPermissionHelper {

    private static final int code = Math.abs("ChatPermissionHelper".hashCode());

    private static int offset;

    /**
     *
     * @param fragment       fragment
     * @param permission     权限
     * @param permissionDesc 权限描述
     * @param call           权限成功回调
     */
    public static void request(@NonNull Fragment fragment, @NonNull String permission, @NonNull String permissionDesc, @NonNull ChatFun.Fun call) {
        boolean hasPermission = ContextCompat.checkSelfPermission(fragment.requireActivity(), permission) == PackageManager.PERMISSION_GRANTED;
        if (hasPermission) {
            call.apply();
        } else {
            IMAlertHelper.with(fragment.requireActivity())
                    .title(R.string.im_alert_tip)
                    .message(permissionDesc)
                    .cancel(R.string.im_alert_next_say, ChatDialog::dismiss)
                    .confirm(R.string.im_alert_agree, dialog -> {
                        dialog.dismiss();
                        offset++;
                        ActivityCompat.requestPermissions(fragment.requireActivity(), new String[]{permission}, code + offset);
                    })
                    .show();
        }
    }

}
