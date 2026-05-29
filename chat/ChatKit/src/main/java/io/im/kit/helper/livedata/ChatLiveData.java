package io.im.kit.helper.livedata;


import androidx.lifecycle.MediatorLiveData;

import io.im.kit.model.ForwardSelectorBean;

/**
 * by DAD FZ
 * 2026/5/29
 * desc：
 **/
public class ChatLiveData {


    private static ChatLiveData instance = null;

    public MediatorLiveData<ForwardSelectorBean> forwardLive = new MediatorLiveData<>();

    private ChatLiveData() {
    }

    public static ChatLiveData getInstance() {
        if (instance == null) {
            synchronized (ChatLiveData.class) {
                if (instance == null) {
                    instance = new ChatLiveData();
                }
            }
        }
        return instance;
    }


}
