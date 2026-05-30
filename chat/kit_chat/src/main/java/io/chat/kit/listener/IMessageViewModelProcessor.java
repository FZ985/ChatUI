package io.chat.kit.listener;


import io.chat.kit.chat.messagelist.viewmodel.ChatMessageViewModel;
import io.chat.kit.model.UiMessage;

/**
 * by DAD FZ
 * 2026/5/28
 * desc：聊天列表处理接口，当用户需要实现自定义
 **/
public interface IMessageViewModelProcessor {

    boolean onViewClick(ChatMessageViewModel viewModel, int clickType, UiMessage data);

    boolean onViewLongClick(ChatMessageViewModel viewModel, int clickType, UiMessage data);

}
