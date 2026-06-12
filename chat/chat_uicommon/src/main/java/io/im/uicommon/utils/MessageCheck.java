package io.im.uicommon.utils;


import io.im.core.model.Message;
import io.im.core.utils.ServeTime;

/**
 * by DAD FZ
 * 2026/6/11
 * desc：
 **/
public class MessageCheck {

    public static boolean checkRevokeMessage(Message message, long revokeTime) {
        long canRevokeTime = ServeTime.currentTimeMillis() - message.getMessageTime();
        return canRevokeTime <= revokeTime;
    }
}
