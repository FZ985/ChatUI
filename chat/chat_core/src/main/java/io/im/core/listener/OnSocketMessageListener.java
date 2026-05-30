package io.im.core.listener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.im.core.model.Message;
import io.im.core.core.socket.ErrorResult;


/**
 * author : JFZ
 * date : 2023/12/22 14:36
 * description :
 */
public interface OnSocketMessageListener {

    void onMessage(@NonNull Message message, int code);

    void onMessageError(@Nullable Message message, ErrorResult error);

}
