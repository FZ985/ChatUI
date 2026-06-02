package io.im.uicommon.helper;

import android.app.Activity;
import android.graphics.Rect;

import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.annotation.StringRes;

import io.im.core.listener.ChatFun;
import io.im.uicommon.R;
import io.im.uicommon.dialog.IMAlert;

/**
 * by JFZ
 * 2024/3/12
 * desc：统一的信息弹窗管理类
 **/
public class IMAlertHelper implements ChatDialog {

    private IMAlert alert;
    private boolean isCancelable = true;
    private boolean isCanceledOnTouchOutside = true;
    private ChatFun.Fun1<ChatDialog> dismissCall;

    private IMAlertHelper() {
    }

    public static IMAlertHelper with(Activity activity) {
        IMAlertHelper alert = new IMAlertHelper();
        alert.build(activity);
        return alert;
    }

    public static IMAlertHelper dialog(Activity activity) {
        IMAlertHelper alert = new IMAlertHelper();
        alert.build(activity);
        return alert.title(R.string.im_alert_tip);
    }

    private void build(Activity activity) {
        alert = new IMAlert(activity);
    }

    public IMAlertHelper title(@StringRes int title) {
        if (alert != null) {
            alert.titleRes(title);
        }
        return this;
    }

    public IMAlertHelper message(@StringRes int message) {
        if (alert != null) {
            alert.messageRes(message);
        }
        return this;
    }

    public IMAlertHelper messagePadding(Rect rect) {
        if (alert != null) {
            alert.messagePadding(rect);
        }
        return this;
    }

    public IMAlertHelper messageTextSize(@Px int size) {
        if (alert != null) {
            alert.messageTextSize(size);
        }
        return this;
    }

    public IMAlertHelper title(CharSequence title) {
        if (alert != null) {
            alert.title(title);
        }
        return this;
    }

    public IMAlertHelper message(CharSequence message) {
        if (alert != null) {
            alert.message(message);
        }
        return this;
    }

    public IMAlertHelper cancel(@StringRes int res, @Nullable ChatFun.Fun1<ChatDialog> call) {
        if (alert != null) {
            return cancel(alert.getContext().getResources().getString(res), call);
        }
        return this;
    }

    public IMAlertHelper confirm(@StringRes int res, @Nullable ChatFun.Fun1<ChatDialog> call) {
        if (alert != null) {
            return confirm(alert.getContext().getResources().getString(res), call);
        }
        return this;
    }

    public IMAlertHelper cancel(String res, @Nullable ChatFun.Fun1<ChatDialog> call) {
        if (alert != null) {
            alert.cancel(res);
            alert.cancelCall(dialog -> {
                if (call != null) {
                    call.apply(IMAlertHelper.this);
                } else {
                    dialog.dismiss();
                }
            });
        }
        return this;
    }

    public IMAlertHelper confirm(String res, @Nullable ChatFun.Fun1<ChatDialog> call) {
        if (alert != null) {
            alert.confirm(res);
            alert.confirmCall(dialog -> {
                if (call != null) {
                    call.apply(IMAlertHelper.this);
                } else {
                    dialog.dismiss();
                }
            });
        }
        return this;
    }

    public IMAlertHelper setCancelable(boolean cancelable) {
        isCancelable = cancelable;
        return this;
    }

    public IMAlertHelper setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        isCanceledOnTouchOutside = canceledOnTouchOutside;
        return this;
    }

    @Override
    public void show() {
        if (alert != null) {
            alert.setCancelable(isCancelable);
            alert.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
            if (dismissCall != null) {
                alert.setOnDismissListener(dialog -> {
                    if (dismissCall != null) {
                        dismissCall.apply(IMAlertHelper.this);
                    }
                    dismissCall = null;
                });
            }
            alert.show();
        }
    }

    @Override
    public void dismiss() {
        if (alert != null) {
            alert.dismiss();
        }
    }

    public IMAlertHelper setOnDismissListener(ChatFun.Fun1<ChatDialog> dismissCall) {
        this.dismissCall = dismissCall;
        return this;
    }

}
