package com.hope.main_ui.fragments


import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import com.alibaba.android.arouter.launcher.ARouter
import com.hope.lib_mvvm.MainViewModel
import com.hope.lib_mvvm.fragment.BaseFragment
import com.hope.main_ui.R
import com.hope.main_ui.adapters.User
import com.hope.main_ui.adapters.UserAdapter
import com.hope.main_ui.databinding.LayoutMainfragmentBinding
import com.hope.main_ui.databinding.TabBarLayoutBinding

class MainFragment : BaseFragment<MainViewModel, LayoutMainfragmentBinding>() {
    private val demoUsers = listOf(
        User("Alice Johnson", "alice@example.com"),
        User("Bob Smith", "bob.smith@gmail.com"),
        User("Carol Lee", "carol.lee@yahoo.com"),
        User("David Kim", "davidk@domain.com"),
        User("Eva Green", "eva.green@mail.com")
    )
    private val adapter = UserAdapter()
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
        val headView: View = LayoutInflater.from(context).inflate(R.layout.tab_bar_layout, null)

        mDatabind.recyclerView.adapter = adapter
        adapter.setNewInstance(demoUsers.toMutableList())
//        adapter.addFooterView(headView,-1, LinearLayout.VERTICAL)

    }

    override fun initObservers() {
//        viewModel.simulateLoadingProcess()

    }
}
