// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package io.im.uicommon.widgets.text.selection;

import android.view.View;

import io.im.core.model.Message;


/**
 * 选择文本信息回调
 */
public interface SelectableOnChangeListener {
    /**
     * 选择文本变化
     *
     * @param view        视图
     * @param position    位置
     * @param message     消息
     * @param text        选择文本
     * @param isSelectAll 是否全选
     */
    void onChange(View view, int position, Message message, CharSequence text, boolean isSelectAll, int eventAction);
}
