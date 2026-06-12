package io.im.uicommon.helper;

import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Px;

import io.im.uicommon.IMCenter;

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


    public static void updateImageSize(View view, @Px int oldSize) {
        updateImageSize(view, oldSize, oldSize);
    }

    public static void updateImageSize(View view, @Px int oldW, @Px int oldH) {
        float type = IMCenter.getInstance().getOptions().getFontSize().getType();
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp != null) {
            float newW = oldW * type;
            float newH = oldH * type;
            lp.width = (int) newW;
            lp.height = (int) newH;
            view.setLayoutParams(lp);
        }
    }
}
