package io.im.core.listener;


import androidx.annotation.Nullable;

/**
 * by DAD FZ
 * 2026/6/25
 * desc：
 **/
public interface FetchCallback<T> {

    void onError(int errorCode, @Nullable String errorMsg);

    void onSuccess(@Nullable T data);

}
