package io.im.lib.utils.language;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.view.ContextThemeWrapper;

import java.util.Locale;

public class ChatConfigurationManager {
    private static final String Chat_CONFIG = "RongKitConfiguration";
    private static final String FILE_MAX_SIZE = "FileMaxSize";
    private static boolean isInit = false;

    private ChatConfigurationManager() {}

    private static class SingletonHolder {
        static ChatConfigurationManager sInstance = new ChatConfigurationManager();
    }

    /** 监听系统语言的切换，避免应用语言根随系统语言的切换而切换 */
    private static class SystemConfigurationChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_LOCALE_CHANGED.equals(intent.getAction())) {
                LangUtils.setSystemLocale(Locale.getDefault());
                LangUtils.ChatLocale appLocale = LangUtils.getAppLocale(context);
                Locale systemLocale = LangUtils.getSystemLocale();
                if (!appLocale.toLocale().equals(systemLocale)) {
                    ChatConfigurationManager.getInstance().switchLocale(appLocale, context);
                }
            }
        }
    }

    public static ChatConfigurationManager getInstance() {
        return SingletonHolder.sInstance;
    }

    public static void init(Context context) {
        if (!isInit) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_LOCALE_CHANGED);
            context.registerReceiver(new SystemConfigurationChangedReceiver(), filter);

            // 初始化时将应用语言重新设置为之前设置的语言
            LangUtils.ChatLocale locale =
                    ChatConfigurationManager.getInstance().getAppLocale(context);
            ChatConfigurationManager.getInstance().switchLocale(locale, context);
            isInit = true;
        }
    }

    /**
     * 用于切换语言
     *
     * @param locale 可传入的值为RCLocale.LOCALE_CHINA、ChatLocale.LOCALE_US 和 ChatLocale.AUTO
     */
    public void switchLocale(LangUtils.ChatLocale locale, Context context) {
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.locale = locale.toLocale();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            context.getResources().updateConfiguration(config, resources.getDisplayMetrics());
        }
        LangUtils.saveLocale(context, locale);
    }

    /**
     * 生成 ConfigurationContext，在 attachBaseContext 方法中替换
     *
     * @param newBase 上下文
     * @return ConfigurationContext
     */
    public Context getConfigurationContext(Context newBase) {
        Context context = LangUtils.getConfigurationContext(newBase);
        final Configuration configuration = context.getResources().getConfiguration();
        return new ContextThemeWrapper(context, androidx.appcompat.R.style.Theme_AppCompat_Empty) {
            @Override
            public void applyOverrideConfiguration(Configuration overrideConfiguration) {
                if (overrideConfiguration != null) {
                    overrideConfiguration.setTo(configuration);
                }
                super.applyOverrideConfiguration(overrideConfiguration);
            }
        };
    }

    /**
     * 获取应用内设置的语言
     *
     * @return 应用内设置的语言
     */
    public LangUtils.ChatLocale getAppLocale(Context context) {
        return LangUtils.getAppLocale(context);
    }

    /**
     * 获取系统语言
     *
     * @return 系统语言
     */
    public Locale getSystemLocale() {
        return LangUtils.getSystemLocale();
    }

    /**
     * 获取当前app 的语言设置
     *
     * @return
     */
    public LangUtils.ChatLocale getLanguageLocal(Context context) {
        LangUtils.ChatLocale appLocale = ChatConfigurationManager.getInstance().getAppLocale(context);
        if (appLocale == LangUtils.ChatLocale.LOCALE_AUTO) {
            Locale systemLocale = ChatConfigurationManager.getInstance().getSystemLocale();
            if (systemLocale.getLanguage().equals(Locale.CHINESE.getLanguage())) {
                appLocale = LangUtils.ChatLocale.LOCALE_CHINA;
            } else if (systemLocale.getLanguage().equals(Locale.ENGLISH.getLanguage())) {
                appLocale = LangUtils.ChatLocale.LOCALE_US;
            } else {
                appLocale = LangUtils.ChatLocale.LOCALE_CHINA;
            }
        }
        return appLocale;
    }
}
