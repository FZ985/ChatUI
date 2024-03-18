package io.im.kit.config;

import java.util.ArrayList;
import java.util.List;

import io.im.kit.conversation.extension.component.plugins.ChatPluginModule;
import io.im.kit.conversation.extension.component.plugins.impl.CameraPlugin;
import io.im.kit.conversation.extension.component.plugins.impl.CardPlugin;
import io.im.kit.conversation.extension.component.plugins.impl.CollectPlugin;
import io.im.kit.conversation.extension.component.plugins.impl.FilePlugin;
import io.im.kit.conversation.extension.component.plugins.impl.ImagePlugin;
import io.im.kit.conversation.extension.component.plugins.impl.LocationPlugin;
import io.im.kit.conversation.extension.component.plugins.impl.MusicPlugin;
import io.im.kit.conversation.extension.component.plugins.impl.RedPacketPlugin;
import io.im.kit.conversation.extension.component.plugins.impl.TransferPlugin;
import io.im.kit.conversation.extension.component.plugins.impl.VideoCallPlugin;
import io.im.kit.conversation.extension.component.plugins.impl.VoiceInputPlugin;
import io.im.lib.model.UserInfo;

/**
 * by JFZ
 * 2024/3/16
 * desc：插件配置
 **/
public class ChatPluginConfig {

    private final List<ChatPluginModule> defaultPluginModules = new ArrayList<>();
    private ChatPluginManager manager;

    public ChatPluginConfig() {
        defaultPluginModules.add(new ImagePlugin());
        defaultPluginModules.add(new CameraPlugin());
        defaultPluginModules.add(new VideoCallPlugin());
        defaultPluginModules.add(new LocationPlugin());
        defaultPluginModules.add(new RedPacketPlugin());
        defaultPluginModules.add(new TransferPlugin());
        defaultPluginModules.add(new VoiceInputPlugin());
        defaultPluginModules.add(new CollectPlugin());
        defaultPluginModules.add(new CardPlugin());
        defaultPluginModules.add(new FilePlugin());
        defaultPluginModules.add(new MusicPlugin());
    }

    public List<ChatPluginModule> getPluginModules(UserInfo userCall) {
        if (manager != null) {
            List<ChatPluginModule> pluginModules = manager.getPluginModules(userCall, defaultPluginModules);
            if (pluginModules != null) {
                return pluginModules;
            }
        }
        return defaultPluginModules;
    }

    public void setChatPluginManager(ChatPluginManager manager) {
        this.manager = manager;
    }

    interface ChatPluginManager {
        List<ChatPluginModule> getPluginModules(UserInfo user, List<ChatPluginModule> plugins);
    }

}
