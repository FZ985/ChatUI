package io.im.core.utils;


import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.util.Locale;

import io.im.core.core.ChatSDK;
import io.im.core.model.ConversationType;

/**
 * by DAD FZ
 * 2026/6/23
 * desc：会话ID转换类
 **/
public final class ConversationIdUtil {

    private ConversationIdUtil() {
    }

    private static final String TAG = "V2NIMConversationIdUtil";
    private static final String CONVERSATION_ID_FORMAT = "%s|%d|%s";
    private static final String CONVERSATION_ID_SPLIT = "\\|";

    private static final String nullStr = "";

    /**
     * 生成会话id
     *
     * @param targetId 目标id
     * @param type     会话类型
     * @return 会话id
     */
    public static String conversationId(String targetId, ConversationType type) {
        if (TextUtils.isEmpty(targetId) || type == null) {
            return nullStr;
        }
        String account = ChatSDK.getAccountId();
        if (TextUtils.isEmpty(account)) {
            return nullStr;
        }
        return String.format(Locale.ENGLISH, CONVERSATION_ID_FORMAT, account, type.getValue(), targetId);
    }


    /**
     * 生成p2p会话id
     *
     * @param accountId 对方账号id
     * @return 会话id
     */
    public static String p2pConversationId(String accountId) {
        return conversationId(accountId, ConversationType.TYPE_P2P);
    }

    /**
     * 生成群聊会话id
     *
     * @param teamId 群id
     * @return 会话id
     */
    public static String teamConversationId(String teamId) {
        return conversationId(teamId, ConversationType.TYPE_GROUP);
    }

    /**
     * 根据会话id返回会话类型
     *
     * @param conversationId 会话id
     * @return 会话类型
     */
    public static ConversationType conversationType(@NonNull String conversationId) {
        if (TextUtils.isEmpty(conversationId)) {
            return ConversationType.TYPE_UNKNOWN;
        }
        String[] components = conversationId.split(CONVERSATION_ID_SPLIT);
        // 3 parts
        if (components.length != 3) {
            return ConversationType.TYPE_UNKNOWN;
        }
        // account and target
        if (TextUtils.isEmpty(components[0]) || TextUtils.isEmpty(components[2])) {
            return ConversationType.TYPE_UNKNOWN;
        }
        try {
            int type = Integer.parseInt(components[1]);
            return ConversationType.setValue(type);
        } catch (Throwable e) {
            JLog.e(TAG, "conversationType error, conversationId=" + conversationId + ", e=" + e.getMessage());
            return ConversationType.TYPE_UNKNOWN;
        }
    }


    /**
     * 根据会话id返回会话对应目标 ID(accountId、teamId等等)
     *
     * @param conversationId 会话id
     * @return 会话目标id
     */
    public static String conversationToAccountId(String conversationId) {
        if (TextUtils.isEmpty(conversationId)) {
            return nullStr;
        }
        String[] components = conversationId.split(CONVERSATION_ID_SPLIT);
        // 3 parts
        if (components.length != 3) {
            return nullStr;
        }
        // account and target
        if (TextUtils.isEmpty(components[0]) || TextUtils.isEmpty(components[2])) {
            return nullStr;
        }
        return components[2];
    }

    /**
     * 会话id是否合法
     *
     * @param conversationId 会话id
     * @return 是否合法 true:合法 false:不合法
     */
    public static boolean isConversationIdValid(String conversationId) {
        if (TextUtils.isEmpty(conversationId)) {
            return false;
        }
        String[] components = conversationId.split(CONVERSATION_ID_SPLIT);
        // 3 parts
        if (components.length != 3) {
            return false;
        }

        String account = ChatSDK.getAccountId();
        if (account != null && !account.equals(components[0])) {
            return false;
        }

        if (TextUtils.isEmpty(components[0])) {
            return false;
        }

        ConversationType type;
        try {
            int typeInt = Integer.parseInt(components[1]);
            type = ConversationType.setValue(typeInt);
        } catch (NumberFormatException e) {
            return false;
        }
        if (type == ConversationType.TYPE_UNKNOWN) {
            return false;
        }

        if (TextUtils.isEmpty(components[2])) {
            return false;
        }

        return true;

    }
}
