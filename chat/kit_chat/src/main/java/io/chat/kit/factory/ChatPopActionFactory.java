// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

package io.chat.kit.factory;


import android.content.Context;
import android.text.TextUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.chat.kit.R;
import io.chat.kit.provider.ChatProvider;
import io.chat.kit.ui.ActionConstants;
import io.chat.kit.ui.popmenu.IChatPopMenu;
import io.chat.kit.ui.popmenu.IChatPopMenuClickListener;
import io.im.core.MessageType;
import io.im.core.message.im.TextMessage;
import io.im.core.model.Message;
import io.im.core.model.PluginAction;
import io.im.core.utils.ChatNetworkUtil;
import io.im.core.utils.ChatToast;
import io.im.uicommon.utils.MessageCheck;

/**
 * 聊天界面长按弹窗工厂类，根据长按的消息返回对应的弹窗中的内容
 */
public class ChatPopActionFactory {

    private static volatile ChatPopActionFactory instance;

    private WeakReference<IChatPopMenuClickListener> actionListener;

    private WeakReference<IChatPopMenu> customPopMenu;

    private ChatPopActionFactory() {
    }

    public static ChatPopActionFactory getInstance() {
        if (instance == null) {
            synchronized (ChatPopActionFactory.class) {
                if (instance == null) {
                    instance = new ChatPopActionFactory();
                }
            }
        }
        return instance;
    }

    public void setActionListener(IChatPopMenuClickListener actionListener) {
        this.actionListener = new WeakReference<>(actionListener);
    }

    public void setChatPopMenu(IChatPopMenu popMenu) {
        this.customPopMenu = new WeakReference<>(popMenu);
    }

    /**
     * 获取文本长按弹窗中的内容
     *
     * @param text 长按的文本
     * @return 弹窗中的内容列表
     */
    public List<PluginAction> getTextActions(Context context, Message messageInfo, String text) {
        List<PluginAction> actions = new ArrayList<>();
        if (customPopMenu == null
                || customPopMenu.get() == null
                || customPopMenu.get().showDefaultPopMenu()) {
            actions.add(getCopyAction(context, text));
        }
        if (customPopMenu != null && customPopMenu.get() != null) {
            return customPopMenu.get().customizePopMenu(actions, messageInfo);
        }
        return actions;
    }

    /**
     * 获取长按弹窗中的内容
     *
     * @param message 长按的消息
     * @return 弹窗中的内容
     */
    public List<PluginAction> getMessageActions(Context context, Message message) {
        List<PluginAction> actions = new ArrayList<>();
        if (message.getMessageContent() == null) {
            return actions;
        }
        if (customPopMenu == null
                || customPopMenu.get() == null
                || customPopMenu.get().showDefaultPopMenu()) {

            // 自定义消息，根据自定义消息的Type区分
            //复制
            addCopyActionIfNeed(context, actions, message);
            //转发
            actions.add(getForwardAction(context, message));
            //删除
            actions.add(getDeleteAction(context, message));
            //撤回
            addRevokeAction(context, actions, message);
            //多选
            actions.add(getMultiSelectAction(context, message));
            //引用
            actions.add(getReferAction(context, message));

        }
        if (customPopMenu != null && customPopMenu.get() != null) {
            return customPopMenu.get().customizePopMenu(actions, message);
        }
        return actions;
    }

    /**
     * 添加复制操作
     *
     * @param actions 弹窗操作列表
     * @param message 消息
     */
    private void addCopyActionIfNeed(Context context, List<PluginAction> actions, Message message) {
        if (message.getMessageType() == MessageType.CHAT_TEXT
                && message.getMessageContent() instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message.getMessageContent();
            if (!TextUtils.isEmpty(textMessage.getContent())) {
                actions.add(getCopyAction(context, textMessage.getContent()));
            }
        }
    }

    // 构建引用按钮
    private PluginAction<Message> getReferAction(Context context, Message message) {
        return new PluginAction<>(
                ActionConstants.POP_ACTION_REFER,
                context.getString(R.string.chat_message_action_refer),
                R.drawable.kit_message_menu_refer,
                (view, messageInfo) -> {
                    if (actionListener != null) {
                        actionListener.get().onRefer(messageInfo);
                    }
                },
                message);
    }

    // 构建复制按钮
    private PluginAction<String> getCopyAction(Context context, String text) {
        return new PluginAction<>(
                ActionConstants.POP_ACTION_COPY,
                context.getString(R.string.chat_message_action_copy),
                R.drawable.kit_message_menu_copy,
                (view, messageInfo) -> {
                    if (actionListener != null) {
                        actionListener.get().onCopy(messageInfo);
                    }
                },
                text);
    }

    // 构建撤回按钮
    private void addRevokeAction(Context context, List<PluginAction> actions, Message message) {
        if (message.getMessageDirection() == Message.MessageDirection.SEND) {
            long revokeTime = ChatProvider.getOptions().revokeTime;
            if (MessageCheck.checkRevokeMessage(message, revokeTime)) {
                actions.add(new PluginAction<>(
                        ActionConstants.POP_ACTION_REVOKE,
                        context.getString(R.string.chat_message_revoke),
                        R.drawable.kit_message_menu_revoke,
                        (view, messageInfo) -> {
                            if (!ChatNetworkUtil.isConnection(context)) {
                                ChatToast.toast(context, io.im.uicommon.R.string.chat_network_error_tip);
                                return;
                            }
                            if (actionListener != null) {
                                actionListener.get().onRevoke(messageInfo);
                            }
                        },
                        message));
            }
        }
    }

    // 构建语音转文字按钮
//    private PluginAction<ChatMessageBean> getVoiceToTextAction(
//            Context context, ChatMessageBean message) {
//        return new PluginAction<>(
//                ActionConstants.POP_ACTION_VOICE_TO_TEXT,
//                context.getString(R.string.chat_voice_to_text),
//                R.drawable.ic_voice_to_text,
//                (view, messageInfo) -> {
//                    YunXinExt.postOperate();
//                    if (!NetworkUtils.isConnected()) {
//                        ToastX.showShortToast(R.string.chat_network_error_tip);
//                        return;
//                    }
//                    if (actionListener != null) {
//                        actionListener.get().onTransferToText(messageInfo);
//                    }
//                },
//                message);
//    }

    // 构建标记按钮
//    private PluginAction<ChatMessageBean> getPinAction(Context context, ChatMessageBean message) {
//        return new PluginAction<>(
//                ActionConstants.POP_ACTION_PIN,
//                !TextUtils.isEmpty(message.getPinAccid())
//                        ? context.getString(R.string.chat_message_action_pin_cancel)
//                        : context.getString(R.string.chat_message_action_pin),
//                R.drawable.ic_message_sign,
//                (view, messageInfo) -> {
//                    YunXinExt.postOperate();
//                    if (!NetworkUtils.isConnected()) {
//                        ToastX.showShortToast(R.string.chat_network_error_tip);
//                        return;
//                    }
//                    if (actionListener != null) {
//                        actionListener
//                                .get()
//                                .onSignal(messageInfo, !TextUtils.isEmpty(messageInfo.getPinAccid()));
//                    }
//                },
//                message);
//    }

    // 构建多选按钮
    private PluginAction<Message> getMultiSelectAction(
            Context context, Message message) {
        return new PluginAction<>(
                ActionConstants.POP_ACTION_MULTI_SELECT,
                context.getString(R.string.chat_message_action_multi_select),
                R.drawable.kit_message_menu_multi_select,
                (view, messageInfo) -> {
                    if (actionListener != null) {
                        actionListener.get().onMultiSelected(messageInfo);
                    }
                },
                message);
    }

    // 构建置顶按钮
//    private PluginAction<ChatMessageBean> getTopStickyAction(
//            Context context, ChatMessageBean message) {
//        boolean isAdd;
//        if (ChatUserCache.getInstance().getTopMessage() != null) {
//            isAdd = !ChatUserCache.getInstance().getTopMessage().equals(message.getMessageData());
//        } else {
//            isAdd = true;
//        }
//        return new PluginAction<>(
//                ActionConstants.POP_ACTION_TOP_STICK,
//                isAdd
//                        ? context.getString(R.string.chat_message_action_top)
//                        : context.getString(R.string.chat_message_action_cancel_top),
//                isAdd ? R.drawable.ic_pop_top_sticky : R.drawable.ic_pop_untop_sticky,
//                (view, messageInfo) -> {
//                    YunXinExt.postOperate();
//                    if (!NetworkUtils.isConnected()) {
//                        ToastX.showShortToast(R.string.chat_network_error_tip);
//                        return;
//                    }
//                    if (actionListener != null) {
//                        actionListener.get().onTopSticky(messageInfo, isAdd);
//                    }
//                },
//                message);
//    }

    // 构建收藏按钮
//    private PluginAction<ChatMessageBean> getCollectionAction(
//            Context context, ChatMessageBean message) {
//        return new PluginAction<>(
//                ActionConstants.POP_ACTION_COLLECTION,
//                context.getString(R.string.chat_message_action_collection),
//                R.drawable.ic_message_collection,
//                (view, messageInfo) -> {
//                    YunXinExt.postOperate();
//                    if (!NetworkUtils.isConnected()) {
//                        ToastX.showShortToast(R.string.chat_network_error_tip);
//                        return;
//                    }
//                    if (actionListener != null) {
//                        actionListener.get().onCollection(messageInfo);
//                    }
//                },
//                message);
//    }

    // 构建删除按钮
    private PluginAction<Message> getDeleteAction(Context context, Message message) {
        return new PluginAction<>(
                ActionConstants.POP_ACTION_DELETE,
                context.getString(R.string.chat_message_action_delete),
                R.drawable.kit_message_menu_delete,
                (view, messageInfo) -> {
                    if (!ChatNetworkUtil.isConnection(context)) {
                        ChatToast.toast(context, io.im.uicommon.R.string.chat_network_error_tip);
                        return;
                    }
                    if (actionListener != null) {
                        actionListener.get().onDelete(message);
                    }
                },
                message);
    }

    // 构建转发按钮
    private PluginAction<Message> getForwardAction(Context context, Message message) {
        return new PluginAction<>(
                ActionConstants.POP_ACTION_FORWARD,
                context.getString(R.string.chat_message_action_forward),
                R.drawable.kit_message_menu_forward,
                (view, messageInfo) -> {
                    if (!ChatNetworkUtil.isConnection(context)) {
                        ChatToast.toast(context, io.im.uicommon.R.string.chat_network_error_tip);
                        return;
                    }
                    if (actionListener != null) {
                        actionListener.get().onForward(messageInfo);
                    }
                },
                message);
    }
}
