package io.im.lib.utils.language;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;

import java.util.Locale;


public class LangUtils {
    private static final String LOCALE_CONF_FILE_NAME = "locale.config";
    private static final String APP_LOCALE = "app_locale";
    private static Locale systemLocale = Locale.getDefault();

    public static Context getConfigurationContext(Context context) {
        Resources resources = context.getResources();
        Configuration config = new Configuration(resources.getConfiguration());
        Context configurationContext = context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList localeList = new LocaleList(getAppLocale(context).toLocale());
            LocaleList.setDefault(localeList);
            config.setLocales(localeList);
            configurationContext = context.createConfigurationContext(config);
        }
        return configurationContext;
    }

    public static ChatLocale getAppLocale(Context context) {
        SharedPreferences sp = context.getSharedPreferences(LOCALE_CONF_FILE_NAME, Context.MODE_PRIVATE);
        String locale = sp.getString(APP_LOCALE, "auto");
        return ChatLocale.valueOf(locale);
    }

    public static void saveLocale(Context context, ChatLocale locale) {
        SharedPreferences sp =
                context.getSharedPreferences(LOCALE_CONF_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(APP_LOCALE, locale.value()).commit();
    }

    /**
     * 可选择的语言的包装类
     */
    public static class ChatLocale {
        /**
         * 中文
         */
        public static final ChatLocale LOCALE_CHINA = new ChatLocale("zh");
        /**
         * 英文
         */
        public static final ChatLocale LOCALE_US = new ChatLocale("en");

        /**
         * 跟随系统
         */
        public static final ChatLocale LOCALE_AUTO = new ChatLocale("auto");

        private String ChatLocale;

        private ChatLocale(String ChatLocale) {
            this.ChatLocale = ChatLocale;
        }

        public String value() {
            return ChatLocale;
        }

        public Locale toLocale() {
            Locale locale;
            if (ChatLocale.equals(LOCALE_CHINA.value())) {
                locale = Locale.CHINESE;
            } else if (ChatLocale.equals(LOCALE_US.value())) {
                locale = Locale.ENGLISH;
            } else {
                locale = getSystemLocale();
            }
            return locale;
        }

        public static ChatLocale valueOf(String ChatLocale) {
            ChatLocale locale;
            if (LOCALE_CHINA.value().equals(ChatLocale)) {
                locale = LOCALE_CHINA;
            } else if (LOCALE_US.value().equals(ChatLocale)) {
                locale = LOCALE_US;
            } else {
                locale = LOCALE_AUTO;
            }
            return locale;
        }
    }

    /**
     * 获取系统语言
     *
     * @return 系统语言
     */
    public static Locale getSystemLocale() {
        return systemLocale;
    }

    /**
     * 设置系统语言
     *
     * @param locale 设置的系统语音
     */
    public static void setSystemLocale(Locale locale) {
        systemLocale = locale;
    }

    /**
     * 获取当前语言,不管有没有设置或跟随系统
     *
     * @param context 上下文
     * @return 当前语言
     */
    public static ChatLocale getCurrentLanguage(Context context) {
        SharedPreferences sp = context.getSharedPreferences(LOCALE_CONF_FILE_NAME, Context.MODE_PRIVATE);
        String locale = sp.getString(APP_LOCALE, "auto");
        if (("auto").equals(locale)) {
            return getSystemLocale().toString().equals("zh_CN")
                    ? ChatLocale.LOCALE_CHINA
                    : ChatLocale.LOCALE_US;
        }
        return ChatLocale.valueOf(locale);
    }

    /**
     * 获取当前app 的语言设置
     */
    public static ChatLocale getAppLanguageLocal(Context context) {
        ChatLocale appLocale = getAppLocale(context);
        if (appLocale == ChatLocale.LOCALE_AUTO) {
            Locale systemLocale = ChatConfigurationManager.getInstance().getSystemLocale();
            if (systemLocale.getLanguage().equals(Locale.CHINESE.getLanguage())) {
                appLocale = ChatLocale.LOCALE_CHINA;
            } else if (systemLocale.getLanguage().equals(Locale.ENGLISH.getLanguage())) {
                appLocale = ChatLocale.LOCALE_US;
            } else {
                appLocale = ChatLocale.LOCALE_CHINA;
            }
        }
        return appLocale;
    }
}
