package io.im.kit.config;

import androidx.lifecycle.MutableLiveData;

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


    //字体大小设置
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
}
