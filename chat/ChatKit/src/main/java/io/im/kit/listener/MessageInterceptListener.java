package io.im.kit.listener;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.im.lib.model.Message;

/**
 * author : JFZ
 * date : 2024/1/8 18:52
 * description :消息拦截器监听
 */
public interface MessageInterceptListener {

    //发送消息前的拦截，false:不拦截，true：拦截；
    boolean onSendInterceptMessage(@NonNull Message message);

    //接收消息拦截，false:不拦截，true：拦截；此拦截包含所有拦截，包括发送成功的消息
    boolean onReceiveInterceptMessage(@Nullable Message message, int code);

}
