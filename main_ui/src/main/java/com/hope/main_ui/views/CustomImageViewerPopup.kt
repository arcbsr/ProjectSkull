package com.hope.main_ui.views


import android.content.Context
import com.hope.common.log.Log
import com.hope.main_ui.R
import com.lxj.xpopup.core.ImageViewerPopupView

class CustomImageViewerPopup(context: Context) :
    ImageViewerPopupView(context) {
    override fun getImplLayoutId(): Int {
        return R.layout.custom_image_viewer_popup
    }

    override fun onShow() {
        super.onShow()
        Log.e("tag", "CustomImageViewerPopup onShow")
    }

    override fun onDismiss() {
        super.onDismiss()
        Log.e("tag", "CustomImageViewerPopup onDismiss")
    }
}