package io.chat.conversation.utils;


import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.chat.conversation.R;
import io.im.uicommon.model.UiSession;
import io.im.uicommon.IMCenter;

/**
 * by DAD FZ
 * 2026/6/23
 * desc：
 **/
public class ConUIHelper {


    //更新未读书UI样式
    public static void updateConversationCount(TextView tv, UiSession session) {
        int count = session.getSession().getUnreadCount();
        if (count <= 0) {
            tv.setVisibility(View.GONE);
            return;
        }
        tv.setVisibility(View.VISIBLE);
        int dp1 = tv.getContext().getResources().getDimensionPixelSize(io.im.core.R.dimen.chat_dp1);
        ViewGroup.LayoutParams lp = tv.getLayoutParams();

        float radio = IMCenter.getInstance().getOptions().getFontSize().getType();

        if (count <= 9) {
            tv.setBackgroundResource(R.drawable.con_read_count_circle);
            lp.width = (int) (dp1 * 16 * radio);
            lp.height = (int) (dp1 * 16 * radio);
            tv.setPadding(0, 0, 0, 0);
            tv.setText(String.valueOf(count));
        } else {
            tv.setBackgroundResource(R.drawable.con_read_count_rect);
            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            String str;
            int horPadding;
            int verPadding;
            if (count > 99) {
                str = "···";
                horPadding = (int) (dp1 * radio * 3);
                verPadding = 0;
            } else {
                str = String.valueOf(count);
                horPadding = (int) (dp1 * radio * 5);
                verPadding = (int) (dp1 * radio);
            }
            tv.setPadding(horPadding, verPadding, horPadding, verPadding);
            tv.setText(str);
        }
        tv.setLayoutParams(lp);
    }

}
