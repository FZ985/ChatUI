package io.chat.kit;

import android.util.Size;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.chat.kit.model.UiMessage;
import io.im.core.MessageType;
import io.im.core.message.im.ImageMessage;
import io.im.core.message.im.TextMessage;
import io.im.core.message.UnKnowMessage;
import io.im.core.model.ConversationType;
import io.im.core.model.Message;
import io.im.core.model.MessageContent;
import io.im.core.model.UserInfo;

/**
 * author : JFZ
 * date : 2024/2/19 10:30
 * description :
 */
public class IMTest {

    public static UserInfo loginUser = new UserInfo("1", "会飞的牛肉干", "");

    public static UserInfo toUser = new UserInfo("123", "哈哈哈", "");

    public static List<UiMessage> message() {
        List<UiMessage> list = new ArrayList<>();
        list.add(new UiMessage(getSendMsg(MessageType.CHAT_TEXT, TextMessage.obtain("234"))));
        list.add(new UiMessage(getSendMsg(MessageType.CHAT_TEXT, TextMessage.obtain("234234234234234234234234234234234234234234234234234234"))));
        list.add(new UiMessage(getSendMsg(MessageType.CHAT_TEXT, TextMessage.obtain("https://www.baidu.com"))));
        list.add(new UiMessage(getSendMsg(MessageType.CHAT_TEXT, TextMessage.obtain("123123123123123")).setReadStatusEnum(Message.ReadStatus.READ)));
        list.add(new UiMessage(getSendMsg(MessageType.CHAT_TEXT, TextMessage.obtain("123"))));
        list.add(new UiMessage(getSendMsg(MessageType.CHAT_TEXT, TextMessage.obtain("234"))));
        list.add(new UiMessage(getSendMsg(MessageType.CHAT_TEXT, TextMessage.obtain("234"))));
        list.add(new UiMessage(getSendMsg(MessageType.CHAT_TEXT, TextMessage.obtain("234"))));
        list.add(new UiMessage(new Message(new UnKnowMessage())));
        list.add(new UiMessage(getSendMsg(MessageType.CHAT_IMAGE, ImageMessage.obtain("", "", new Size(800, 500)))));
        list.add(new UiMessage(getSendMsg(MessageType.CHAT_IMAGE, ImageMessage.obtain("https://img1.baidu.com/it/u=2145774639,3196122421&fm=253&fmt=auto&app=138&f=JPEG?w=800&h=500", ""))));
        list.add(new UiMessage(getSendMsg(MessageType.CHAT_IMAGE, ImageMessage.obtain("https://img1.baidu.com/it/u=2457208575,2459586361&fm=253&fmt=auto&app=138&f=JPEG?w=313&h=500", "", new Size(313, 500)))));
        return list;
    }

    public static UiMessage randomMessage() {
        List<UiMessage> list = message();
        Random random = new Random();
        return list.get(random.nextInt(list.size()));
    }

    private static Message getSendMsg(int msgType, MessageContent content) {
        return Message.obtain(toUser, ConversationType.PRIVATE, msgType, content);
    }

    private static Message getReceiveMsg(int msgType, MessageContent content) {
        return Message.obtain(loginUser, ConversationType.PRIVATE, msgType, content);
    }
}
