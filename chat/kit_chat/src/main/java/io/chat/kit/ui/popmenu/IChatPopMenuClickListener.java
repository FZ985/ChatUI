// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package io.chat.kit.ui.popmenu;

import android.view.View;

import io.im.core.model.Message;

/**
 * 消息长按菜单按钮点击事件
 */
public interface IChatPopMenuClickListener {

    // 点击复制
    default boolean onCopy(String text) {
        return false;
    }

    default boolean onTextSelected(View view, int position, Message messageInfo, String text, boolean isSelectAll) {
        return false;
    }

    // 点击回复
    default boolean onReply(Message messageInfo) {
        return false;
    }

    // 点击转发
    default boolean onForward(Message messageInfo) {
        return false;
    }

    // 点击置顶
//  default boolean onTopSticky(ChatMessageBean messageInfo, boolean isAdd) {
//    return false;
//  }

    // 点击标记
//  default boolean onSignal(ChatMessageBean messageInfo, boolean isCancel) {
//    return false;
//  }

    //点击多选
    default boolean onMultiSelected(Message messageInfo) {
        return false;
    }

    // 点击收藏
//  default boolean onCollection(ChatMessageBean messageInfo) {
//    return false;
//  }

    // 点击删除
    default boolean onDelete(Message messageInfo) {
        return false;
    }

    // 点击撤回
//  default boolean onRecall(ChatMessageBean messageInfo) {
//    return false;
//  }

    // 点击语音转文字
//  default boolean onTransferToText(ChatMessageBean messageInfo) {
//    return false;
//  }

    // 自定义按钮点击
//  default boolean onCustom(View view, ChatMessageBean messageInfo, String action) {
//    return false;
//  }
}
