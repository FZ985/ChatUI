package io.im.lib;

import java.util.HashMap;
import java.util.Map;

/**
 * author : JFZ
 * date : 2023/12/21 09:01
 * description : 消息类型
 */
public class MessageType {

    public static final int UNKNOWN = -1;//未知

    private static final Map<Integer, Integer> chatTypeMap = new HashMap<>();
    private static final Map<Integer, Integer> appTypeMap = new HashMap<>();


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
    public static final int APP_SYSTEM = 601;//应用系统通知


    static {
        chatTypeMap.put(CHAT_SYSTEM, CHAT_SYSTEM);
        chatTypeMap.put(CHAT_TEXT, CHAT_TEXT);
        chatTypeMap.put(CHAT_IMAGE, CHAT_IMAGE);
        chatTypeMap.put(CHAT_GIF, CHAT_GIF);
        chatTypeMap.put(CHAT_VIDEO, CHAT_VIDEO);
        chatTypeMap.put(CHAT_VOICE, CHAT_VOICE);
        chatTypeMap.put(CHAT_LOCATION, CHAT_LOCATION);
        chatTypeMap.put(CHAT_FILE, CHAT_FILE);

        appTypeMap.put(APP_SYSTEM, APP_SYSTEM);
    }


    public static void addChatType(int type) {
        if (!chatTypeMap.containsKey(type)) {
            chatTypeMap.put(type, type);
        }
    }

    public static void addAppType(int type) {
        if (!appTypeMap.containsKey(type)) {
            appTypeMap.put(type, type);
        }
    }


    public static boolean isAppType(int type) {
        return appTypeMap.containsKey(type);
    }

}
