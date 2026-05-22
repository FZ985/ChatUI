package io.im.lib.core.aidl;

import android.os.RemoteException;

import io.im.lib.core.service.CoreService;


/**
 * author : JFZ
 * date : 2023/12/22 09:36
 * description :
 */
public class CoreBind extends CoreInterface.Stub {

    private final CoreService service;

    public CoreBind(CoreService service) {
        this.service = service;
    }

    @Override
    public void toTypeAction(int type, String action, CoreResultInterface callback) throws RemoteException {
        service.toTypeAction(type, action, callback);
    }

    @Override
    public String getTypeResult(int type, CoreResultInterface callback) throws RemoteException {
        return service.getTypeResult(type, callback);
    }

    @Override
    public String getTypeDataResult(int type, String data, CoreResultInterface callback) throws RemoteException {
        return service.getTypeDataResult(type, data, callback);
    }

}
