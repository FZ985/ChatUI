package io.im.uicommon.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.Px
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import io.im.core.model.User
import io.im.core.model.UserConvert
import io.im.uicommon.R
import io.im.uicommon.databinding.CommonKitViewAvatarBinding
import io.im.uicommon.helper.OptionsHelper
import io.im.uicommon.utils.AvatarColor
import java.util.Random
import kotlin.math.abs


/**
 * by DAD FZ
 * 2026/6/12
 * desc：
 **/

@SuppressLint("UseKtx")
class IAvatarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var userInfo: User? = null

    private var defaultRadius = 0f

    private val binding: CommonKitViewAvatarBinding =
        CommonKitViewAvatarBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        val type = context.obtainStyledAttributes(attrs, R.styleable.IAvatarView)

        val textSize = type.getFloat(R.styleable.IAvatarView_iav_text_size, 0f)
        defaultRadius =
            type.getDimensionPixelSize(R.styleable.IAvatarView_iav_image_radius, 0).toFloat()

        type.recycle()

        if (textSize > 0) {
            OptionsHelper.updateTextSize(binding.avatarTv, textSize.toInt())
        }
    }

    fun setUserInfo(info: User) {
        this.userInfo = info
    }

    fun setUserInfoByMessageUser(info: UserConvert) {
        this.userInfo = info.toUserInfo()
    }

    fun imageView(): ImageView {
        return binding.avatar
    }

    fun showTextAvatar(): Boolean {
        return showTextAvatar(0f)
    }

    fun showTextAvatar(@Px radius: Float): Boolean {
        userInfo?.run {
            binding.avatarTv.isVisible = true

            val hashCode = AvatarColor.avatarColor(id)
            val pos = if (hashCode == 0) {
                val random = Random()
                random.nextInt(SIZE)
            } else {
                abs(hashCode) % SIZE
            }

            val drawable = ContextCompat.getDrawable(
                context,
                bgRes[abs(pos)]
            )?.mutate() as GradientDrawable
            drawable.cornerRadius = radius

            binding.avatarTv.background = drawable
            val nickName = this.name
            if (nickName.length <= AVATAR_NAME_LEN) {
                binding.avatarTv.text = nickName
            } else {
                binding.avatarTv.text = nickName.substring(nickName.length - AVATAR_NAME_LEN)
            }
            return true
        } ?: run {
            binding.avatarTv.isVisible = false
            return false
        }
    }

    fun loadAvatar(info: User) {
        setUserInfo(info)
        loadAvatar()
    }

    fun loadAvatar(info: UserConvert) {
        setUserInfoByMessageUser(info)
        loadAvatar()
    }

    private fun loadAvatar() {
        userInfo?.run {
            Glide.with(context)
                .load(avatar)
                .placeholder(R.mipmap.kit_ic_default_avatar)
                .error(R.mipmap.kit_ic_default_avatar)
                .transition(DrawableTransitionOptions.withCrossFade())
                .transform(CenterCrop(), RoundedCorners(defaultRadius.toInt()))
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        p0: GlideException?,
                        p1: Any?,
                        p2: Target<Drawable?>?,
                        p3: Boolean
                    ): Boolean {
                        return showTextAvatar(defaultRadius)
                    }

                    override fun onResourceReady(
                        p0: Drawable?,
                        p1: Any?,
                        p2: Target<Drawable?>?,
                        p3: DataSource?,
                        p4: Boolean
                    ) = false
                }).into(binding.avatar)
        }
    }


    companion object {
        private const val SIZE = 7

        private val bgRes = intArrayOf(
            R.drawable.common_default_avatar_bg_0,
            R.drawable.common_default_avatar_bg_1,
            R.drawable.common_default_avatar_bg_2,
            R.drawable.common_default_avatar_bg_3,
            R.drawable.common_default_avatar_bg_4,
            R.drawable.common_default_avatar_bg_5,
            R.drawable.common_default_avatar_bg_6
        )

        /**
         * default avatar show sub-name length
         */
        private const val AVATAR_NAME_LEN = 1

    }
}