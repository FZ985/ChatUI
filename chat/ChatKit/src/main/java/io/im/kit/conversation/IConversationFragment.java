package io.im.kit.conversation;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.im.kit.R;
import io.im.kit.callback.ConversationUserCall;
import io.im.kit.conversation.extension.ConversationHelper;
import io.im.kit.databinding.KitFragmentConversationBinding;
import io.im.kit.model.UiMessage;
import io.im.kit.widget.FixedLinearLayoutManager;
import io.im.kit.widget.adapter.IViewProviderListener;
import io.im.kit.widget.switchpanel.PanelSwitchHelper;
import io.im.kit.widget.switchpanel.interfaces.PanelHeightMeasurer;
import io.im.kit.widget.switchpanel.interfaces.listener.OnPanelChangeListener;
import io.im.kit.widget.switchpanel.view.panel.IPanelView;
import io.im.lib.base.ChatBaseFragment;
import io.im.lib.message.TextMessage;
import io.im.lib.message.UnKnowMessage;
import io.im.lib.model.ConversationType;
import io.im.lib.model.Message;
import io.im.lib.model.MessageContent;
import io.im.lib.model.UserInfo;
import io.im.lib.utils.ChatLibUtil;
import io.im.lib.utils.JLog;

/**
 * author : JFZ
 * date : 2024/1/30 13:02
 * description :
 */
public class IConversationFragment extends ChatBaseFragment implements ConversationUserCall, SwipeRefreshLayout.OnRefreshListener,
        IViewProviderListener<UiMessage> {

    private final String TAG = "IConversationFragment";
    private KitFragmentConversationBinding binding;

    private final ConversationListAdapter adapter = new ConversationListAdapter(this);

    private FixedLinearLayoutManager layoutManager;
    private ConversationType conversationType = ConversationType.PRIVATE;
    private UserInfo userInfo;

    private PanelSwitchHelper mHelper;

    private final ConversationHelper helper = new ConversationHelper();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = KitFragmentConversationBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.refresh.setColorSchemeResources(io.im.lib.R.color.chat_theme);
        binding.refresh.setOnRefreshListener(this);
        layoutManager = new FixedLinearLayoutManager(mActivity);
        layoutManager.setStackFromEnd(true);
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


        test();
    }

    @Override
    public void onRefresh() {
        binding.refresh.postDelayed(() -> binding.refresh.setRefreshing(false), 500);
    }

    @Override
    public void onViewClick(View view, int clickType, UiMessage data) {

    }

    @Override
    public boolean onViewLongClick(View view, int clickType, UiMessage data) {
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mHelper == null) {
            mHelper = new PanelSwitchHelper.Builder(this)
                    .setWindowInsetsRootView(binding.getRoot())
                    //可选
                    .addKeyboardStateListener((visible, height) -> JLog.e(TAG, "系统键盘是否可见 : " + visible + " 高度为：" + height))
                    //可选
                    .addEditTextFocusChangeListener((view, hasFocus) -> {
                        JLog.e(TAG, "输入框是否获得焦点 : " + hasFocus);
                        if (hasFocus) {
//                            scrollToBottom();
                        }
                    })
                    //可选
                    .addViewClickListener(view -> {
//                        switch (view.getId()){
//                            case R.id.edit_text:
//                            case R.id.add_btn:
//                            case R.id.emotion_btn:{
//                                scrollToBottom();
//                            }
//                        }
                        JLog.e(TAG, "点击了View : " + view);
                    })
                    //可选
                    .addPanelChangeListener(new OnPanelChangeListener() {

                        @Override
                        public void onKeyboard() {
                            JLog.e(TAG, "唤起系统输入法");
//                            mBinding.emotionBtn.setSelected(false);
                        }

                        @Override
                        public void onNone() {
                            JLog.e(TAG, "隐藏所有面板");
//                            mBinding.emotionBtn.setSelected(false);
                        }

                        @Override
                        public void onPanel(IPanelView view) {
                            JLog.e(TAG, "唤起面板 : " + view);
//                            if(view instanceof PanelView){
//                                boolean isEmotion = ((PanelView) view).getId() == R.id.panel_emotion;
//                                mBinding.emotionBtn.setSelected(isEmotion);
//                                if (isEmotion) {
//                                    mBinding.panelSwitchLayout.focusAndShowSelection();
//                                }
//                            }
                        }

                        @Override
                        public void onPanelSizeChange(IPanelView panelView, boolean portrait, int oldWidth, int oldHeight, int width, int height) {
//                            if(panelView instanceof PanelView){
//                                switch (((PanelView)panelView).getId()) {
//                                    case R.id.panel_emotion: {
//                                        EmotionPagerView pagerView = mBinding.getRoot().findViewById(R.id.view_pager);
//                                        int viewPagerSize = height - DisplayUtils.dip2px(getContext(), 30f);
//                                        pagerView.buildEmotionViews(
//                                                (PageIndicatorView) mBinding.getRoot().findViewById(R.id.pageIndicatorView),
//                                                mBinding.editText,
//                                                Emotions.getEmotions(), width, viewPagerSize);
//                                        break;
//                                    }
//                                    case R.id.panel_addition: {
//                                        //auto center,nothing to do
//                                        break;
//                                    }
//                                }
//                            }
                        }
                    })
                    .addPanelHeightMeasurer(new PanelHeightMeasurer() {
                        @Override
                        public boolean synchronizeKeyboardHeight() {
                            return false;
                        }

                        @Override
                        public boolean forceUseTargetPanelDefaultHeight() {
                            return true;
                        }

                        @Override
                        public int getTargetPanelDefaultHeight() {
                            return ChatLibUtil.dip2px(mActivity, 380);
                        }

                        @Override
                        public int getPanelTriggerId() {
                            return R.id.emoji;
                        }
                    })
                    .logTrack(true)             //output log
                    .build();
        }
        binding.recycler.setPanelSwitchHelper(mHelper);
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.switchLayout.recycle();
        mHelper = null;
    }

    private void test() {
        List<UiMessage> list = new ArrayList<>();
        list.add(new UiMessage(getSendMsg(TextMessage.obtain("123"))));
        list.add(new UiMessage(getSendMsg(TextMessage.obtain("123"))));
        list.add(new UiMessage(getReceiveMsg(TextMessage.obtain("234"))));
        list.add(new UiMessage(getReceiveMsg(TextMessage.obtain("234"))));
        list.add(new UiMessage(getReceiveMsg(TextMessage.obtain("234"))));
        list.add(new UiMessage(getReceiveMsg(TextMessage.obtain("234"))));
        list.add(new UiMessage(getSendMsg(TextMessage.obtain("123123123123123")).setReadStatus(Message.ReadStatus.READ)));
        list.add(new UiMessage(getReceiveMsg(TextMessage.obtain("223423423423423423434234234234234234234234234234234234234234234234234234234234234234234234"))));
        list.add(new UiMessage(getReceiveMsg(TextMessage.obtain("234234234234234234234234234234234234234234234234234234"))));
        list.add(new UiMessage(new Message(new UnKnowMessage())));
        adapter.setDataCollection(list);
    }


    private Message getSendMsg(MessageContent content) {
        Message msg = new Message(content);
        msg.setMessageDirection(Message.MessageDirection.SEND);
        msg.setMessageTime(System.currentTimeMillis() - (new Random().nextInt(100) + 10) * 60 * 1000);
        return msg;
    }

    private Message getReceiveMsg(MessageContent content) {
        Message msg = new Message(content);
        msg.setMessageDirection(Message.MessageDirection.SEND);
        msg.setMessageTime(System.currentTimeMillis() - (new Random().nextInt(100) + 10) * 60 * 1000);
        return msg;
    }

    private void closeExpand() {
        mHelper.hookSystemBackByPanelSwitcher();
    }

    @Override
    public void updateUser(UserInfo user) {
        this.userInfo = user;
        helper.updateConversationUserCall(this);
    }

    @Override
    public UserInfo getUser() {
        return userInfo;
    }


    public ConversationType getConversationType() {
        return conversationType;
    }
}
