package com.hope.main_ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hope.main_ui.fragments.MainFragment

class ViewPagerAdapter (
    fragmentActivity: FragmentActivity,
    private val titles: List<String>
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = titles.size

    override fun createFragment(position: Int): Fragment {
    val searchQuery = arrayOf("Love", "Life", "Song", "Action")

        return MainFragment(searchQuery[position])
    }
}