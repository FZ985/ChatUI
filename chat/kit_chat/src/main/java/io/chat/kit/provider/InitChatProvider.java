package io.chat.kit.provider;


import android.app.Application;

import io.chat.kit.chat.IChatActivity;
import io.chat.kit.forward.IForwardSelectorActivity;
import io.im.core.core.init.IKitInitializer;
import io.im.core.core.init.InitializerManager;
import io.im.core.core.init.InitializerProvider;
import io.im.core.utils.JLog;
import io.chat.kit.chat.extension.component.emoticon.ChatAndroidEmoji;
import io.chat.kit.config.ChatOptions;
import io.im.uicommon.route.IMRoute;
import io.im.uicommon.route.RouterConstant;

/**
 * by DAD FZ
 * 2026/5/30
 * desc：聊天初始化，及获取配置相关
 **/
public final class InitChatProvider extends InitializerProvider implements IKitInitializer {

    public static final String key = "InitChatService";

    private static final ChatOptions options = new ChatOptions();

    @Override
    protected void onInitializer() {
        JLog.e("======InitChatProvider====init");
        InitializerManager.getInstance().registerInitializer(key, this);
    }

    @Override
    public void init(Application application) {
        IMRoute.registerRouter(RouterConstant.PAGE_CHAT_P2P, IChatActivity.class);
        IMRoute.registerRouter(RouterConstant.PAGE_CHAT_FORWARD, IForwardSelectorActivity.class);

        ChatAndroidEmoji.init(application);
    }

    public static ChatOptions getOptions() {
        return options;
    }
}
