package io.im.lib.core;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.text.TextUtils;

import io.im.lib.core.socket.ConnectRequest;
import io.im.lib.core.socket.ErrorResult;
import io.im.lib.core.socket.SocketCode;
import io.im.lib.database.DbManager;
import io.im.lib.utils.ChatNetworkUtil;
import io.im.lib.utils.JLog;

/**
 * author : JFZ
 * date : 2023/12/21 16:56
 * description : 对外公开初始化
 */
public class ChatSDK {

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    private static DbManager dbManager;

    private static boolean isBackground = false;

    public static boolean isInitialized() {
        return mContext != null && !TextUtils.isEmpty(getConnectUser()) && isOnLine();
    }

    public static Context getContext() {
        return mContext.getApplicationContext();
    }

    public static void init(Application application) {
        mContext = application;
        dbManager = new DbManager(application);
        connService(application);
        initNetworkState(application);
    }

    private static void connService(Application application) {
        CoreSingle.getInstance().bindService(application, isConnect -> {
            if (!isConnect) {
                //断开重连
                connService(application);
            }
        });
        IMBackgroundAppManager backgroundAppManager = IMBackgroundAppManager.init(application);
        backgroundAppManager.addListener(new IMBackgroundAppManager.Listener() {
            @Override
            public void onBecameForeground(Activity activity) {
                //app 切换到前台
                boolean onLine = isOnLine();
                JLog.e("#####切换到前台======onLine:" + onLine);
                if (onLine && isBackground) {
                    CoreSingle.getInstance().onBecameForeground(application);
                }
                isBackground = false;
            }

            @Override
            public void onBecameBackground(Activity activity) {
                JLog.e("#####切换到后台======");
                isBackground = true;
            }
        });
    }

    private static BroadcastReceiver receiver;

    private static void initNetworkState(Application application) {
        if (receiver == null) {
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent != null) {
                        String action = intent.getAction();
                        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                            boolean connection = ChatNetworkUtil.isConnection(context);
                            ErrorResult result = new ErrorResult(connection ? SocketCode.NETWORK_SUCCESS : SocketCode.NETWORK_ERROR,
                                    connection ? "网络连接成功" : "当前无法连接网络，可检查网络设置是否正常。");
                            IMClientCore.getInstance().sendError(result);
                            if (connection) {
                                CoreSingle.getInstance().onNetWorkSuccess(application);
                            }
                        }
                    }
                }
            };
        }
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        application.registerReceiver(receiver, mFilter); // 网络监听必须调用系统的注册
    }


    public static void connect(ConnectRequest request, String userInfo) {
        setOnLineState(true);
        saveConnectUser(userInfo);
        CoreSingle.getInstance().connectWebsocket(request);
    }

    public static DbManager getDbManager() {
        return dbManager;
    }

    public static void saveConnectUser(String userInfo) {
        if (userInfo != null) {
            SharedPreferences sp = getContext().getSharedPreferences("im_user", Context.MODE_PRIVATE);
            sp.edit().putString("user", userInfo).apply();
        }
    }

    public static String getConnectUser() {
        SharedPreferences sp = getContext().getSharedPreferences("im_user", Context.MODE_PRIVATE);
        return sp.getString("user", "");
    }

    public static boolean isOnLine() {
        SharedPreferences sp = getContext().getSharedPreferences("im_user", Context.MODE_PRIVATE);
        return sp.getBoolean("online", false);
    }

    public static void setOnLineState(boolean state) {
        SharedPreferences sp = getContext().getSharedPreferences("im_user", Context.MODE_PRIVATE);
        sp.edit().putBoolean("online", state).apply();
    }

    public static boolean isNotRequest() {
        return CoreSingle.getInstance().isNotRequest();
    }

    public static void unConnect() {
        SharedPreferences sp = getContext().getSharedPreferences("im_user", Context.MODE_PRIVATE);
        sp.edit().clear().commit();
        IMClientCore.getInstance().closeSocket();
    }
}
