package io.im.uicommon.config;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import io.im.core.model.Message;
import io.im.core.model.Session;
import io.im.core.model.UserInfo;
import io.im.core.utils.ChatLibUtil;
import io.im.uicommon.R;
import io.im.uicommon.listener.ImageLoader;
import io.im.uicommon.widgets.IAvatarView;

/**
 * author : JFZ
 * date : 2024/1/27 17:17
 * description :
 */
public class DefaultImageLoader implements ImageLoader {
    @Override
    public void loadChatAvatar(Context context, IAvatarView view, Message message, boolean isSender) {
        Glide.with(context)
                .load(message.getFromUser().getAvatar())
                .placeholder(R.mipmap.kit_ic_default_avatar)
                .error(R.mipmap.kit_ic_default_avatar)
                .transition(DrawableTransitionOptions.withCrossFade())
                .transform(new CenterCrop(), new RoundedCorners(ChatLibUtil.dip2px(context, 5)))
                .addListener(new DefaultRequestListener(view, ChatLibUtil.dip2px(context, 5)))
                .into(view.imageView());
    }

    @Override
    public void loadForwardSelectorAvatar(Context context, IAvatarView view, UserInfo userInfo) {
        Glide.with(context)
                .load(userInfo.getAvatar())
                .placeholder(R.mipmap.kit_ic_default_avatar)
                .error(R.mipmap.kit_ic_default_avatar)
                .transition(DrawableTransitionOptions.withCrossFade())
                .transform(new CenterCrop(), new RoundedCorners(ChatLibUtil.dip2px(context, 5)))
                .addListener(new DefaultRequestListener(view, ChatLibUtil.dip2px(context, 5)))
                .into(view.imageView());
    }

    @Override
    public void loadConversationAvatar(Context context, IAvatarView view, Session session) {
        Glide.with(context)
                .load(session.getUser().getAvatar())
                .placeholder(R.mipmap.kit_ic_default_avatar)
                .error(R.mipmap.kit_ic_default_avatar)
                .transition(DrawableTransitionOptions.withCrossFade())
                .transform(new CenterCrop(), new RoundedCorners(ChatLibUtil.dip2px(context, 5)))
                .addListener(new DefaultRequestListener(view, ChatLibUtil.dip2px(context, 5)))
                .into(view.imageView());
    }


    private static class DefaultRequestListener implements RequestListener<Drawable> {

        final IAvatarView avatarView;
        final float radius;

        public DefaultRequestListener(@NonNull IAvatarView avatarView, float radius) {
            this.avatarView = avatarView;
            this.radius = radius;
        }

        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            return avatarView.showTextAvatar(radius);
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            return false;
        }
    }
}
