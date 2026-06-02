package io.im.uicommon.listener;


import android.content.Context;

import io.im.core.listener.ChatFun;
import io.im.uicommon.event.PermissionEvent;

/**
 * by DAD FZ
 * 2026/6/1
 * desc：
 **/
public interface IPermissionProxy {

    /**
     *
     * @param context               上下文
     * @param event                 请求权限事件
     * @param permissionSuccessCall 请求成功，执行逻辑
     */
    void requestPermission(Context context, PermissionEvent event, ChatFun.Fun permissionSuccessCall);

}
