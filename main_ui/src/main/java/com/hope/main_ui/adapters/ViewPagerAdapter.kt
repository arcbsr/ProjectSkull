package com.hope.main_ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hope.main_ui.routers.RouterUtils

class ViewPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val titles: List<String>
) : FragmentStateAdapter(fragmentActivity) {

    private val searchQuery = arrayOf("Love", "Life", "Song", "Action")

    // Track fragments by position
    private val fragmentMap = mutableMapOf<Int, Fragment>()

    override fun getItemCount(): Int = titles.size

    override fun createFragment(position: Int): Fragment {
        val fragment = when (position) {
            0 -> RouterUtils().navigateToHomeFragment(query = searchQuery[position])
            1 -> RouterUtils().navigateToPreviousChat(query = searchQuery[position])
            2 -> RouterUtils().navigateToHomeBackUpFragment(query = searchQuery[position])
            3 -> RouterUtils().navigateToHistoryFragment(query = searchQuery[position])
            else -> RouterUtils().navigateToHomeFragment(query = searchQuery[0])
        }

        fragmentMap[position] = fragment
        return fragment
    }

    // Expose a method to get fragment by position
    fun getFragmentAt(position: Int): Fragment? = fragmentMap[position]
    fun getAllFragments(): List<Fragment> =
        (0 until itemCount).mapNotNull { fragmentMap[it] }
}
