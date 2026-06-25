package io.im.uicommon.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.im.uicommon.databinding.CommonViewErrorTipBinding;


/**
 * author : JFZ
 * date : 2024/1/9 09:18
 * description :
 */
public class TipErrorView extends FrameLayout {
    private final CommonViewErrorTipBinding binding;

    public TipErrorView(@NonNull Context context) {
        this(context, null);
    }

    public TipErrorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TipErrorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        binding = CommonViewErrorTipBinding.inflate(LayoutInflater.from(context), this, true);
    }

    public void setTipText(String text) {
        if (binding != null) {
            binding.appErrorTipTv.setText(text);
        }
    }
}
