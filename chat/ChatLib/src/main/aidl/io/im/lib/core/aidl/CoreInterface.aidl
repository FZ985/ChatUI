// CoreInterface.aidl
package io.im.lib.core.aidl;

import io.im.lib.core.aidl.CoreResultInterface;
// Declare any non-default types here with import statements

interface CoreInterface {

      //根据类型和参数去做一些事
      void toTypeAction(int type,String action,CoreResultInterface callback);

      //根据类型获取结果
      String getTypeResult(int type,CoreResultInterface callback);

      //根据类型 和 参数 获取结果
      String getTypeDataResult(int type,String data,CoreResultInterface callback);

}