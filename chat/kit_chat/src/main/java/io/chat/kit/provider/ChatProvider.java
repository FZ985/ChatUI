package io.chat.kit.provider;


import android.app.Application;

import io.im.core.core.init.IKitInitializer;
import io.im.core.core.init.InitializerManager;
import io.im.core.core.init.InitializerProvider;
import io.im.core.utils.JLog;
import io.chat.kit.chat.extension.component.emoticon.ChatAndroidEmoji;
import io.chat.kit.config.ChatOptions;

/**
 * by DAD FZ
 * 2026/5/30
 * desc：聊天初始化，及获取配置相关
 **/
public final class ChatProvider extends InitializerProvider implements IKitInitializer {

    public static final String key = "ChatService";

    private static final ChatOptions options = new ChatOptions();

    @Override
    protected void onInitializer() {
        JLog.e("======ChatProvider====init");
        InitializerManager.getInstance().registerInitializer(key, this);
    }

    @Override
    public void init(Application application) {
        ChatAndroidEmoji.init(application);
    }

    public static ChatOptions getOptions() {
        return options;
    }
}
