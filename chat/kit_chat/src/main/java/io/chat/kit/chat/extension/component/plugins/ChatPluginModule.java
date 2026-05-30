package io.chat.kit.chat.extension.component.plugins;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.Nullable;

import io.im.uicommon.base.ChatBaseFragment;
import io.im.core.model.Message;

/**
 * by JFZ
 * 2024/3/16
 * desc：插件模版
 **/
public interface ChatPluginModule {

    /**
     * 获取 plugin 图标
     *
     * @param context 上下文
     * @return 图片的 Drawable
     */
    Drawable obtainDrawable(Context context);

    /**
     * 获取 plugin 标题
     *
     * @param context 上下文
     * @return 标题的字符串
     */
    String obtainTitle(Context context);

    /**
     * 插件点击
     *
     * @param fragment     插件所在页面
     * @param v            view
     * @param replyMessage 回复消息
     */
    default void onPluginClick(ChatBaseFragment fragment, View v, @Nullable Message replyMessage) {
    }

    /**
     * 插件长按
     *
     * @param fragment     插件所在页面
     * @param v            view
     * @param replyMessage 回复消息
     * @return true，false
     */
    default boolean onPluginLongClick(ChatBaseFragment fragment, View v, @Nullable Message replyMessage) {
        return false;
    }

    /**
     * 插件所在页面的结果回调
     *
     * @param fragment    插件所在页面
     * @param requestCode 请求code
     * @param resultCode  结果code
     * @param data        结果
     */
    default void onPluginActivityResult(ChatBaseFragment fragment, int requestCode, int resultCode, Intent data) {
    }

    /**
     * 插件释放
     */
    default void onPluginDestroy() {
    }
}
