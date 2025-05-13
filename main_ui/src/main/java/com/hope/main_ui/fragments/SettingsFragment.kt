package com.hope.main_ui.fragments


import com.alibaba.android.arouter.facade.annotation.Route
import com.hope.lib_mvvm.MainViewModel
import com.hope.lib_mvvm.fragment.BaseFragment
import com.hope.main_ui.databinding.LayoutMainfragmentBinding
import com.hope.main_ui.routers.RoutePath
import dagger.hilt.android.AndroidEntryPoint

@Route(path = RoutePath.HomeFragment.SETTINGS)
@AndroidEntryPoint
class SettingsFragment :
    BaseFragment<MainViewModel, LayoutMainfragmentBinding>() {


    override fun showLoading(message: String) {
    }

    override fun dismissLoading() {
    }

    override fun initViews() {

    }

    override fun initObservers() {

    }
}
