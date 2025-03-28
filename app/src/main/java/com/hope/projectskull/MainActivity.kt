package com.hope.projectskull

import com.hope.projectskull.databinding.LayoutMainactivityBinding
import com.hope.projectskull.mvvm.base.activity.BaseVmDbActivity

class MainActivity : BaseVmDbActivity<MainViewModel, LayoutMainactivityBinding>() {


    override fun initViews() {

        // Use FragmentTransaction to load MainFragment
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        // Add the fragment to the container (replace if already exists)
        fragmentTransaction.replace(R.id.fragment_container, MainFragment())

        // Commit the transaction
        fragmentTransaction.commit()
    }

    override fun showLoading(message: String) {

    }

    override fun dismissLoading() {

    }

}