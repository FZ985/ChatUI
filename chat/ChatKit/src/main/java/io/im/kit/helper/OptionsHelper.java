package io.im.kit.helper;

import android.widget.EditText;
import android.widget.TextView;

import io.im.kit.IMCenter;

/**
 * author : JFZ
 * date : 2024/1/31 13:51
 * description :
 */
public class OptionsHelper {

    public static void updateTextSize(TextView textView, int oldSize) {
        float type = IMCenter.getInstance().getOptions().getFontSize().getType();
        textView.setTextSize(oldSize * type);
    }

    public static void updateTextSize(EditText textView, int oldSize) {
        float type = IMCenter.getInstance().getOptions().getFontSize().getType();
        textView.setTextSize(oldSize * type);
    }

}
