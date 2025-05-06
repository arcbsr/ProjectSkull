package com.hope.main_ui


import android.util.Log
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavCallback
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
                .navigation(context, object : NavCallback() {
                    override fun onFound(postcard: Postcard) {
                        Log.d("Rafiur>>", "Found route")
                    }

                    override fun onLost(postcard: Postcard) {
                        Log.e("Rafiur>>", "Route not found>>" + postcard.toString())
                    }

                    override fun onArrival(postcard: Postcard) {
                        Log.d("Rafiur>>", "Navigation completed")
                    }

                    override fun onInterrupt(postcard: Postcard) {
                        Log.d("Rafiur>>", "Navigation interrupted")
                    }
                })
        }


    }

    override fun initObservers() {
        viewModel.simulateLoadingProcess()

    }
}
