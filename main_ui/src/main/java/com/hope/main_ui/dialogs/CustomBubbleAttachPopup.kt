package com.hope.main_ui.dialogs

import android.content.Context
import android.widget.TextView
import com.hope.main_ui.R
import com.lxj.xpopup.core.AttachPopupView

class CustomBubbleAttachPopup(context: Context) : AttachPopupView(context) {

    private lateinit var messageTextView: TextView

    var bubbleText: String = "Hello, I'm an AI Agent!"
        set(value) {
            field = value
            if (this::messageTextView.isInitialized) {
                messageTextView.text = value
            }
        }

    override fun getImplLayoutId(): Int = R.layout.bubble_popup

    override fun onCreate() {
        super.onCreate()
        messageTextView = findViewById(R.id.tv_bubble_message)
        messageTextView.text = bubbleText
    }
}
