package io.im.kit.chat;

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

import io.im.kit.IMCenter;
import io.im.kit.MessageOperate;
import io.im.kit.R;
import io.im.kit.chat.extension.ChatExtCall;
import io.im.kit.chat.extension.ChatMessageViewModel;
import io.im.kit.databinding.ChatFragmentChatBinding;
import io.im.kit.event.PageEvent;
import io.im.kit.event.uievent.RefreshEvent;
import io.im.kit.event.uievent.ScrollToEndEvent;
import io.im.kit.factory.ChatPopActionFactory;
import io.im.kit.helper.IChatHelper;
import io.im.kit.model.UiMessage;
import io.im.kit.ui.popmenu.IChatPopMenuClickListener;
import io.im.kit.utils.RouteUtil;
import io.im.kit.widget.FixedLinearLayoutManager;
import io.im.kit.widget.adapter.IViewProviderListener;
import io.im.kit.widget.text.selection.SelectableTextHelper;
import io.im.lib.base.ChatBaseFragment;
import io.im.lib.helper.ChatMsgCache;
import io.im.lib.model.ConversationType;
import io.im.lib.model.Message;
import io.im.lib.model.UserInfo;
import io.im.lib.utils.ChatLibUtil;

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
            IChatPopMenuClickListener listener = IMCenter.getInstance().getOptions().popMenuClickListener;
            if (listener != null && listener.onCopy(text)) {
                return true;
            }
            MessageOperate.copyText(text, true);
            return true;
        }

        @Override
        public boolean onDelete(Message messageInfo) {
            IChatPopMenuClickListener listener = IMCenter.getInstance().getOptions().popMenuClickListener;
            if (listener != null && listener.onDelete(messageInfo)) {
                return true;
            }
            MessageOperate.deleteMessage(messageInfo, null);
            return true;
        }

        @Override
        public boolean onReply(Message messageInfo) {
            IChatPopMenuClickListener listener = IMCenter.getInstance().getOptions().popMenuClickListener;
            if (listener != null && listener.onReply(messageInfo)) {
                return true;
            }
            helper.setReplyMessage(messageInfo, -1);
            return true;
        }

        @Override
        public boolean onMultiSelected(Message messageInfo) {
            IChatPopMenuClickListener listener = IMCenter.getInstance().getOptions().popMenuClickListener;
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
        binding.refresh.setColorSchemeResources(io.im.lib.R.color.chat_theme);
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

        userInfo = (UserInfo) requireActivity().getIntent().getSerializableExtra(RouteUtil.User);
        conversationType = ConversationType.setValue(requireActivity().getIntent().getIntExtra(RouteUtil.ConversationType, ConversationType.PRIVATE.getValue()));
        helper.bindConversation(mActivity, this);
        messageViewModel = new ViewModelProvider(this).get(ChatMessageViewModel.class);
        messageViewModel.bindConversation(this);

        checkMultiSelectView();
        IMCenter.getInstance().getOptions().getFontSizeLiveData().observe(getViewLifecycleOwner(), fontSize -> {
            if (!isDetached() && fontSize != null) {
                binding.conversationToolbar.updateTitleSize();
            }
        });

        liveDataListener();
        scrollToBottom(-1);
        if (IMCenter.getInstance().getOptions().chatPopMenu != null) {
            ChatPopActionFactory.getInstance().setChatPopMenu(IMCenter.getInstance().getOptions().chatPopMenu);
        }
        ChatPopActionFactory.getInstance().setActionListener(menuClickListener);

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
        int count = ChatMsgCache.getMessageCount();
        if (count > 0 || messageViewModel.isEdit()) {
            binding.conversationToolbar.setTitleName(getString(R.string.chat_message_select_count, String.valueOf(count)));
            binding.conversationToolbar.setLeftIcon(io.im.lib.R.drawable.chat_skin_close_black);
            binding.conversationToolbar.setLeftOnclick(v -> messageViewModel.setEdit(false));
        } else {
            if (userInfo != null) {
                binding.conversationToolbar.setTitleName(userInfo.getUserName());
            }
            binding.conversationToolbar.setLeftOnclick(v -> mActivity.onBackPressed());
            binding.conversationToolbar.setLeftIcon(io.im.lib.R.drawable.chat_skin_arrow_left_black);
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
