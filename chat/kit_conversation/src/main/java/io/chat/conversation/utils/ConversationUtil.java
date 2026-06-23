package io.chat.conversation.utils;


import java.util.Comparator;

import io.chat.conversation.model.UiSession;

/**
 * by DAD FZ
 * 2026/6/23
 * desc：
 **/
public class ConversationUtil {

    public static Comparator<UiSession> getConversationComparator() {
        // 会话排序规则
        return (bean1, bean2) -> {
            int result;
            if (bean1 == null) {
                result = 1;
            } else if (bean2 == null) {
                result = -1;
            } else if (bean1.isTop() == bean2.isTop()) {
                long time = bean1.getLastMsgTime() - bean2.getLastMsgTime();
                result = time == 0L ? 0 : (time > 0 ? -1 : 1);
            } else {
                result = bean1.isTop() ? -1 : 1;
            }
            return result;
        };
    }
}
