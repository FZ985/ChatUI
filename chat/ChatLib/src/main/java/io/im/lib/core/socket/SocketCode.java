package io.im.lib.core.socket;


/**
 * by DAD FZ
 * 2026/5/21
 * desc：
 **/
public interface SocketCode {

    int success = 200;

    int SIGN_OUT = 409;//单点登录

    int SOCKET_SEND_CLOSE = 1000;//主动关闭socket


    //Android 本地自定义code,为了和服务端区分开，code定义为负数
    int NOT_INIT = -10000;//未初始化

    int NETWORK_ERROR = -10001;//网络断开

    int NETWORK_SUCCESS = -10002;//网络连接

    int SOCKET_ERROR = -10003;//socket 错误回调

    int SOCKET_CLOSING = -10004;//socket 正在关闭

    int SOCKET_CLOSED = -10005;//socket 已关闭

    int JSON_INVALID_DATA = -10006;//json 解析失败

    int SOCKET_SEND_ERROR = -10007;//发送socket 数据失败

    int SOCKET_PING_SUCCESS = -10009;//主动ping 成功

    int SOCKET_MESSAGE = 0x10006;

    int SOCKET_OPEN = 0x10007;
}
