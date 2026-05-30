package io.chat.kit.config;


import io.chat.kit.listener.IMessageViewModelProcessor;
import io.chat.kit.ui.popmenu.IChatPopMenu;
import io.chat.kit.ui.popmenu.IChatPopMenuClickListener;

/**
 * by DAD FZ
 * 2026/5/30
 * desc：
 **/
public final class ChatOptions {

    //会话配置
    private static final ChatConfig chatConfig = new ChatConfig();

    //表情配置
    private static final ChatEmojiConfig emojiConfig = new ChatEmojiConfig();

    //插件配置
    private static final ChatPluginConfig pluginConfig = new ChatPluginConfig();

    //会话消息配置
    public ChatConfig getChatConfig() {
        return chatConfig;
    }

    //聊天表情面板配置---------------------------------------------------------------------
    public ChatEmojiConfig getEmojiConfig() {
        return emojiConfig;
    }


    //聊天插件面板配置---------------------------------------------------------------------
    public ChatPluginConfig getPluginConfig() {
        return pluginConfig;
    }

    //聊天列表消息点击处理---------------------------------------------------------------------------
    private IMessageViewModelProcessor viewModelProcessor;

    public IMessageViewModelProcessor getViewModelProcessor() {
        return viewModelProcessor;
    }

    public void setViewModelProcessor(IMessageViewModelProcessor viewModelProcessor) {
        this.viewModelProcessor = viewModelProcessor;
    }

    //长按消息弹窗事件---------------------------------------------------------------------------
    public IChatPopMenu chatPopMenu;
    //消息长按菜单时间定制
    public IChatPopMenuClickListener popMenuClickListener;

}
