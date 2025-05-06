package com.hope.main_ui.fragments


import com.alibaba.android.arouter.launcher.ARouter
import com.hope.lib_mvvm.MainViewModel
import com.hope.lib_mvvm.fragment.BaseFragment
import com.hope.main_ui.databinding.LayoutMainfragmentBinding

class MainFragment : BaseFragment<MainViewModel, LayoutMainfragmentBinding>() {
    override fun showLoading(message: String) {
        mDatabind.textView.text = message
    }

    override fun dismissLoading() {
    }

    override fun initViews() {
        // Set initial UI state
        mDatabind.textView.text = "Welcome to MainFragment!"
        mDatabind.textView.setOnClickListener {
            // Handle click event
//            startActivity(Intent(requireContext(), SecondActivity::class.java))
//            ARouter.getInstance().build("/test/secactivity").navigation()
            ARouter.getInstance().build("/test/secondactivity")
                .navigation(context)
        }


    }

    override fun initObservers() {
        viewModel.simulateLoadingProcess()

    }
}
