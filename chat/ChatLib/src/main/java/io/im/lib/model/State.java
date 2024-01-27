package io.im.lib.model;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class State {
    @IntDef({NORMAL, ERROR, PROGRESS, CANCEL, RECEIVED, PAUSE, SUCCESS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Value {
    }

    public static final int NORMAL = 0;

    public static final int PROGRESS = 1;

    public static final int SUCCESS = 2;

    public static final int ERROR = 3;

    public static final int RECEIVED = 4;

    public static final int PAUSE = 5;

    public static final int CANCEL = 6;
}
