package io.im.core.core.init;


import android.app.Application;

/**
 * by DAD FZ
 * 2026/5/30
 * desc：
 **/
public interface IKitInitializer extends Comparable<IKitInitializer> {

    void init(Application context);

    default int priority() {
        return 0;
    }

    @Override
    default int compareTo(IKitInitializer o) {
        return o.priority() - this.priority();
    }
}
