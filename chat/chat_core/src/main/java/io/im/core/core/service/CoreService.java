package io.im.core.core.service;

import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import io.im.core.core.CoreConstant;
import io.im.core.core.aidl.CoreBind;
import io.im.core.core.aidl.CoreResultInterface;
import io.im.core.core.socket.ISocketCoreService;


/**
 * author : JFZ
 * date : 2023/12/21 14:26
 * description :
 */
public class CoreService extends ISocketCoreService {

    private CoreBind bind;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (bind == null) {
            bind = new CoreBind(this);
        }
        return bind;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    //根据类型和参数去做一些事
    public void toTypeAction(int type, String action, CoreResultInterface callback) {
        mCallback = callback;
        if (type == CoreConstant.Connect) {
            connectWebSocket(action);
        } else if (type == CoreConstant.SendMessage) {
            sendSocketData(action);
        } else if (type == CoreConstant.CloseSocket) {
            closeSocket();
        }
    }

    //根据类型获取结果
    public String getTypeResult(int type, CoreResultInterface callback) {
        mCallback = callback;
        if (type == CoreConstant.CheckSocket) {
            return String.valueOf(checkSocket());
        }
        return "";
    }

    //根据类型 和 参数 获取结果
    public String getTypeDataResult(int type, String data, CoreResultInterface callback) {
        mCallback = callback;
        return "";
    }


}
