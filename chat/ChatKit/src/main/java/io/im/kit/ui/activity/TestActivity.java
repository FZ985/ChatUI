package io.im.kit.ui.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import io.im.kit.IMCenter;
import io.im.kit.config.FontSize;
import io.im.kit.databinding.ZTestBinding;
import io.im.lib.base.ChatBaseActivity;

/**
 * author : JFZ
 * date : 2024/2/19 09:02
 * description :
 */
public class TestActivity extends ChatBaseActivity {

    private ZTestBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ZTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.normal.setOnClickListener(v -> {
            IMCenter.getInstance().getOptions().setFontSize(FontSize.None);
        });
        binding.small.setOnClickListener(v -> {
            IMCenter.getInstance().getOptions().setFontSize(FontSize.Small);
        });
        binding.big.setOnClickListener(v -> {
            IMCenter.getInstance().getOptions().setFontSize(FontSize.Large);
        });
        binding.max.setOnClickListener(v -> {
            IMCenter.getInstance().getOptions().setFontSize(FontSize.Largest);
        });
    }
}
