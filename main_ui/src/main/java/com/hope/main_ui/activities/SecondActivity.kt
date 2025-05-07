package com.hope.main_ui.activities

import com.alibaba.android.arouter.facade.annotation.Route
import com.hope.lib_mvvm.MainViewModel
import com.hope.lib_mvvm.activity.BaseVmDbActivity
import com.hope.main_ui.databinding.LayoutMainactivityBinding


@Route(path = "/test/secondactivity")
class SecondActivity : BaseVmDbActivity<MainViewModel, LayoutMainactivityBinding>() {


    override fun initViews() {

//        Log.w("Rafiur>>", "SecondActivity")
//        // Use FragmentTransaction to load MainFragment
//        val fragmentTransaction = supportFragmentManager.beginTransaction()
//
//        // Add the fragment to the container (replace if already exists)
//        fragmentTransaction.replace(R.id.fragment_container, MainFragment())
//
//        // Commit the transaction
//        fragmentTransaction.commit()
        mDatabind.tvMainHello.text = "SecondActivity"
    }

    override fun showLoading(message: String) {

    }

    override fun dismissLoading() {

    }

}