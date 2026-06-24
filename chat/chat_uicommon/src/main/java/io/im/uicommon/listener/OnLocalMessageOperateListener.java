package io.im.uicommon.listener;


import androidx.annotation.Nullable;

import io.im.core.model.Message;

/**
 * by DAD FZ
 * 2026/6/24
 * desc：部分本地消息操作回调
 **/
public interface OnLocalMessageOperateListener {

    //本地消息删除后的最新消息
    default void onDeletedAfterLastMessage(String sid, @Nullable Message message) {
    }

}
