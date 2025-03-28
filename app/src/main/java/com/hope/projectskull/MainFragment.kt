package com.hope.projectskull


import com.hope.projectskull.databinding.LayoutMainfragmentBinding
import com.hope.projectskull.mvvm.base.fragment.BaseFragment
import com.hope.resources.R

class MainFragment : BaseFragment<MainViewModel, LayoutMainfragmentBinding>() {
    override fun showLoading(message: String) {
        mDatabind.textView.text = message
    }

    override fun dismissLoading() {
        mDatabind.textView.text = getString(R.string.test_lib)
    }

    override fun initViews() {
        // Set initial UI state
        mDatabind.textView.text = "Welcome to MainFragment!"
    }

    override fun initObservers() {
        viewModel.simulateLoadingProcess()
    }
}
