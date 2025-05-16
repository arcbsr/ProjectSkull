package com.hope.main_ui.dialogs


import android.content.Context
import android.view.View
import android.widget.ProgressBar
import com.hope.main_ui.R
import com.lxj.xpopup.core.BottomPopupView


class ProgressLoadingDialog(
    activity: Context,
) : BottomPopupView(activity) {
    private var close: ((Boolean) -> Unit)? = null
    var position = 0
    override fun getImplLayoutId(): Int {
        return R.layout.progress_dialog
    }

    override fun onCreate() {
        super.onCreate()
        initView()
    }

    var progressBar:ProgressBar? = null
    private fun initView() {
        findViewById<View>(R.id.btn_close).setOnClickListener{
            close?.invoke(true)
            setProgress(30)
//            dismiss()
        }
        progressBar = findViewById<ProgressBar>(R.id.progressBar)
    }
    fun setProgress(progress: Int) {
        progressBar?.progress = progress
    }

}