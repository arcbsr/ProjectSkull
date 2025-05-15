package com.hope.main_ui.views


import android.content.Context
import com.hope.common.log.Log
import com.hope.main_ui.R
import com.lxj.xpopup.core.ImageViewerPopupView


/**
 * Description: 自定义大图浏览弹窗
 * Create by dance, at 2019/5/8
 */
class CustomImageViewerPopup(context: Context) :
    ImageViewerPopupView(context) {
    override fun getImplLayoutId(): Int {
        return R.layout.custom_image_viewer_popup
    }

    override fun onCreate() {
        super.onCreate()
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