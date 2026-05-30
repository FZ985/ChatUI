package io.im.uicommon.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;


/**
 * Create by JFZ
 * date: 2020-07-07 11:02
 **/
public class IMBasicWebView extends WebView {

    public IMBasicWebView(Context context) {
        super(context);
        init(context);
    }

    public IMBasicWebView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public IMBasicWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init(Context context) {
        getSettings().setJavaScriptEnabled(true);
        getSettings().setBlockNetworkImage(false);//解决图片不显示
        getSettings().setUseWideViewPort(true);
        getSettings().setTextZoom(100);
        getSettings().setPluginState(WebSettings.PluginState.ON);//视频播放相关配置
        getSettings().setDefaultTextEncodingName("utf-8");
        getSettings().setDomStorageEnabled(true);
        getSettings().setAllowContentAccess(true);
        getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        getSettings().setAllowFileAccessFromFileURLs(true);
        getSettings().setAllowFileAccess(true);
        getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        getSettings().setLoadsImagesAutomatically(true);
        getSettings().setTextSize(WebSettings.TextSize.NORMAL);
        getSettings().setDisplayZoomControls(false);
        WebView.setWebContentsDebuggingEnabled(true);
        getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        getSettings().setSupportZoom(false);

        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

}
