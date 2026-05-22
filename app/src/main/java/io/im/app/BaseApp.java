package io.im.app;

import androidx.multidex.MultiDexApplication;

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
    }
}
