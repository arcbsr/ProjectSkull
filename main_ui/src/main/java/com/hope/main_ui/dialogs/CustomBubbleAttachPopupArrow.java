package com.hope.main_ui.dialogs;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hope.main_ui.R;
import com.lxj.xpopup.core.BubbleAttachPopupView;
import com.lxj.xpopup.util.XPopupUtils;

/**
 * Description: 自定义气泡Attach弹窗
 * Create by lxj, at 2019/3/13
 */
public class CustomBubbleAttachPopupArrow extends BubbleAttachPopupView {
    public CustomBubbleAttachPopupArrow(@NonNull Context context, @NonNull String message, OnBubbleClickListener listener) {
        super(context);
        this.message = message;
        this.onBubbleClickListener = listener;
    }

    public interface OnBubbleClickListener {
        void onBubbleClick();
    }

    private OnBubbleClickListener onBubbleClickListener;

    public void setOnBubbleClickListener(OnBubbleClickListener listener) {
        this.onBubbleClickListener = listener;
    }

    private String message;

    @Override
    protected int getImplLayoutId() {
        return R.layout.bubble_popup;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        setBubbleBgColor(getContext().getColor(com.hope.resources.R.color.orange));
        setBubbleShadowSize(XPopupUtils.dp2px(getContext(), 6));
        setBubbleShadowColor(getContext().getColor(com.hope.resources.R.color.orange));
        setArrowWidth(XPopupUtils.dp2px(getContext(), 8));
        setArrowHeight(XPopupUtils.dp2px(getContext(), 9));
//                                .setBubbleRadius(100)
        setArrowRadius(XPopupUtils.dp2px(getContext(), 2));
        final TextView tv = findViewById(R.id.tv_bubble_message);
        tv.setText(message);
        tv.setOnClickListener(v -> {
            if (onBubbleClickListener != null)
                onBubbleClickListener.onBubbleClick();
            dismiss();
        });
    }

}
