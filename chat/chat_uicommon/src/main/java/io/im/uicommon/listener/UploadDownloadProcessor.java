package io.im.uicommon.listener;


import java.io.File;

import io.im.core.listener.ChatFun;

/**
 * by DAD FZ
 * 2026/6/26
 * desc：
 **/
public interface UploadDownloadProcessor {


    /**
     *
     * @param file         上传文件
     * @param successCall  成功回调
     * @param errorCall    失败回调
     * @param progressCall 进度回调
     */
    void upload(File file,
                ChatFun.Fun1<String> successCall,
                ChatFun.Fun1<String> errorCall,
                ChatFun.Fun1<Float> progressCall);


    /**
     *
     * @param url          下载地址
     * @param path         保存路径
     * @param fileName     保存文件名称
     * @param successCall  成功回调
     * @param errorCall    失败回调
     * @param progressCall 进度回调 （当前下载量，百分比，总下载量）
     */
    void download(String url,
                  String path,
                  String fileName,
                  ChatFun.Fun1<File> successCall,
                  ChatFun.Fun1<String> errorCall,
                  ChatFun.Fun3<Long, Float, Long> progressCall);


}
