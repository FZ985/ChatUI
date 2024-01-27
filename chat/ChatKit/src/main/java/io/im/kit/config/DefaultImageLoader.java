package io.im.kit.config;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import io.im.kit.R;
import io.im.kit.callback.ImageLoader;
import io.im.lib.model.Message;
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
                .load(isSender ? message.getFromAvatar() : message.getToAvatar())
                .placeholder(R.mipmap.kit_ic_default_avatar)
                .error(R.mipmap.kit_ic_default_avatar)
                .transition(DrawableTransitionOptions.withCrossFade())
                .transform(new CenterCrop(), new RoundedCorners(ChatLibUtil.dip2px(context, 5)))
                .into(view);
    }
}
