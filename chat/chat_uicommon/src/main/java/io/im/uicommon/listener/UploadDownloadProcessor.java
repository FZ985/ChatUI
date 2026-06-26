package io.im.uicommon.listener;


import java.io.File;

import io.im.core.listener.ChatFun;

/**
 * by DAD FZ
 * 2026/6/26
 * desc：
 **/
public interface UploadDownloadProcessor {


    void upload(File file, ChatFun.Fun1<String> successCall, ChatFun.Fun1<String> errorCall, ChatFun.Fun1<Float> progressCall);


    void download(String url, ChatFun.Fun1<File> successCall, ChatFun.Fun1<String> errorCall, ChatFun.Fun1<Float> progressCall);


}
