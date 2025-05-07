package com.hope.main_ui.dialogs


import android.content.Context
import com.hope.main_ui.R
import com.lxj.xpopup.core.BottomPopupView


class InviteUserDialog(
    private var activity: Context,
) : BottomPopupView(activity) {

    var position = 0
    override fun getImplLayoutId(): Int {
        return R.layout.tab_bar_layout
    }

    init {

    }

    override fun onCreate() {
        super.onCreate()
    }

}