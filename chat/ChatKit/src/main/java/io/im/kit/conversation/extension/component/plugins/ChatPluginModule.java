package io.im.kit.conversation.extension.component.plugins;

import android.content.Context;
import android.graphics.drawable.Drawable;

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

}
