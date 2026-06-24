package io.im.app;


import android.app.Application;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

import io.chat.kit.IMTest;
import io.im.core.model.UserInfo;
import io.im.uicommon.IMCenter;
import io.im.uicommon.config.Options;

/**
 * by DAD FZ
 * 2026/5/22
 * desc：
 **/
public class ImDebug {


    public static String wss = "wss://socket.linyichengxin.com:9530";

    private static final String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3NzkxNzQ5NTQsInVzZXJuYW1lIjoiMTYzMDAwMzIzNDYyNDQ5NTYxNyJ9.QZg_xBAcUGhoqJfERa_bThU9ed0P9OAVn6I89welfkE";

    private static final String version = "1";

    private static final String type = "2";

    public static void init(Application application) {
        Options options = new Options();
        IMCenter.init(application, options);


        //连接socket
//        ConnectRequest request = ConnectRequest.get().url(wss);
//        request.addParam("version", version);
//        request.addParam("token", token);
//        request.addParam("type", type);
//        request.addParam("device", "android_" + getAndroidId(application));
//        IMCenter.getInstance().connect(request);

        //模拟IM登录
        IMCenter.login(getLoginUser());
    }


    private static UserInfo getLoginUser() {
        UserInfo user = IMCenter.getLoginUser();
        if (TextUtils.isEmpty(user.getId())) {
            return IMTest.loginUser;
        }
        return user;
    }


    public static String getAndroidId(Context context) {
        String aid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (aid != null && !"".equals(aid)) {
            return aid;
        }
        return "";
    }
}
