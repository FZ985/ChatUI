package io.im.kit.ui.activity;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import io.im.kit.IMCenter;
import io.im.kit.config.enums.FontSize;
import io.im.kit.databinding.ZTestBinding;
import io.im.lib.base.ChatBaseActivity;

/**
 * author : JFZ
 * date : 2024/2/19 09:02
 * description :
 */
public class TestActivity extends ChatBaseActivity<ZTestBinding> {


    @Override
    public void onInitPage(@Nullable Bundle savedInstanceState) {
        getBinding().normal.setOnClickListener(v -> {
            IMCenter.getInstance().getOptions().setFontSize(FontSize.None);
        });
        getBinding().small.setOnClickListener(v -> {
            IMCenter.getInstance().getOptions().setFontSize(FontSize.Small);
        });
        getBinding().big.setOnClickListener(v -> {
            IMCenter.getInstance().getOptions().setFontSize(FontSize.Large);
        });
        getBinding().max.setOnClickListener(v -> {
            IMCenter.getInstance().getOptions().setFontSize(FontSize.Largest);
        });
    }

    @NonNull
    @Override
    public ZTestBinding getBinding(@NotNull LayoutInflater inflater) {
        return ZTestBinding.inflate(inflater);
    }
}


