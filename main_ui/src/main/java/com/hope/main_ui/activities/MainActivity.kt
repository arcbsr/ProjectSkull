package com.hope.main_ui.activities

import android.util.Log
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.hope.lib_mvvm.MainViewModel
import com.hope.lib_mvvm.activity.BaseVmDbActivity
import com.hope.main_ui.fragments.MainFragment
import com.hope.main_ui.R
import com.hope.main_ui.databinding.LayoutMainactivityBinding

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
            ARouter.getInstance().build("/test/mainactivity2").navigation(this, object : NavCallback() {
                override fun onFound(postcard: Postcard) {
                    Log.d("Rafiur>>", "Found route")
                }

                override fun onLost(postcard: Postcard) {
                    Log.e("Rafiur>>", "Route not found>>$postcard")
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

    override fun showLoading(message: String) {

    }

    override fun dismissLoading() {

    }

}