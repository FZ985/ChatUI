package io.chat.conversation.provider;


import android.app.Application;

import io.chat.conversation.ui.ConversationActivity;
import io.im.core.core.init.IKitInitializer;
import io.im.core.core.init.InitializerManager;
import io.im.core.core.init.InitializerProvider;
import io.im.core.utils.JLog;
import io.im.uicommon.route.IMRoute;
import io.im.uicommon.route.RouterConstant;

/**
 * by DAD FZ
 * 2026/5/30
 * desc：会话初始化
 **/
public final class InitConversationProvider extends InitializerProvider implements IKitInitializer {

    public static final String key = "InitConversationService";


    @Override
    protected void onInitializer() {
        JLog.e("======InitChatProvider====init");
        InitializerManager.getInstance().registerInitializer(key, this);
    }

    @Override
    public void init(Application application) {
        IMRoute.registerRouter(RouterConstant.PAGE_CONVERSATION_PAGE, ConversationActivity.class);
    }
}
