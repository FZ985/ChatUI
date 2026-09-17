package io.im.core.model;


import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.io.Serializable;

import io.im.core.utils.ChatLibUtil;

/**
 * by DAD FZ
 * 2026/9/17
 * desc：
 **/
public class ExtMessage implements Serializable {

    //引用消息扩展类型
    public static final int EXT_REFER = Math.abs("EXT_REFER".hashCode());

    private final int extType;

    /**
     * 引用消息
     * type:EXT_REFER
     * data: Message json结构
     * -------------------------
     */
    private final String extData;


    public ExtMessage(int extType, String extData) {
        this.extType = extType;
        this.extData = extData == null ? "" : extData;
    }

    public static String buildReferMessageJson(String extData) {
        return ChatLibUtil.toJson(new ExtMessage(EXT_REFER, extData));
    }

    public static ExtMessage buildReferMessage(@Nullable Message message) {
        if (message != null) {
            return new ExtMessage(EXT_REFER, message.toJson());
        }
        return new ExtMessage(EXT_REFER, "");
    }

    @Nullable
    public Message getReferMessage() {
        if (extType == EXT_REFER && !TextUtils.isEmpty(extData)) {
            return Message.parseMessageFromJsonOrNull(extData);
        }
        return null;
    }

    public int getExtType() {
        return extType;
    }

    public String getExtData() {
        return extData;
    }


    public String toJson() {
        return ChatLibUtil.toJson(this);
    }
}
