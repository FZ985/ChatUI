package io.im.kit.config;

/**
 * author : JFZ
 * date : 2023/12/21 09:01
 * description : 消息类型
 */
public interface MessageType {

    int SYSTEM = 100;//系统消息

    int TEXT = 101;//文本消息

    int IMAGE = 102;//图片消息

    int VIDEO = 103;//视频消息

    int VOICE = 104;//语音消息

    int LOCATION = 105;//位置消息

    int FILE = 106;//文件消息

}
