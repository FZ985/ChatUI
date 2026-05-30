package io.im.core.core.socket;

import okio.ByteString;

/**
 * author : JFZ
 * date : 2023/12/21 14:28
 * description :
 */
public interface ISocketListener {

    void onMessage(String text);

    void onMessage(ByteString bytes);

    void onOpen(String msg);

    void onClosed(String msg);

    void onClosing(String msg);

    void onFailure(Throwable t);

    void onPingSuccess();

}
