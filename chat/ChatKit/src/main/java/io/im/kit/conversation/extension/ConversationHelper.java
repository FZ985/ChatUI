package io.im.kit.conversation.extension;

import io.im.kit.callback.ConversationUserCall;
import io.im.kit.callback.ConversationUserUpdate;
import io.im.kit.config.InputStyle;
import io.im.kit.conversation.IConversationFragment;
import io.im.kit.utils.RouteUtil;
import io.im.lib.callback.ChatLifecycle;

/**
 * author : JFZ
 * date : 2024/1/30 17:09
 * description :
 */
public class ConversationHelper implements ChatLifecycle, ConversationUserUpdate {


    public void bindConversation(IConversationFragment fragment){
        InputStyle.setType(
                fragment.requireActivity().getIntent().getIntExtra(
                        RouteUtil.InputStyle,
                        InputStyle.All.getType()
                )
        );
    }

    @Override
    public void updateConversationUserCall(ConversationUserCall userCall) {

    }
}
