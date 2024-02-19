package io.im.kit.conversation.extension.component.emoticon;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.LiveData;

/**
 * author : JFZ
 * date : 2023/12/14 14:41
 * description : 表情面板扩展
 */
public interface ChatEmoticonTab {

    /**
     * 表情小图标选中展示，不能为空
     */
    Drawable onTabSelectDrawable(Context context);

    /**
     * 表情小图标未选中展示，不能为空
     */
    Drawable onTabUnSelectDrawable(Context context);


    /**
     * 创建表情面板，不能为空
     */
    View onCreateTabPager(Context context, ViewGroup parent);

    /**
     * 返回 tab 页对应输入框的更新信息
     *
     * @return 输入到 EditText 的 LiveData
     */
    LiveData<String> getEditInfo();

}
