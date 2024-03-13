package io.im.kit.ui.dialog;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * by JFZ
 * 2024/3/5
 * desc：m3 主题的 alert 弹窗
 **/
public class IMAlert extends MaterialAlertDialogBuilder {
    private AlertDialog dialog;

    public IMAlert(@NonNull Context context) {
        super(context);
        compatAlert(context);
    }

    public IMAlert(@NonNull Context context, int overrideThemeResId) {
        super(context, overrideThemeResId);
        compatAlert(context);
    }

    private void compatAlert(Context context) {
        if (context instanceof AppCompatActivity) {
            //绑定生命周期，防止页面销毁没有正常关闭弹窗
            ((AppCompatActivity) context).getLifecycle().addObserver(new DefaultLifecycleObserver() {
                @Override
                public void onDestroy(@NonNull LifecycleOwner owner) {
                    try {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        dialog = null;
                    } catch (Exception e) {
                        Log.e("dialog", "dialog销毁失败===");
                    }
                }
            });
        }
    }

    @Override
    public AlertDialog show() {
        dialog = super.create();
        dialog.show();
        return dialog;
    }

    @NonNull
    @Override
    public AlertDialog create() {
        dialog = super.create();
        return dialog;
    }
}
