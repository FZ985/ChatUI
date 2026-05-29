package io.im.kit.config;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import io.im.kit.R;
import io.im.kit.listener.ImageLoader;
import io.im.lib.model.Message;
import io.im.lib.model.UserInfo;
import io.im.lib.utils.ChatLibUtil;

/**
 * author : JFZ
 * date : 2024/1/27 17:17
 * description :
 */
public class DefaultImageLoader implements ImageLoader {
    @Override
    public void loadConversationAvatar(Context context, ImageView view, Message message, boolean isSender) {
        Glide.with(context)
                .load(isSender ? message.getFromUser().getUserAvatar() : message.getToUser().getUserAvatar())
                .placeholder(R.mipmap.kit_ic_default_avatar)
                .error(R.mipmap.kit_ic_default_avatar)
                .transition(DrawableTransitionOptions.withCrossFade())
                .transform(new CenterCrop(), new RoundedCorners(ChatLibUtil.dip2px(context, 5)))
                .into(view);
    }

    @Override
    public void loadForwardSelectorAvatar(Context context, ImageView view, UserInfo userInfo) {
        Glide.with(context)
                .load(userInfo.getUserAvatar())
                .placeholder(R.mipmap.kit_ic_default_avatar)
                .error(R.mipmap.kit_ic_default_avatar)
                .transition(DrawableTransitionOptions.withCrossFade())
                .transform(new CenterCrop(), new RoundedCorners(ChatLibUtil.dip2px(context, 5)))
                .into(view);
    }
}
