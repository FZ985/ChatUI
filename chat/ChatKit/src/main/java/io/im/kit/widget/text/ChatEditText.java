package io.im.kit.widget.text;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * author : JFZ
 * date : 2023/12/20 14:28
 * description :
 */
public class ChatEditText extends AppCompatEditText {

    private List<TextWatcher> mTextWatcherList;

    private final List<OnFocusChangeListener> focusList = new ArrayList<>();

    public ChatEditText(@NonNull Context context) {
        super(context);
        addTextWatcher();
    }

    public ChatEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        addTextWatcher();
    }

    public ChatEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addTextWatcher();
    }

    @Override
    public void addTextChangedListener(TextWatcher watcher) {
        if (mTextWatcherList == null) {
            mTextWatcherList = new ArrayList<>();
        }
        if (!mTextWatcherList.contains(watcher)) {
            mTextWatcherList.add(watcher);
        }
    }

    @Override
    public void removeTextChangedListener(TextWatcher watcher) {
        if (mTextWatcherList != null) {
            mTextWatcherList.remove(watcher);
        }
    }

    public void setText(CharSequence text, boolean triggerTextChanged) {
        if (!triggerTextChanged) {
            super.removeTextChangedListener(mTextWatcher);
        }
        super.setText(text);
        if (!triggerTextChanged) {
            super.addTextChangedListener(mTextWatcher);
        }
    }

    private final TextWatcher mTextWatcher =
            new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (mTextWatcherList != null) {
                        for (TextWatcher textWatcher : mTextWatcherList) {
                            textWatcher.beforeTextChanged(s, start, count, after);
                        }
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (mTextWatcherList != null) {
                        for (TextWatcher textWatcher : mTextWatcherList) {
                            textWatcher.onTextChanged(s, start, before, count);
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (mTextWatcherList != null) {
                        for (TextWatcher textWatcher : mTextWatcherList) {
                            textWatcher.afterTextChanged(s);
                        }
                    }
                }
            };

    private void addTextWatcher() {
        super.removeTextChangedListener(mTextWatcher);
        super.addTextChangedListener(mTextWatcher);
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener l) {
        if (!focusList.contains(l)) {
            focusList.add(l);
        }
        super.setOnFocusChangeListener(_innerListener);
    }

    private final OnFocusChangeListener _innerListener = (view, b) -> {
        try {
            for (OnFocusChangeListener l : focusList) {
                if (l != null) {
                    l.onFocusChange(view, b);
                }
            }
        } catch (Exception e) {
        }
    };
}
