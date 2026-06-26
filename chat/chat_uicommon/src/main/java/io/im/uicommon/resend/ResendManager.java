package io.im.uicommon.resend;

import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import java.io.File;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentLinkedQueue;

import io.im.core.core.ChatSDK;
import io.im.core.core.socket.SocketCode;
import io.im.core.listener.MessageCallback;
import io.im.core.message.im.MediaMessage;
import io.im.core.model.Message;
import io.im.core.utils.JLog;
import io.im.core.utils.PathUtil;
import io.im.uicommon.IMCenter;
import io.im.uicommon.MessageOperate;


/**
 * 用于管理重新发送
 */
public class ResendManager {

    private final String TAG = "ResendManager";
    // 发送消息间隔
    private static final int TIME_DELAY = 300;
    private volatile boolean mIsProcessing = false;
    private final Hashtable<Long, Message> mMessageMap = new Hashtable<>();
    private final ConcurrentLinkedQueue<Long> mMessageQueue = new ConcurrentLinkedQueue<>();
    private final Handler mResendHandler;

    private ResendManager() {
        HandlerThread resendThread = new HandlerThread("RESEND_WORK");
        resendThread.start();
        mResendHandler = new Handler(resendThread.getLooper());
        IMCenter.getInstance()
                .getOptions()
                .getConnectService()
                .addConnectListener(result -> {
                    if (result.getCode() == SocketCode.SIGN_OUT) {
                        ResendManager.getInstance().removeAllResendMessage();
                    } else if (result.getCode() == SocketCode.NETWORK_SUCCESS) {
                        // 开始发送缓存队列因发送失败需要重发的消息
                        ResendManager.getInstance().beginResend();
                    }
                });
    }

    private static class ResendManagerHolder {
        private static final ResendManager instance = new ResendManager();
    }

    public static ResendManager getInstance() {
        return ResendManagerHolder.instance;
    }

    public void addResendMessage(final Message message, boolean resend) {
        if (!mMessageMap.containsKey(message.getMessageId())) {
            JLog.d(TAG, "addResendMessage : id=" + message.getMessageId());
            mMessageMap.put(message.getMessageId(), message);
            mMessageQueue.add(message.getMessageId());
            if (resend) {
                beginResend();
            }
        }
    }

    public void removeResendMessage(final long messageId) {
        mResendHandler.post(() -> {
            mMessageMap.remove(messageId);
            mMessageQueue.remove(messageId);
        });
    }

    public void removeAllResendMessage() {
        mResendHandler.post(() -> {
            mMessageMap.clear();
            mMessageQueue.clear();
            mIsProcessing = false;
        });
    }

    public boolean needResend(long messageId) {
        return mMessageMap.containsKey(messageId);
    }

    public void beginResend() {
        mResendHandler.post(() -> {
            if (mMessageMap.isEmpty()) {
                JLog.i(TAG, "beginResend onChanged no message need resend");
                mIsProcessing = false;
                return;
            }
            if (mIsProcessing) {
                JLog.i(TAG, "beginResend ConnectionStatus is resending");
                return;
            }
            mIsProcessing = true;
            loopResendMessage();
        });
    }

    private void loopResendMessage() {
        mResendHandler.postDelayed(() -> {
                    final Long idInteger = mMessageQueue.peek();
                    JLog.d(TAG, "beginResend: messageId = " + idInteger);
                    if (idInteger == null
                            || !IMCenter.getInstance().getOptions().getConnectService().isConnect()) {
                        mIsProcessing = false;
                        return;
                    }
                    Message message = mMessageMap.get(idInteger);
                    if (message == null) {
                        removeResendMessage(idInteger);
                        loopResendMessage();
                        return;
                    }
                    resendMessage(message, new MessageCallback<>() {
                        @Override
                        public void onSuccess(Message message) {
                            removeResendMessage(idInteger);
                            loopResendMessage();
                        }

                        @Override
                        public void onError(Message message, int errorCode) {
                            removeResendMessage(idInteger);
                            loopResendMessage();
                        }
                    });
                },
                TIME_DELAY);
    }


    /**
     * 重发新消息
     *
     * @param message 消息
     */
    private void resendMessage(Message message, MessageCallback<Message> callback) {
        if (TextUtils.isEmpty(message.getToUser().getId()) || message.getMessageContent() == null) {
            JLog.e(TAG, "targetId or messageContent is Null");
            removeResendMessage(message.getMessageId());
            loopResendMessage();
            return;
        }
        if (message.getMessageContent() instanceof MediaMessage) {
            MediaMessage body = (MediaMessage) message.getMessageContent();
            if (!TextUtils.isEmpty(body.getUrl())) {
                MessageOperate.sendMessage(message, null, callback);
            } else {
                Uri uri = body.getLocalUri();
                String path = PathUtil.getPath(ChatSDK.getContext(), uri);
//                YLIMLogger.e("=====上传并发送消息path:" + path);
                if (!TextUtils.isEmpty(path)) {
                    MessageOperate.uploadAndSendMediaMessage(message, new File(path));
                } else {
                    removeResendMessage(message.getMessageId());
                    loopResendMessage();
                }
            }
        } else {
            MessageOperate.sendMessage(message, null, callback);
        }
    }

}
