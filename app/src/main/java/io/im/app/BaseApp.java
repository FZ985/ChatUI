package io.im.app;

import androidx.multidex.MultiDexApplication;

import io.im.app.ext.AITextMessageProvider;
import io.im.uicommon.config.ChatMessageProvider;

/**
 * author : JFZ
 * date : 2024/1/26 11:15
 * description :
 */
public class BaseApp extends MultiDexApplication {

    private static BaseApp app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        ImDebug.init(this);
        configIm();
    }

    private void configIm() {
        ChatMessageProvider.addMessageProvider(io.im.core.message.im.AIMessage.TYPE_AI_MESSAGE, new AITextMessageProvider(), io.im.core.message.im.AIMessage.class);
    }
}
