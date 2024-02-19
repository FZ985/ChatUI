package io.im.kit;

import android.annotation.SuppressLint;
import android.app.Application;

import io.im.kit.config.Options;
import io.im.kit.conversation.extension.component.emoticon.ChatAndroidEmoji;
import io.im.lib.utils.JLog;
import io.im.lib.utils.SystemUtil;
import io.im.lib.utils.language.ChatConfigurationManager;

/**
 * author : JFZ
 * date : 2024/1/26 10:51
 * description :
 */
public class IMCenter {

    private static final Options defaultOptions = new Options();
    private static Options options;
    private final static String TAG = "IMCenter";

    private IMCenter() {

    }

    public static IMCenter getInstance() {
        return SingletonHolder.sInstance;
    }

    public static void init(Application application) {
        init(application, defaultOptions);
    }

    public static void init(Application application, Options options) {
        IMCenter.options = options;
        String current = SystemUtil.getProcessName(application);
        String mainProcessName = application.getPackageName();
        if (!mainProcessName.equals(current)) {
            JLog.e(TAG, "Init. Current process : " + current);
            return;
        }
        ChatConfigurationManager.init(application);
        ChatAndroidEmoji.init(application);
    }

    public Options getOptions() {
        if (options == null) {
            return defaultOptions;
        }
        return options;
    }

    private static class SingletonHolder {
        @SuppressLint("StaticFieldLeak")
        private static final IMCenter sInstance = new IMCenter();
    }
}
