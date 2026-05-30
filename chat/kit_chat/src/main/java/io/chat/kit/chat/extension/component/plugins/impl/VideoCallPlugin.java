package io.chat.kit.chat.extension.component.plugins.impl;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import io.chat.kit.R;
import io.chat.kit.chat.extension.component.plugins.ChatPluginModule;

/**
 * by JFZ
 * 2024/3/16
 * desc：
 **/
public class VideoCallPlugin implements ChatPluginModule {
    @Override
    public Drawable obtainDrawable(Context context) {
        return ContextCompat.getDrawable(context,R.drawable.kit_default_plugin_video_call);
    }

    @Override
    public String obtainTitle(Context context) {
        return ContextCompat.getString(context, R.string.plugin_video_call);
    }
}
