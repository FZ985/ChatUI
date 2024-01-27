package io.im.kit.config;

import io.im.kit.callback.ImageLoader;

/**
 * author : JFZ
 * date : 2024/1/26 17:24
 * description :
 */
public final class Options {

    //会话消息配置
    public ConversationConfig getConversationConfig() {
        return Chat.getConversationConfig();
    }

    //图片加载
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
}
