package io.im.core.core.socket;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.im.core.utils.ChatLibUtil;
import io.im.core.utils.ChatNull;
import io.im.core.utils.JLog;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;


/**
 * author : JFZ
 * date : 2023/12/21 08:47
 * description :
 */
public class ISocketClient {

    private Request request;
    private OkHttpClient client;
    private WebSocket webSocket;

    private final Handler mDelivery;

    private volatile boolean isConnect;

    private ISocketListener listener;

    private final String ping = "ping";
    private final String pong = "pong";

    private ISocketClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS);
//                .pingInterval(10, TimeUnit.SECONDS);
        client = builder.build();
        mDelivery = new Handler(Looper.getMainLooper());
    }

    private static final class SocketClientHolder {
        static final ISocketClient socketClient = new ISocketClient();

    }

    public static ISocketClient getInstance() {
        return SocketClientHolder.socketClient;
    }

    private int reconnectNum;

    private void reconnect() {
        if (request != null) {
            int RECONNECT_MAX_NUM = Integer.MAX_VALUE;
            if (reconnectNum < RECONNECT_MAX_NUM) {
                log("重连...");
                obtainHandler().removeCallbacks(pingRun);
                try {
                    int RECONNECT_MILLIS = 3000;
                    Thread.sleep(RECONNECT_MILLIS);
                    startConnect();
                    reconnectNum++;
                } catch (InterruptedException e) {
                    log("重连失败：" + e.getMessage());
                    reconnect();
                }
            } else {
                log("超出重连次数！");
            }
        } else {
            log("=====>>>> request == null");
        }
    }

    private void setWebSocket(WebSocket webSocket) {
        this.webSocket = webSocket;
    }

    public boolean isConnect() {
        return webSocket != null && isConnect;
    }

    public boolean closeConnect() {
        if (isConnect()) {
            webSocket.cancel();
            return webSocket.close(SocketCode.SOCKET_SEND_CLOSE, "webSocket is closing");
        }
        log("webSocket has closed or not connect");
        return false;
    }

    public void startConnect(ConnectRequest chatRequest, ISocketListener listener) {
        this.listener = listener;
        Request.Builder builder = new Request.Builder();
        log("socketUrl:" + chatRequest.getFinalUrl());
        builder.url(chatRequest.getFinalUrl());
        HashMap<String, String> map = chatRequest.getHeadMap();
        for (String key : map.keySet()) {
            builder.addHeader(key, ChatNull.compat(map.get(key)));
        }
        request = builder.build();
        startConnect();
    }

    private void startConnect() {
        if (isConnect()) {
            log("WebSocket has connected successfully");
            return;
        }
        log("=====startConnect");
        reconnectNum = 0;
        isConnect = false;
        if (request != null) {
            webSocket = client.newWebSocket(request, new WebSocketListener() {
                @Override
                public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                    super.onClosed(webSocket, code, reason);
                    log("onClosed===:" + reason + ",code:" + code);
                    if (ISocketClient.this.webSocket != null || isConnect) {
                        setWebSocket(null);
                        isConnect = false;
                    }
                    if (listener != null) {
                        obtainHandler().post(() -> {
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("close", "closed");
                            map.put("code", code);
                            map.put("reason", reason);
                            if (listener != null) {
                                listener.onClosed(ChatLibUtil.toJson(map));
                            }
                        });
                    }
                }

                @Override
                public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                    super.onClosing(webSocket, code, reason);
                    log("onClosing===:" + reason + ",code:" + code);
                    //closeConnect()为true表示webSocket未断连，正在断开连接，false表示已经断开连接
                    if (closeConnect()) {
                        setWebSocket(null);
                        isConnect = false;
                    }
                    if (listener != null) {
                        obtainHandler().post(() -> {
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("close", "closing");
                            map.put("code", code);
                            map.put("reason", reason);
                            if (listener != null) {
                                listener.onClosing(ChatLibUtil.toJson(map));
                            }
                        });
                    }
                }

                @Override
                public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
                    super.onFailure(webSocket, t, response);
                    if (t != null) {
                        log("onFailure===:" + t.getMessage());
                    }
                    if (response != null) {
                        log("onFailure===:" + response.message());
                    }
                    if (listener != null) {
                        obtainHandler().post(() -> {
                            if (listener != null) {
                                listener.onFailure(t);
                            }
                        });
                    }
                    boolean connect = isConnect();
                    if (connect) {
                        //其他原因比如网络异常去重连
                        log("other reason connect fail");
                        setWebSocket(null);
                        isConnect = false;
                        reconnect();
                    } else {
                        if (request != null) {
                            reconnect();
                        }
                    }
                }

                @Override
                public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                    super.onMessage(webSocket, text);
                    log("onMessage===String:" + text);
                    isConnect = true;
                    String content = ChatNull.compat(text);
                    if (listener != null) {
                        obtainHandler().post(() -> {
                            if (listener != null) {
                                if (!content.equals(pong)) {
                                    listener.onMessage(text);
                                } else {
                                    listener.onPingSuccess();
                                }
                            }
                        });
                    }
                }

                @Override
                public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
                    super.onMessage(webSocket, bytes);
                    isConnect = true;
//                log("onMessage===ByteString:" + bytes);
                    if (listener != null) {
                        obtainHandler().post(() -> {
                            if (listener != null) {
                                listener.onMessage(bytes);
                            }
                        });
                    }
                }

                @Override
                public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                    super.onOpen(webSocket, response);
                    log("onOpen===code:" + response.code() + "," + response.toString());
                    setWebSocket(webSocket);
                    if (response.code() == 101) {
                        webSocket.send(ping);
                        postPing();
                    }
                    if (listener != null) {
                        obtainHandler().post(() -> {
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("code", response.code());
                            map.put("message", response.message());
                            if (listener != null) {
                                listener.onOpen(ChatLibUtil.toJson(map));
                            }
                        });
                    }
                }
            });
        }
    }

    private final Runnable pingRun = () -> {
        sendMessage(ping);
        log("发送心跳包.....");
        postPing();
        if (!isConnect()) {
            log("未连接网络等，无法发送ping指令，开始重连。。。");
            reconnect();
        }
    };

    public boolean sendPing() {
        return sendMessage(ping);
    }

    private void postPing() {
        postPing(60 * 1000);
    }

    private void postPing(long duration) {
        obtainHandler().removeCallbacks(pingRun);
        obtainHandler().postDelayed(pingRun, duration);
    }

    public boolean sendMessage(String text) {
        if (!isConnect()) {
            log("webSocket is not connected");
            return false;
        }
        return webSocket.send(text);
    }

    public boolean sendMessage(ByteString text) {
        if (!isConnect()) {
            log("webSocket is not connected");
            return false;
        }
        return webSocket.send(text);
    }

    public Handler obtainHandler() {
        return mDelivery;
    }

    public void destroy() {
        //closeConnect()为true表示webSocket未断连，正在断开连接，false表示已经断开连接
        if (closeConnect()) {
            setWebSocket(null);
            log("webSocket: " + this.webSocket + ", isConnect: " + isConnect + ", webSocket closed successfully");
        }
        isConnect = false;
        request = null;
        listener = null;
        obtainHandler().removeCallbacks(pingRun);
        log("webSocket destroy");
    }

    private void log(String m) {
        JLog.e("OkSocket", m);
    }
}
