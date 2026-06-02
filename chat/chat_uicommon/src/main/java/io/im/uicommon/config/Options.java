package io.im.uicommon.config;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import io.im.uicommon.config.enums.FontSize;
import io.im.uicommon.listener.IPermissionProxy;
import io.im.uicommon.listener.ImageLoader;
import io.im.uicommon.listener.MessageEventListener;
import io.im.uicommon.listener.MessageInterceptListener;

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

    //权限申请代理---------------------------------------------------------------------------
    private IPermissionProxy permissionProxy;

    public IPermissionProxy getPermissionProxy() {
        return permissionProxy;
    }

    public void setPermissionProxy(IPermissionProxy permissionProxy) {
        this.permissionProxy = permissionProxy;
    }


    //语音消息是否连续播放---------------------------------------------------------------------
    private boolean rc_play_audio_continuous = true;

    public boolean isRc_play_audio_continuous() {
        return rc_play_audio_continuous;
    }

    public void setRc_play_audio_continuous(boolean rc_play_audio_continuous) {
        this.rc_play_audio_continuous = rc_play_audio_continuous;
    }


    //---------------------------------------------------------------------------
}
