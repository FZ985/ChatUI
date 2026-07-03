package io.im.uicommon.utils;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;

import io.im.core.core.ChatSDK;
import io.im.core.utils.ChatToast;
import io.im.uicommon.R;

/**
 * by DAD FZ
 * 2026/7/2
 * desc：
 **/
public class TextUtil {

    /**
     * 复制文本到剪切板
     *
     * @param text      文本内容
     * @param showToast 是否显示Toast提示
     */
    public static void copyText(String text, boolean showToast) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        ClipboardManager cmb = (ClipboardManager) ChatSDK.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(null, text);
        if (clipData != null) {
            cmb.setPrimaryClip(clipData);
            if (showToast) {
                ChatToast.toast(ChatSDK.getContext(), R.string.chat_message_action_copy_success);
            }
        }
    }
}
