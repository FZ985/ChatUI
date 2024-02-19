package io.im.kit.config;

/**
 * author : JFZ
 * date : 2024/1/26 11:13
 * description :
 */
public final class Chat {

    private static final ConversationConfig conversationConfig = new ConversationConfig();

    public static ConversationConfig getConversationConfig() {
        return conversationConfig;
    }

    private static final ChatEmojiConfig emojiConfig = new ChatEmojiConfig();

    public static ChatEmojiConfig getEmojiConfig() {
        return emojiConfig;
    }

}
