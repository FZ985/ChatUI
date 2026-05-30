package io.im.uicommon;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.im.core.core.ChatSDK;
import io.im.core.core.IMClientCore;
import io.im.core.core.socket.ConnectRequest;
import io.im.core.core.socket.ErrorResult;
import io.im.core.listener.OnSocketMessageListener;
import io.im.core.model.Message;
import io.im.core.model.UserInfo;
import io.im.core.utils.JLog;
import io.im.core.utils.SystemUtil;
import io.im.core.utils.language.ChatConfigurationManager;
import io.im.uicommon.config.Options;

/**
 * author : JFZ
 * date : 2024/1/26 10:51
 * description :
 */
public class IMCenter {

    private static final Options defaultOptions = new Options();
    private static Options options;
    private final static String TAG = "IMCenter";

    private final OnSocketMessageListener _innerMessageReceiveListener = new OnSocketMessageListener() {
        @Override
        public void onMessage(@NonNull Message message, int code) {

        }

        @Override
        public void onMessageError(@Nullable Message message, ErrorResult error) {
            getOptions().getConnectService().getConnectListener().onConnected(error);
        }
    };

    private IMCenter() {

    }

    public static IMCenter getInstance() {
        return SingletonHolder.sInstance;
    }

    public static void init(Application application) {
        init(application, defaultOptions);
    }

    public static void init(Application application, Options options) {
        if (options != null) {
            IMCenter.options = options;
        } else {
            IMCenter.options = defaultOptions;
        }
        String current = SystemUtil.getProcessName(application);
        String mainProcessName = application.getPackageName();
        if (!mainProcessName.equals(current)) {
            JLog.e(TAG, "Init. Current process : " + current);
            return;
        }
        ChatSDK.init(application);
        ChatConfigurationManager.init(application);
    }

    public void connect(ConnectRequest request) {
        IMClientCore.getInstance().setOnSocketMessageListener(_innerMessageReceiveListener);
        ChatSDK.connect(request);
    }

    public static void login(UserInfo user) {
        ChatSDK.login(user);
    }

    public Options getOptions() {
        if (options == null) {
            return defaultOptions;
        }
        return options;
    }

    public static UserInfo getLoginUser() {
        return ChatSDK.getConnectUser();
    }

    public static boolean isOnLine() {
        return ChatSDK.isOnLine();
    }

    public static void setOnLineState(boolean state) {
        ChatSDK.setOnLineState(state);
    }

    public static boolean isNotRequest() {
        return ChatSDK.isNotRequest();
    }

    public static void unConnect() {
        getInstance().getOptions().getConnectService().clearAllConnectListener();
        setOnLineState(false);
        ChatSDK.unConnect();
    }

    private static class SingletonHolder {
        @SuppressLint("StaticFieldLeak")
        private static final IMCenter sInstance = new IMCenter();
    }
}
