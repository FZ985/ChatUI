package io.im.core.message.im;

import androidx.annotation.Keep;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.im.core.model.Message;
import io.im.core.model.MessageContent;
import io.im.core.utils.ChatNull;

/**
 * author : JFZ
 * date : 2024/1/27 13:31
 * description :
 */
@Keep
public final class ForwardMessage extends MessageContent implements Serializable {

    private String fromName;
    private String toName;

    private List<Message> messages;

    public static ForwardMessage obtain(String fromName, String toName, List<Message> messages) {
        ForwardMessage message = new ForwardMessage();
        message.setFromName(fromName);
        message.setToName(toName);
        message.setMessages(messages);
        return message;
    }

    @Override
    public MessageContent parseContent(JSONObject obj) {
        if (obj != null) {
            setFromName(obj.optString("fromName"));
            setToName(obj.optString("toName"));
            try {
                List<Message> messageList = new ArrayList<>();
                if (obj.has("messages")) {
                    Object mObj = obj.opt("messages");
                    if (mObj instanceof JSONArray) {
                        JSONArray mArr = (JSONArray) mObj;
                        for (int i = 0; i < mArr.length(); i++) {
                            messageList.add(Message.parseMessageFromJson(mArr.getJSONObject(i).toString()));
                        }
                    }
                }
                setMessages(messageList);
            } catch (JSONException e) {
                //
            }
        }
        return this;
    }

    public String getFromName() {
        return ChatNull.compat(fromName);
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getToName() {
        return ChatNull.compat(toName);
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public List<Message> getMessages() {
        return ChatNull.compatList(messages);
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
