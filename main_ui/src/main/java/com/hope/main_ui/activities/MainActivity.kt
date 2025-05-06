package com.hope.main_ui.activities

import android.util.Log
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.hope.common.router.RoutePath
import com.hope.lib_mvvm.MainViewModel
import com.hope.lib_mvvm.activity.BaseVmDbActivity
import com.hope.main_ui.R
import com.hope.main_ui.databinding.LayoutMainactivityBinding
import com.hope.main_ui.fragments.MainFragment

@Route(path = "/test/activity")
class MainActivity : BaseVmDbActivity<MainViewModel, LayoutMainactivityBinding>() {


    override fun initViews() {

        Log.w("Rafiur>>>", "MainActivity")
        // Use FragmentTransaction to load MainFragment
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        // Add the fragment to the container (replace if already exists)
        fragmentTransaction.replace(R.id.fragment_container, MainFragment())

        // Commit the transaction
        fragmentTransaction.commit()
        mDatabind.tvMain.text = "MainActivity"
        mDatabind.tvMain.setOnClickListener {
//            ARouter.getInstance().build("/test/secactivity").navigation()
            ARouter.getInstance().build(RoutePath.Home.HOME).navigation(this)

        }
    }

    override fun showLoading(message: String) {

    }

    override fun dismissLoading() {

    }

}