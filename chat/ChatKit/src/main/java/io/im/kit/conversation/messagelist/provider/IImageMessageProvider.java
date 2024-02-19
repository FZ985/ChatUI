package io.im.kit.conversation.messagelist.provider;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

import io.im.kit.R;
import io.im.kit.model.UiMessage;
import io.im.kit.widget.adapter.IViewProviderListener;
import io.im.kit.widget.adapter.ViewHolder;
import io.im.lib.message.ImageMessage;
import io.im.lib.model.MessageContent;
import io.im.lib.utils.ChatLibUtil;
import io.im.lib.utils.ChatNull;

/**
 * author : JFZ
 * date : 2024/1/27 14:36
 * description :
 */
public class IImageMessageProvider extends BaseMessageItemProvider<ImageMessage> {

    private final static int THUMB_COMPRESSED_SIZE = 320;
    private final static int THUMB_COMPRESSED_MIN_SIZE = 240;

    private Integer minSize = null;
    private Integer maxSize = null;

    public IImageMessageProvider() {
        mConfig.showProgress = false;
        mConfig.showReadState = true;
        mConfig.showContentBubble = false;
    }

    @Override
    protected ViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent.getContext(), LayoutInflater.from(parent.getContext()).inflate(R.layout.kit_item_message_image, parent, false));
    }

    @Override
    protected void bindContentViewHolder(ViewHolder parentHolder, ViewHolder contentHolder, ImageMessage message, UiMessage uiMessage, boolean isSender, int position, List<UiMessage> list, IViewProviderListener<UiMessage> listener) {
        final RelativeLayout root = contentHolder.getView(R.id.msg_image_root);
        final ShapeableImageView imageView = contentHolder.getView(R.id.msg_image);
        int size35 = ChatLibUtil.dip2px(imageView.getContext(), 40);
        String thumbUri = "";
        boolean localExit = message.isLocalExit(contentHolder.getContext());
        if (localExit) {
            thumbUri = message.getLocalUri();
        } else {
            thumbUri = ChatNull.compatValue(message.getUrl());
        }
        int width = message.getWidth();
        int height = message.getHeight();
        if (width != 0 && height != 0) {
            if (!TextUtils.isEmpty(thumbUri)) {
                measureLayoutParams(root, width, height);
                Glide.with(imageView)
                        .load(thumbUri)
                        .error(isSender ? R.drawable.kit_send_thumb_image_broken
                                : R.drawable.kit_received_thumb_image_broken)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .transform(new RoundedCorners(ChatLibUtil.dip2px(imageView.getContext(), 5)))
                        .into(imageView);
            } else {
                ViewGroup.LayoutParams params = root.getLayoutParams();
                params.height = size35;
                params.width = size35;
                root.setLayoutParams(params);
                imageView.setImageResource(
                        isSender ? R.drawable.kit_send_thumb_image_broken
                                : R.drawable.kit_received_thumb_image_broken);
            }
        } else {
            int size = ChatLibUtil.dip2px(contentHolder.getContext(), 150);
            if (!TextUtils.isEmpty(thumbUri)) {
                measureLayoutParams(root, size, size);
                Glide.with(imageView)
                        .load(thumbUri)
                        .error(isSender ? R.drawable.kit_send_thumb_image_broken
                                : R.drawable.kit_received_thumb_image_broken)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .transform(new RoundedCorners(ChatLibUtil.dip2px(imageView.getContext(), 5)))
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(
                                    @Nullable GlideException e,
                                    Object model,
                                    Target<Drawable> target,
                                    boolean isFirstResource) {
                                ViewGroup.LayoutParams params = root.getLayoutParams();
                                params.height = size35;
                                params.width = size35;
                                root.setLayoutParams(params);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(
                                    Drawable resource,
                                    Object model,
                                    Target<Drawable> target,
                                    DataSource dataSource,
                                    boolean isFirstResource) {
                                measureLayoutParams(root, resource);
                                return false;
                            }
                        }).into(imageView);
            } else {
                ViewGroup.LayoutParams params = root.getLayoutParams();
                params.height = size35;
                params.width = size35;
                root.setLayoutParams(params);
                imageView.setImageResource(
                        isSender ? R.drawable.kit_send_thumb_image_broken
                                : R.drawable.kit_received_thumb_image_broken);
            }
        }
    }

    // 图片消息最小值为 100 X 100，最大值为 240 X 240
    // 重新梳理规则，如下：
    // 1、宽高任意一边小于 100 时，如：20 X 40 ，则取最小边，按比例放大到 100 进行显示，如最大边超过240 时，居中截取 240
    // 进行显示
    // 2、宽高都小于 240 时，大于 100 时，如：120 X 140 ，则取最长边，按比例放大到 240 进行显示
    // 3、宽高任意一边大于240时，分两种情况：
    // (1）如果宽高比没有超过 2.4，等比压缩，取长边 240 进行显示。
    // (2）如果宽高比超过 2.4，等比缩放（压缩或者放大），取短边 100，长边居中截取 240 进行显示。
    private void measureLayoutParams(View view, Drawable drawable) {
        measureLayoutParams(view, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }

    private void measureLayoutParams(View view, int width, int height) {
        if (view == null) {
            return;
        }
        if (minSize == null) {
            minSize = THUMB_COMPRESSED_MIN_SIZE;
        }
        if (maxSize == null) {
            maxSize = THUMB_COMPRESSED_SIZE;
        }
        int finalWidth;
        int finalHeight;
        if (width < minSize || height < minSize) {
            if (width < height) {
                finalWidth = minSize;
                finalHeight = Math.min((int) (minSize * 1f / width * height), maxSize);
            } else {
                finalHeight = minSize;
                finalWidth = Math.min((int) (minSize * 1f / height * width), maxSize);
            }
        } else if (width < maxSize && height < maxSize) {
            finalWidth = width;
            finalHeight = height;
        } else {
            if (width > height) {
                if (width * 1f / height <= maxSize * 1.0f / minSize) {
                    finalWidth = maxSize;
                    finalHeight = (int) (maxSize * 1f / width * height);
                } else {
                    finalWidth = maxSize;
                    finalHeight = minSize;
                }
            } else {
                if (height * 1f / width <= maxSize * 1.0f / minSize) {
                    finalHeight = maxSize;
                    finalWidth = (int) (maxSize * 1f / height * width);
                } else {
                    finalHeight = maxSize;
                    finalWidth = minSize;
                }
            }
        }
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = ChatLibUtil.dip2px(view.getContext(), finalHeight / 2f);
        params.width = ChatLibUtil.dip2px(view.getContext(), finalWidth / 2f);
        view.setLayoutParams(params);
    }

    @Override
    protected boolean isMessageViewType(@Nullable MessageContent messageContent) {
        return messageContent != null && messageContent instanceof ImageMessage;
    }

    @Override
    protected boolean onItemClick(ViewHolder holder, View view, ImageMessage imageMessage, UiMessage uiMessage, int position, List<UiMessage> list, IViewProviderListener<UiMessage> listener) {
        return false;
    }

    @Override
    public Spannable getSummarySpannable(Context context, ImageMessage imageMessage) {
        return new SpannableString("[图片]");
    }
}
