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
        long currentTime = ServeTime.currentTimeMillis();
        long canRevokeTime = currentTime - message.getMessageTime();
        return canRevokeTime <= revokeTime;
    }


    //剩余的撤回消息时长
    public static long lastRevokeTime(Message message, long revokeTime) {
        return revokeTime - (ServeTime.currentTimeMillis() - message.getMessageTime());
    }
}
