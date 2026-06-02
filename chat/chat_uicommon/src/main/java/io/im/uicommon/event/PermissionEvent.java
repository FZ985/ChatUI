package io.im.uicommon.event;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * by DAD FZ
 * 2026/6/1
 * desc：
 **/
public class PermissionEvent {

    @IntDef({NONE, AUDIO, CAMERA})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Event {
    }

    public static final int NONE = -1;
    public static final int AUDIO = 1;
    public static final int CAMERA = 2;

    private final int event;

    public PermissionEvent(int event) {
        this.event = event;
    }

    public int getEvent() {
        return event;
    }
}
