package io.im.app;

import androidx.multidex.MultiDexApplication;

import io.im.kit.IMCenter;

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
        IMCenter.init(this);
    }
}
