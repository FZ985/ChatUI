package io.im.kit.ui.popmenu;

import androidx.annotation.NonNull;

import java.util.List;

import io.im.lib.model.Message;
import io.im.lib.model.PluginAction;

public interface IChatPopMenu {

    /**
     * custom actions will show first
     */
    @NonNull
    default List<PluginAction> customizePopMenu(@NonNull List<PluginAction> menuList, @NonNull Message messageBean) {
        return menuList;
    }

    /**
     * false will show default actions true will show custom actions only
     */
    default boolean showDefaultPopMenu() {
        return true;
    }
}
