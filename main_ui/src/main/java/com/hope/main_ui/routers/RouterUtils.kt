package com.hope.main_ui.routers

import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.launcher.ARouter

class RouterUtils {


    fun navigateToHomeFragment(query: String): Fragment {
        return ARouter.getInstance().build(RoutePath.HomeFragment.HOME)
            .withString("searchQuery", query)
            .navigation() as Fragment
    }
    fun navigateToPreviousChat(query: String): Fragment {
        return ARouter.getInstance().build(RoutePath.HomeFragment.PREVOUS_CHAT)
            .withString("searchQuery", query)
            .navigation() as Fragment
    }

    fun navigateToHistoryFragment(query: String): Fragment {
        return ARouter.getInstance().build(RoutePath.HomeFragment.HISTORY)
            .withString("searchQuery", query)
            .navigation() as Fragment
    }
}