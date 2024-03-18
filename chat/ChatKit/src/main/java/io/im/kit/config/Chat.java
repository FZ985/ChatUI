package io.im.kit.config;

/**
 * author : JFZ
 * date : 2024/1/26 11:13
 * description :
 */
public final class Chat {

    //会话配置
    private static final ConversationConfig conversationConfig = new ConversationConfig();

    public static ConversationConfig getConversationConfig() {
        return conversationConfig;
    }


    //表情配置
    private static final ChatEmojiConfig emojiConfig = new ChatEmojiConfig();

    public static ChatEmojiConfig getEmojiConfig() {
        return emojiConfig;
    }


    //插件配置
    private static final ChatPluginConfig pluginConfig = new ChatPluginConfig();

    public static ChatPluginConfig getPluginConfig() {
        return pluginConfig;
    }


}
