package io.im.lib.helper;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.im.lib.model.Message;

/**
 * 消息内存缓存 主要用于多选功能，将选中的信息保存到这里，在选中状态维护、合并转发、删除等 操作后，清空缓存
 */
public class ChatMsgCache {
    private static final Map<Long, Message> msgMap = new HashMap<>();

    public static void addMessage(Message message) {
        msgMap.put(message.getMessageId(), message);
    }

    public static void removeMessage(Long uuid) {
        msgMap.remove(uuid);
    }

    /**
     * 根据clientId移除消息
     *
     * @param clientIds 消息clientId
     */
    public static void removeMessagesByClientId(List<Long> clientIds) {
        if (clientIds == null) {
            return;
        }
        for (Long clientId : clientIds) {
            msgMap.remove(clientId);
        }
    }

    public static void removeMessages(List<Message> messages) {
        if (messages == null) {
            return;
        }
        for (Message message : messages) {
            msgMap.remove(message.getMessageId());
        }
    }

    public static boolean contains(Long uuid) {
        return msgMap.containsKey(uuid);
    }

    public static void clear() {
        msgMap.clear();
    }

    public static List<Message> getMessageList() {
        List<Message> list = new ArrayList<>(msgMap.values());
        Collections.sort(
                list,
                (o1, o2) -> {
                    if (o1 == null || o2 == null) {
                        return 0;
                    }
                    return (int) (o1.getMessageTime() - o2.getMessageTime());
                });
        return list;
    }

    public static int getMessageCount() {
        return msgMap.size();
    }

}
