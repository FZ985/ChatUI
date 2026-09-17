package io.im.app;


import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.fluid.afm.AFMInitializer;

import org.json.JSONObject;

import io.im.app.ai.AiImageHandler;
import io.im.core.core.ChatSDK;
import io.im.core.core.CoreConstant;
import io.im.core.core.CoreSingle;
import io.im.core.core.aidl.CoreResultInterface;
import io.im.core.core.socket.SocketCode;
import io.im.core.core.socket.WebSocketResult;
import io.im.core.listener.ChatFun;
import io.im.core.model.Message;
import io.im.core.utils.ChatNetworkUtil;

/**
 * by DAD FZ
 * 2026/9/10
 * desc：模拟消息发送与接收
 **/
public class AIInit {

    private static final Handler testHandler = new Handler(Looper.getMainLooper());

    public static void init(Application application) {
        AFMInitializer.init(application, null, new AiImageHandler(), null);
        CoreSingle.getInstance().setDebugCall(new ChatFun.Fun2<Message, CoreResultInterface>() {
            @Override
            public void apply(Message message, CoreResultInterface callback) {
                onMoNi(application, message, callback);
            }
        });
    }


    private static void onMoNi(Context context, Message message, CoreResultInterface callback) {
        //模拟发送成功
        try {
            String msgJson = message.toJson();
            JSONObject jo = new JSONObject(msgJson);
            if (ChatNetworkUtil.isConnection(ChatSDK.getContext())) {
                jo.put("code", SocketCode.success);
            } else {
                jo.put("code", SocketCode.NETWORK_ERROR);
            }
            String data = new WebSocketResult(SocketCode.SOCKET_MESSAGE, jo.toString()).toJson();
            callback.onResult(CoreConstant.SocketResponse, data);

            testHandler.postDelayed(() -> {
                try {
                    Message flipMessage = message.flipFromTo();
                    flipMessage.setExtMessage("");
                    flipMessage.setMessageId(flipMessage.buildMessageId());

                    String receiveJson = flipMessage.toJson();
                    JSONObject receiveObj = new JSONObject(receiveJson);
                    receiveObj.put("code", SocketCode.success);
                    String receiveData = new WebSocketResult(SocketCode.SOCKET_MESSAGE, receiveObj.toString()).toJson();
                    callback.onResult(CoreConstant.SocketResponse, receiveData);


                    //模拟AI消息
//                    if (flipMessage.getMessageType() == MessageType.CHAT_TEXT) {
//                        testHandler.postDelayed(() -> {
//                            try {
//                                AIMessage ai = AIMessage.obtain(context.getResources().getString(R.string.ai_sample));
//                                flipMessage.setMessageType(AIMessage.TYPE_AI_MESSAGE);
//                                flipMessage.updateMessageBody(ai);
//
//                                String receiveJson2 = flipMessage.toJson();
//                                JSONObject receiveObj2 = new JSONObject(receiveJson2);
//
//                                receiveObj2.put("code", SocketCode.success);
//
//                                String receiveData2 = new WebSocketResult(SocketCode.SOCKET_MESSAGE, receiveObj2.toString()).toJson();
//
//                                callback.onResult(CoreConstant.SocketResponse, receiveData2);
//                            } catch (Exception e) {
//
//                            }
//                        }, 200);
//                    }
                } catch (Exception e) {
                    //
                }
            }, 300);
        } catch (Exception e) {
            //
        }

    }
}
