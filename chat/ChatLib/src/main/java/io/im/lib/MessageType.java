package io.im.lib;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import io.im.lib.message.UnKnowMessage;
import io.im.lib.message.app.AppSystemNotificationMessage;
import io.im.lib.message.im.ImageMessage;
import io.im.lib.message.im.TextMessage;
import io.im.lib.model.MessageContent;

/**
 * author : JFZ
 * date : 2023/12/21 09:01
 * description : 消息类型
 */
public class MessageType {

    public static final int UNKNOWN = -1;//未知

    private static final Map<Integer, MessageContent> chatTypeMap = new HashMap<>();
    private static final Map<Integer, MessageContent> appTypeMap = new HashMap<>();


    //聊天消息-------------------1～600个内置聊天类型
    public static final int CHAT_SYSTEM = 1;//聊天系统消息

    public static final int CHAT_TEXT = 2;//聊天文本消息

    public static final int CHAT_IMAGE = 3;//聊天图片消息

    public static final int CHAT_GIF = 4;//聊天图片gif消息

    public static final int CHAT_VIDEO = 5;//聊天视频消息

    public static final int CHAT_VOICE = 6;//聊天语音消息

    public static final int CHAT_LOCATION = 7;//聊天位置消息

    public static final int CHAT_FILE = 8;//聊天文件消息


    //app消息，全局通知类
    public static final int APP_SYSTEM_NOTIFICATION = 601;//应用系统通知


    static {
        chatTypeMap.put(CHAT_TEXT, new TextMessage());
        chatTypeMap.put(CHAT_IMAGE, new ImageMessage());

        appTypeMap.put(APP_SYSTEM_NOTIFICATION, new AppSystemNotificationMessage());
    }


    public static void addChatType(int type, Class<? extends MessageContent> messageClass) {
        if (!chatTypeMap.containsKey(type) && messageClass != null) {
            try {
                Constructor<?> constructor = messageClass.getConstructor();
                Object obj = constructor.newInstance();
                chatTypeMap.put(type, (MessageContent) obj);
            } catch (Exception e) {
                //
            }
        }
    }

    public static MessageContent getMessageContent(int messageType) {
        if (chatTypeMap.containsKey(messageType)) {
            MessageContent messageContent = chatTypeMap.get(messageType);
            if (messageContent != null) {
                return messageContent.copy();
            }
        }
        return UnKnowMessage.obtain();
    }

    public static void addAppMessageType(int type, Class<? extends MessageContent> messageClass) {
        if (!appTypeMap.containsKey(type) && messageClass != null) {
            try {
                Constructor<?> constructor = messageClass.getConstructor();
                Object obj = constructor.newInstance();
                appTypeMap.put(type, (MessageContent) obj);
            } catch (Exception e) {
                //
            }
        }
    }

    public static MessageContent getAppMessageContent(int messageType) {
        if (appTypeMap.containsKey(messageType)) {
            MessageContent messageContent = appTypeMap.get(messageType);
            if (messageContent != null) {
                return messageContent.copy();
            }
        }
        return UnKnowMessage.obtain();
    }


    public static boolean isAppType(int type) {
        return appTypeMap.containsKey(type);
    }

}
