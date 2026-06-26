package io.im.core.core;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.text.TextUtils;

import androidx.annotation.Keep;

import org.json.JSONObject;

import io.im.core.core.aidl.CoreInterface;
import io.im.core.core.aidl.CoreResultBind;
import io.im.core.core.aidl.CoreResultInterface;
import io.im.core.core.service.CoreService;
import io.im.core.core.socket.ConnectRequest;
import io.im.core.core.socket.SocketCode;
import io.im.core.core.socket.WebSocketResult;
import io.im.core.model.Message;
import io.im.core.utils.ChatLibUtil;
import io.im.core.utils.ChatNetworkUtil;
import io.im.core.utils.JLog;


/**
 * 连接socket服务，操作socket服务
 */
@Keep
public class CoreSingle {

    // 单例
    private static CoreSingle rsSingle = null;

    // 服务绑定状态
    private boolean isBindService = false;

    private CoreInterface bind = null;

    private ServiceConnectedListener listener;

    static ConnectRequest request;

    interface ServiceConnectedListener {
        void onServiceConnected(boolean isConnect);
    }

    private final CoreResultInterface callback = new CoreResultBind() {
        @Override
        public void onResult(int type, String data) throws RemoteException {
            if (type == CoreConstant.SocketResponse) {
                WebSocketResult result = ChatLibUtil.gson.fromJson(data, WebSocketResult.class);
                IMClientCore.getInstance().getHandlerSocketResponse().apply(result);
            }
        }
    };

    // 单例
    public static CoreSingle getInstance() {
        if (rsSingle == null) {
            synchronized (CoreSingle.class) {
                if (rsSingle == null) rsSingle = new CoreSingle();
            }
        }
        return rsSingle;
    }

    // 服务链接
    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            JLog.e("=====onServiceConnected====");
            bind = CoreInterface.Stub.asInterface(iBinder);
            isBindService = true;
            listener.onServiceConnected(true);
            if (request != null) {
                connectWebsocket(request);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            JLog.e("=====onServiceDisconnected====");
            if (bind != null) bind = null;
            isBindService = false;
            if (listener != null) {
                listener.onServiceConnected(false);
            }
        }
    };

    // 绑定服务
    protected void bindService(Context var, ServiceConnectedListener listener) {
        JLog.e("===isBindService:" + isBindService);
        this.listener = listener;
        startBind(var);
    }

    private void startBind(Context var) {
        if (!isBindService) {
            JLog.e("bind CoreService");
            Intent bindIntend = new Intent(var, CoreService.class);
            var.bindService(bindIntend, connection, Context.BIND_AUTO_CREATE);
        }
    }

    // 服务解绑
    protected void unBindService(Context var) {
        if (isBindService) {
            var.unbindService(connection);
            isBindService = false;
        }
        request = null;
    }

    // 服务绑定状态
    public boolean bindState() {
        return isBindService && bind != null;
    }

    public synchronized CoreInterface getBind() {
        return bind;
    }

    protected void connectWebsocket(ConnectRequest request) {
        CoreSingle.request = request;
        if (bindState()) {
            try {
                getBind().toTypeAction(CoreConstant.Connect, ChatLibUtil.toJson(request), callback);
            } catch (RemoteException e) {
                JLog.e("连接 exception:" + e.getMessage());
                connectWebsocket(request);
            }
        }
    }

    private final Handler testHandler = new Handler(Looper.getMainLooper());

    public void sendMessage(Message message) {
//        if (bindState()) {
//            try {
//                getBind().toTypeAction(CoreConstant.SendMessage, ChatLibUtil.toJson(message), callback);
//            } catch (RemoteException e) {
//                JLog.e("连接 exception:" + e.getMessage());
//                connectWebsocket(request);
//            }
//        }

        //模拟发送成功
        try {
            String msgJson = message.toJson();
            JSONObject jo = new JSONObject(msgJson);
            if (ChatNetworkUtil.isConnection(ChatSDK.getContext())) {
                jo.put("code", SocketCode.success);
            } else {
                jo.put("code", SocketCode.NETWORK_ERROR);
            }
            String data = new WebSocketResult(SocketCode.SOCKET_MESSAGE, jo.toString()).toJson();
            callback.onResult(CoreConstant.SocketResponse, data);

            testHandler.postDelayed(() -> {
                try {
                    Message flipMessage = message.flipFromTo();
                    flipMessage.setReferMessage("");
                    flipMessage.setMessageId(flipMessage.buildMessageId());
                    String receiveJson = flipMessage.toJson();
                    JSONObject receiveObj = new JSONObject(receiveJson);
                    receiveObj.put("code", SocketCode.success);
                    String receiveData = new WebSocketResult(SocketCode.SOCKET_MESSAGE, receiveObj.toString()).toJson();
                    callback.onResult(CoreConstant.SocketResponse, receiveData);
                } catch (Exception e) {
                    //
                }
            }, 300);
        } catch (Exception e) {
            //
        }

    }

    public void closeSocket() {
        if (bindState()) {
            try {
                getBind().toTypeAction(CoreConstant.CloseSocket, "", callback);
            } catch (RemoteException e) {
            }
        }
        if (request != null) {
            request.release();
        }
        request = null;
    }

    public boolean isNotRequest() {
        return request == null || TextUtils.isEmpty(request.getUrl());
    }

    public void onBecameForeground(Context context) {
        checkSocket(context);
    }

    public void onNetWorkSuccess(Context context) {
        checkSocket(context);
    }

    private void checkSocket(Context context) {
        if (bindState()) {
            try {
                String success = getBind().getTypeResult(CoreConstant.CheckSocket, callback);
                if (success.equals("false")) {
                    JLog.e("======未连接socket");
                    if (request != null) {
                        connectWebsocket(request);
                    }
                } else {
                    JLog.e("====socket 连接正常");
                }
            } catch (RemoteException e) {
            }
        } else {
            //重新绑定
            startBind(context);
        }
    }
}
