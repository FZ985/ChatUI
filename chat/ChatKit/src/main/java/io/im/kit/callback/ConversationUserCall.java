package io.im.kit.callback;


import io.im.lib.model.UserInfo;

/**
 * author : JFZ
 * date : 2024/1/8 11:16
 * description :
 */
public interface ConversationUserCall {


    void updateUser(UserInfo user);

    UserInfo getUser();

}
