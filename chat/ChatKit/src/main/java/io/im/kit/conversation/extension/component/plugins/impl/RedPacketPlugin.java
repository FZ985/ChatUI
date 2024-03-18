package io.im.kit.conversation.extension.component.plugins.impl;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import io.im.kit.R;
import io.im.kit.conversation.extension.component.plugins.ChatPluginModule;

/**
 * by JFZ
 * 2024/3/16
 * desc：
 **/
public class RedPacketPlugin implements ChatPluginModule {
    @Override
    public Drawable obtainDrawable(Context context) {
        return ContextCompat.getDrawable(context,R.drawable.kit_default_plugin_red_packet);
    }

    @Override
    public String obtainTitle(Context context) {
        return ContextCompat.getString(context, R.string.plugin_red_packet);
    }
}
