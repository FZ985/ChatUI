package io.im.kit.config;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import io.im.kit.config.enums.FontSize;
import io.im.kit.listener.IMessageViewModelProcessor;
import io.im.kit.listener.ImageLoader;
import io.im.kit.listener.MessageEventListener;
import io.im.kit.listener.MessageInterceptListener;
import io.im.kit.ui.popmenu.IChatPopMenu;
import io.im.kit.ui.popmenu.IChatPopMenuClickListener;

/**
 * author : JFZ
 * date : 2024/1/26 17:24
 * description :
 */
public final class Options {

    private static final ConnectService connectService = new ConnectService();

    public Options() {

    }

    //全局连接服务
    public ConnectService getConnectService() {
        return connectService;
    }

    //会话消息配置
    public ChatConfig getChatConfig() {
        return Chat.getChatConfig();
    }


    //图片加载---------------------------------------------------------------------------
    private final ImageLoader defaultLoader = new DefaultImageLoader();
    private ImageLoader imageLoader;

    public ImageLoader getImageLoader() {
        if (imageLoader == null) {
            return defaultLoader;
        }
        return imageLoader;
    }

    public void setImageLoader(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }


    //字体大小设置---------------------------------------------------------------------------
    private FontSize fontSize = FontSize.None;
    private final MutableLiveData<FontSize> fontSizeLiveData = new MutableLiveData<>();

    public FontSize getFontSize() {
        return fontSize;
    }

    public void setFontSize(FontSize fontSize) {
        this.fontSize = fontSize;
        this.fontSizeLiveData.setValue(fontSize);
    }

    public MutableLiveData<FontSize> getFontSizeLiveData() {
        return fontSizeLiveData;
    }


    //聊天表情面板配置---------------------------------------------------------------------
    public ChatEmojiConfig getEmojiConfig() {
        return Chat.getEmojiConfig();
    }


    //聊天插件面板配置---------------------------------------------------------------------
    public ChatPluginConfig getPluginConfig() {
        return Chat.getPluginConfig();
    }


    //消息事件---------------------------------------------------------------------------
    private final List<MessageEventListener> messageEventListeners = new ArrayList<>();

    public void addMessageEventListener(MessageEventListener listener) {
        messageEventListeners.add(listener);
    }

    public void removeMessageEventListener(MessageEventListener listener) {
        messageEventListeners.remove(listener);
    }

    public List<MessageEventListener> getMessageEventListeners() {
        return messageEventListeners;
    }

    //消息拦截器---------------------------------------------------------------------------
    private MessageInterceptListener messageInterceptListener;

    public MessageInterceptListener getMessageInterceptListener() {
        return messageInterceptListener;
    }

    public void setMessageInterceptListener(MessageInterceptListener messageInterceptListener) {
        this.messageInterceptListener = messageInterceptListener;
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

    //---------------------------------------------------------------------------
}
