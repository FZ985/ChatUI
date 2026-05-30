package io.im.core.core.init;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * by DAD FZ
 * 2026/5/30
 * desc：
 **/
public final class InitializerManager {

    private static volatile InitializerManager instance;

    private static final Object lock = new Object();

    private final LinkedHashMap<String, IKitInitializer> initMaps = new LinkedHashMap<>();

    private InitializerManager() {
    }


    public static InitializerManager getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new InitializerManager();
                }
            }
        }
        return instance;
    }

    public void registerInitializer(String key, IKitInitializer initializer) {
        initMaps.put(key, initializer);
    }

    public List<IKitInitializer> getInitializers() {
        return new ArrayList<>(initMaps.values());
    }

}
