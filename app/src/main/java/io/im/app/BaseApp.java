package io.im.app;

import androidx.multidex.MultiDexApplication;

import io.im.kit.IMCenter;
import io.im.kit.config.Options;

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
        Options options = new Options();
        IMCenter.init(this, options);
    }
}
