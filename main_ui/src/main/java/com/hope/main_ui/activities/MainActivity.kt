package com.hope.main_ui.activities

import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.hope.common.router.RoutePath
import com.hope.lib_mvvm.MainViewModel
import com.hope.lib_mvvm.activity.BaseVmDbActivity
import com.hope.main_ui.R
import com.hope.main_ui.adapters.ViewPagerAdapter
import com.hope.main_ui.databinding.LayoutMainactivityBinding
import java.util.Calendar

@Route(path = "/test/activity")
class MainActivity : BaseVmDbActivity<MainViewModel, LayoutMainactivityBinding>() {
    private lateinit var tabLayouts: List<LinearLayout>
    private lateinit var tabIcons: List<AppCompatImageView>
    private lateinit var tabTexts: List<TextView>

    private fun setupTabs() {
        tabLayouts = listOf(
            findViewById<LinearLayout>(R.id.tab_home),
            findViewById<LinearLayout>(R.id.tab_recent),
            findViewById<LinearLayout>(R.id.tab_profile),
            findViewById<LinearLayout>(R.id.tab_settings)
        )

        tabIcons = listOf(
            findViewById<LinearLayout>(R.id.tab_home).getChildAt(0) as AppCompatImageView,
            findViewById<LinearLayout>(R.id.tab_recent).getChildAt(0) as AppCompatImageView,
            findViewById<LinearLayout>(R.id.tab_profile).getChildAt(0) as AppCompatImageView,
            findViewById<LinearLayout>(R.id.tab_settings).getChildAt(0) as AppCompatImageView
        )

        tabTexts = listOf(
            findViewById<LinearLayout>(R.id.tab_home).getChildAt(1) as TextView,
            findViewById<LinearLayout>(R.id.tab_recent).getChildAt(1) as TextView,
            findViewById<LinearLayout>(R.id.tab_profile).getChildAt(1) as TextView,
            findViewById<LinearLayout>(R.id.tab_settings).getChildAt(1) as TextView
        )

        tabLayouts.forEachIndexed { index, layout ->
            layout.setOnClickListener {
                mDatabind.viewPager.currentItem = index
            }
        }

        mDatabind.viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                highlightTab(position)
            }
        })

        highlightTab(0) // default
    }

    private fun highlightTab(selectedIndex: Int) {
        tabIcons.forEachIndexed { index, imageView ->
            imageView.setColorFilter(
                ContextCompat.getColor(
                    this,
                    if (index == selectedIndex) R.color.red else R.color.gray
                )
            )
        }

        tabTexts.forEachIndexed { index, textView ->
            textView.setTextColor(
                ContextCompat.getColor(
                    this,
                    if (index == selectedIndex) R.color.red else R.color.gray
                )
            )
        }
    }


    override fun initViews() {
        val titles = listOf("Home", "History", "Profile", "Settings")

        Log.w("Rafiur>>>", "MainActivity")
//        // Use FragmentTransaction to load MainFragment
//        val fragmentTransaction = supportFragmentManager.beginTransaction()
//
//        // Add the fragment to the container (replace if already exists)
//        fragmentTransaction.replace(R.id.fragment_container, MainFragment())
//
//        // Commit the transaction
//        fragmentTransaction.commit()

        mDatabind.viewPager.adapter = ViewPagerAdapter(this, titles)
        setGreetingMessage()
        mDatabind.tvMainHello.setOnClickListener {
//            ARouter.getInstance().build("/test/secactivity").navigation()
            ARouter.getInstance().build(RoutePath.Home.HOME).navigation(this)

        }
        setupTabs()
    }
    private fun setGreetingMessage() {
        val calendar = Calendar.getInstance()

        val greeting = when (calendar.get(Calendar.HOUR_OF_DAY)) {
            in 0..4 -> "Good Night \uD83C\uDF19"
            in 5..11 -> "Good Morning \u2600️"
            in 12..16 -> "Good Afternoon \uD83C\uDF24️"
            in 17..20 -> "Good Evening \uD83C\uDF06"
            else -> "Good Night \uD83C\uDF19"
        }

        mDatabind.tvMainHello.text = greeting
    }

    override fun showLoading(message: String) {

    }

    override fun dismissLoading() {

    }

}