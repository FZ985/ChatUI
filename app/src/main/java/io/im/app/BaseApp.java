package io.im.app;

import android.view.View;
import android.widget.Toast;

import androidx.multidex.MultiDexApplication;

import io.im.kit.IMCenter;
import io.im.kit.config.ChatEmojiConfig;
import io.im.kit.config.Options;

/**
 * author : JFZ
 * date : 2024/1/26 11:15
 * description :
 */
public class BaseApp extends MultiDexApplication {

    private static BaseApp app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        Options options = new Options();
        options.getEmojiConfig().setShowAddButton(true);
        options.getEmojiConfig().setAddButtonClickListener(new ChatEmojiConfig.AddButtonClickListener() {
            @Override
            public void onAddClick(View view, ChatEmojiConfig.RefreshEmojiCall refreshEmoji) {
                Toast.makeText(BaseApp.this, "添加表情", Toast.LENGTH_SHORT).show();
            }
        });
        IMCenter.init(this, options);
    }
}
