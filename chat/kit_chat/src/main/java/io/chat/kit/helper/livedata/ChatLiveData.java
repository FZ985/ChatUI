package io.chat.kit.helper.livedata;


import androidx.lifecycle.MediatorLiveData;

import io.chat.kit.model.ForwardSelectorBean;

/**
 * by DAD FZ
 * 2026/5/29
 * desc：
 **/
public class ChatLiveData {


    public MediatorLiveData<ForwardSelectorBean> forwardLive = new MediatorLiveData<>();

    private ChatLiveData() {
    }

    private static final class InstanceHolder {
        private static final ChatLiveData instance = new ChatLiveData();
    }

    public static ChatLiveData getInstance() {
        return InstanceHolder.instance;
    }


}
