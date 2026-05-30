package io.chat.kit.chat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

import io.chat.kit.provider.ChatProvider;
import io.im.uicommon.IMCenter;
import io.im.uicommon.MessageOperate;
import io.chat.kit.R;
import io.chat.kit.chat.extension.ChatExtCall;
import io.chat.kit.chat.messagelist.viewmodel.ChatMessageViewModel;
import io.chat.kit.databinding.ChatFragmentChatBinding;
import io.chat.kit.event.PageEvent;
import io.chat.kit.event.RefreshEvent;
import io.chat.kit.event.ScrollToEndEvent;
import io.chat.kit.factory.ChatPopActionFactory;
import io.chat.kit.helper.IChatHelper;
import io.chat.kit.model.UiMessage;
import io.chat.kit.ui.popmenu.IChatPopMenuClickListener;
import io.chat.kit.ChatRoute;
import io.im.uicommon.widgets.FixedLinearLayoutManager;
import io.im.uicommon.adapter.IViewProviderListener;
import io.im.uicommon.widgets.text.selection.SelectableTextHelper;
import io.im.uicommon.base.ChatBaseFragment;
import io.im.uicommon.helper.ChatMsgCache;
import io.im.core.model.ConversationType;
import io.im.core.model.Message;
import io.im.core.model.UserInfo;
import io.im.core.utils.ChatLibUtil;

/**
 * author : JFZ
 * date : 2024/1/30 13:02
 * description :
 */
public class IChatFragment extends ChatBaseFragment implements ChatExtCall, SwipeRefreshLayout.OnRefreshListener, IViewProviderListener<UiMessage> {

    private final String TAG = "IChatFragment";
    private String thisFragmentId = "";
    private ChatFragmentChatBinding binding;

    private final IChatListAdapter adapter = new IChatListAdapter(this);

    private ConversationType conversationType = ConversationType.PRIVATE;
    private UserInfo userInfo;

    private final IChatHelper helper = new IChatHelper();

    private ChatMessageViewModel messageViewModel;

    private boolean onScrollStopRefreshList = false;

    private Observer<List<UiMessage>> mListObserver = uiMessages -> refreshList(uiMessages);

    private Observer<PageEvent> mPageObserver = new Observer<PageEvent>() {
        @Override
        public void onChanged(PageEvent pageEvent) {
            if (isDetached()) return;
            if (pageEvent instanceof ScrollToEndEvent) {
                binding.recycler.scrollToPosition(adapter.getItemCount() - 1);
            } else if (pageEvent instanceof RefreshEvent) {
                binding.refresh.setRefreshing(false);
            }
        }
    };

    private final IChatPopMenuClickListener menuClickListener = new IChatPopMenuClickListener() {
        @Override
        public boolean onCopy(String text) {
            IChatPopMenuClickListener listener = ChatProvider.getOptions().popMenuClickListener;
            if (listener != null && listener.onCopy(text)) {
                return true;
            }
            MessageOperate.copyText(text, true);
            return true;
        }

        @Override
        public boolean onDelete(Message messageInfo) {
            IChatPopMenuClickListener listener = ChatProvider.getOptions().popMenuClickListener;
            if (listener != null && listener.onDelete(messageInfo)) {
                return true;
            }
            MessageOperate.deleteMessage(messageInfo, null);
            return true;
        }

        @Override
        public boolean onReply(Message messageInfo) {
            IChatPopMenuClickListener listener = ChatProvider.getOptions().popMenuClickListener;
            if (listener != null && listener.onReply(messageInfo)) {
                return true;
            }
            helper.setReplyMessage(messageInfo, -1);
            return true;
        }

        @Override
        public boolean onMultiSelected(Message messageInfo) {
            IChatPopMenuClickListener listener = ChatProvider.getOptions().popMenuClickListener;
            if (listener != null && listener.onMultiSelected(messageInfo)) {
                return true;
            }
            UiMessage uiMessage = messageViewModel.findUIMessageById(messageInfo.getMessageId());
            if (uiMessage != null) {
                messageViewModel.setEdit(true);
                messageViewModel.checkSelectMessage(uiMessage);
            }
            return true;
        }

        @Override
        public boolean onForward(Message messageInfo) {
            IChatPopMenuClickListener listener = ChatProvider.getOptions().popMenuClickListener;
            if (listener != null && listener.onForward(messageInfo)) {
                return true;
            }
            ChatMsgCache.addMessage(messageInfo);
            ChatRoute.goForwardSelect(mActivity, userInfo, false);
            return true;
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ChatFragmentChatBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        thisFragmentId = (System.currentTimeMillis() + ChatLibUtil.randomNumber(10, 999)) + "";
        binding.refresh.setColorSchemeResources(io.im.core.R.color.chat_theme);
        binding.refresh.setOnRefreshListener(this);
        FixedLinearLayoutManager layoutManager = new FixedLinearLayoutManager(mActivity);
//        layoutManager.setStackFromEnd(true);
        binding.recycler.setLayoutManager(layoutManager);
        SimpleItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setSupportsChangeAnimations(false);
        binding.recycler.setItemAnimator(itemAnimator);
        binding.recycler.setAdapter(adapter);

        GestureDetector gd = new GestureDetector(mActivity, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
                closeExpand();
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public boolean onSingleTapUp(@NonNull MotionEvent e) {
                closeExpand();
                return super.onSingleTapUp(e);
            }
        });

        binding.recycler.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                return gd.onTouchEvent(e);
            }
        });

        binding.recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    SelectableTextHelper.getInstance().hideSelectView();
                } else {
                    SelectableTextHelper.getInstance().resumeSelection();
                }
            }
        });

        userInfo = (UserInfo) requireActivity().getIntent().getSerializableExtra(ChatRoute.User);
        conversationType = ConversationType.setValue(requireActivity().getIntent().getIntExtra(ChatRoute.ConversationType, ConversationType.PRIVATE.getValue()));
        helper.bindConversation(mActivity, this);
        messageViewModel = new ViewModelProvider(this).get(ChatMessageViewModel.class);
        messageViewModel.bindConversation(this);

        IMCenter.getInstance().getOptions().getFontSizeLiveData().observe(getViewLifecycleOwner(), fontSize -> {
            if (!isDetached() && fontSize != null) {
                binding.conversationToolbar.updateTitleSize();
            }
        });

        liveDataListener();
        scrollToBottom(-1);
        if (ChatProvider.getOptions().chatPopMenu != null) {
            ChatPopActionFactory.getInstance().setChatPopMenu(ChatProvider.getOptions().chatPopMenu);
        }
        ChatPopActionFactory.getInstance().setActionListener(menuClickListener);
        uiListener();
    }

    private void uiListener() {
        //逐条转发
        binding.shareMulti.setOnClickListener(v -> {
            ChatRoute.goForwardSelect(mActivity, userInfo, false);
        });

        //和并转发
        binding.shareSingle.setOnClickListener(v -> {
            ChatRoute.goForwardSelect(mActivity, userInfo, true);
        });

        //删除
        binding.shareDelete.setOnClickListener(v -> {
            MessageOperate.deleteMessage(ChatMsgCache.getMessageList(), null);
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void liveDataListener() {
        messageViewModel.getPageEventLiveData().observeForever(mPageObserver);
        messageViewModel.getUiMessageLiveData().observeForever(mListObserver);
        IMCenter.getInstance().getOptions().getFontSizeLiveData().observe(getViewLifecycleOwner(), fontSize -> {
            if (!isDetached() && fontSize != null) {
                binding.inputPanel.updateTextSize();
                messageViewModel.refreshAllMessage();
            }
        });
    }

    private void refreshList(List<UiMessage> data) {
        binding.refresh.setRefreshing(false);
        if (!binding.recycler.isComputingLayout() && binding.recycler.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
            adapter.setDataCollection(data);
        } else {
            onScrollStopRefreshList = true;
        }
    }

    @Override
    public void onRefresh() {
        binding.refresh.postDelayed(() -> binding.refresh.setRefreshing(false), 500);
    }

    @Override
    public void onViewClick(View view, int clickType, int position, UiMessage data) {
        messageViewModel.onViewClick(view, clickType, position, data);
    }

    @Override
    public boolean onViewLongClick(View view, int clickType, int position, UiMessage data) {
        return messageViewModel.onViewLongClick(view, clickType, position, data);
    }

    @Override
    public boolean onTextSelected(View view, int position, UiMessage data, String text, boolean isSelectAll) {
        return messageViewModel.onTextSelected(view, position, data, text, isSelectAll);
    }

    @Override
    public void onResume() {
        super.onResume();
        helper.onResume();
        messageViewModel.onResume();
        checkMultiSelectView();
    }

    @Override
    public void onPause() {
        helper.onPause();
        messageViewModel.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        helper.onStop();
        messageViewModel.onStop();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        helper.onDestroy();
        messageViewModel.onDestroy();
        messageViewModel.getPageEventLiveData().removeObserver(mPageObserver);
        messageViewModel.getUiMessageLiveData().removeObserver(mListObserver);
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        helper.onActivityResult(requestCode, resultCode, data);
        messageViewModel.onActivityResult(requestCode, resultCode, data);
    }

    public void scrollToBottom(int index) {
        if (index == -1) {
            binding.recycler.scrollToPosition(adapter.getItemCount() - 1);
        }
    }

    @Override
    public void checkMultiSelectView() {
        if (messageViewModel.isEdit()) {
            int count = ChatMsgCache.getMessageCount();
            binding.inputPanel.setVisibility(View.GONE);
            binding.multiSelectLl.setVisibility(View.VISIBLE);
            binding.conversationToolbar.setTitleName(getString(R.string.chat_message_select_count, String.valueOf(count)));
            binding.conversationToolbar.setLeftIcon(io.im.core.R.drawable.chat_skin_close_black);
            binding.conversationToolbar.setLeftOnclick(v -> messageViewModel.setEdit(false));
            if (count > 0) {
                binding.shareMulti.setEnabled(true);
                binding.shareMulti.setAlpha(1f);
                binding.shareSingle.setEnabled(true);
                binding.shareSingle.setAlpha(1f);
                binding.shareDelete.setEnabled(true);
                binding.shareDelete.setAlpha(1f);
            } else {
                binding.shareMulti.setAlpha(0.3f);
                binding.shareMulti.setEnabled(false);
                binding.shareSingle.setAlpha(0.3f);
                binding.shareSingle.setEnabled(false);
                binding.shareDelete.setAlpha(0.3f);
                binding.shareDelete.setEnabled(false);
            }
        } else {
            ChatMsgCache.clear();
            binding.multiSelectLl.setVisibility(View.GONE);
            binding.inputPanel.setVisibility(View.VISIBLE);
            if (userInfo != null) {
                binding.conversationToolbar.setTitleName(userInfo.getUserName());
            }
            binding.conversationToolbar.setLeftOnclick(v -> mActivity.onBackPressed());
            binding.conversationToolbar.setLeftIcon(io.im.core.R.drawable.chat_skin_arrow_left_black);
        }
    }

    private void closeExpand() {
        helper.closeExpand();
    }

    @Override
    public void updateUser(UserInfo user) {
        this.userInfo = user;
    }

    @Override
    public UserInfo getUser() {
        return userInfo;
    }

    @Override
    public ConversationType getConversationType() {
        return conversationType;
    }

    public ChatFragmentChatBinding getBinding() {
        return binding;
    }

    @Override
    public int addHeadView(@NonNull View view) {
        return adapter.addHeaderView(view);
    }

    @Override
    public int addFootView(@NonNull View view) {
        return adapter.addFootView(view);
    }

    @Override
    public void removeHeadView(int viewType) {
        adapter.removeHeaderView(viewType);
    }

    @Override
    public void removeFootView(int viewType) {
        adapter.removeFooterView(viewType);
    }

    @Override
    public AppCompatActivity getConversationActivity() {
        return mActivity;
    }

    @Override
    public Intent getConversationIntent() {
        return mActivity.getIntent();
    }

    @Override
    public boolean onBackPressed() {
        if (messageViewModel.onBackPressed()) {
            return true;
        }
        return helper.onBackPressed();
    }

    @Override
    public ChatMessageViewModel getChatMessageViewModel() {
        return messageViewModel;
    }

    @Override
    public IChatHelper getIChatHelper() {
        return helper;
    }
}
