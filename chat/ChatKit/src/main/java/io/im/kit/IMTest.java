package io.im.kit;

import android.util.Size;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.im.kit.model.UiMessage;
import io.im.lib.message.ImageMessage;
import io.im.lib.message.TextMessage;
import io.im.lib.message.UnKnowMessage;
import io.im.lib.model.Message;
import io.im.lib.model.MessageContent;
import io.im.lib.model.UserInfo;

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
        list.add(new UiMessage(getReceiveMsg(TextMessage.obtain("234"))));
        list.add(new UiMessage(getReceiveMsg(TextMessage.obtain("234234234234234234234234234234234234234234234234234234"))));
        list.add(new UiMessage(getSendMsg(TextMessage.obtain("123123123123123")).setReadStatus(Message.ReadStatus.READ)));
        list.add(new UiMessage(getSendMsg(TextMessage.obtain("123"))));
        list.add(new UiMessage(getSendMsg(TextMessage.obtain("234"))));
        list.add(new UiMessage(getSendMsg(TextMessage.obtain("234"))));
        list.add(new UiMessage(getSendMsg(TextMessage.obtain("234"))));
        list.add(new UiMessage(new Message(new UnKnowMessage())));
        list.add(new UiMessage(getSendMsg(ImageMessage.obtain("", "", new Size(800, 500)))));
        list.add(new UiMessage(getSendMsg(ImageMessage.obtain("https://img1.baidu.com/it/u=2145774639,3196122421&fm=253&fmt=auto&app=138&f=JPEG?w=800&h=500", ""))));
        list.add(new UiMessage(getSendMsg(ImageMessage.obtain("https://img1.baidu.com/it/u=2457208575,2459586361&fm=253&fmt=auto&app=138&f=JPEG?w=313&h=500", "", new Size(313, 500)))));
        return list;
    }

    public static UiMessage randomMessage() {
        List<UiMessage> list = message();
        Random random = new Random();
        return list.get(random.nextInt(list.size()));
    }

    private static Message getSendMsg(MessageContent content) {
        Message msg = new Message(content);
        msg.setMessageId(msg.buildMessageId());
        msg.setMessageDirection(Message.MessageDirection.SEND);
        msg.setSendStatus(Message.SentStatus.SEND_SUCCESS.getValue());
        msg.setFromUser(loginUser.toMessageUser());
        msg.setToUser(toUser.toMessageUser());
        msg.setMessageTime(System.currentTimeMillis() - (new Random().nextInt(100) + 10) * 60 * 1000);
        return msg;
    }

    private static Message getReceiveMsg(MessageContent content) {
        Message msg = new Message(content);
        msg.setMessageId(msg.buildMessageId());
        msg.setMessageDirection(Message.MessageDirection.RECEIVE);
        msg.setMessageTime(System.currentTimeMillis() - (new Random().nextInt(100) + 10) * 60 * 1000);
        msg.setFromUser(toUser.toMessageUser());
        msg.setToUser(loginUser.toMessageUser());
        return msg;
    }
}
