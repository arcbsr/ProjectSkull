package com.hope.main_ui.fragments

import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.hope.common.log.Log
import com.hope.db_libs.dbmanager.DatabaseManager
import com.hope.db_libs.dbmanager.ImageItem
import com.hope.lib_mvvm.fragment.BaseFragment
import com.hope.main_ui.adapters.GridSpacingItemDecoration
import com.hope.main_ui.adapters.ImageGridAdapter
import com.hope.main_ui.databinding.LayoutHistoryfragmentBinding
import com.hope.main_ui.routers.RoutePath
import com.hope.main_ui.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@Route(path = RoutePath.HomeFragment.PREVIOUS_CHAT)
@AndroidEntryPoint
class PreviousChatFragment : BaseFragment<HomeViewModel, LayoutHistoryfragmentBinding>() {

    @Autowired(name = "searchQuery")
    @JvmField
    var searchQuery: String? = null

    private val adapter = ImageGridAdapter()

    override fun showLoading(message: String) {}
    override fun dismissLoading() {}

    override fun initViews() {
        ARouter.getInstance().inject(this)
        setupRecyclerView()
        loadInitialData()
        setupListeners()
    }

    private fun setupRecyclerView() {
        mDatabind.recyclerView.adapter = adapter
        val spacingInPx = (16 * resources.displayMetrics.density).toInt()
        mDatabind.recyclerView.addItemDecoration(
            GridSpacingItemDecoration(spanCount = 2, spacing = spacingInPx, includeEdge = true)
        )

        adapter.setOnInviteClickListener(object : ImageGridAdapter.OnEventClickListener {
            override fun onEventClickReload(position: Int, item: ImageItem) {
                Log.d("MainFragment", "Image clicked: $item")
            }

            override fun onEventClickDelete(position: Int, item: ImageItem) {
                lifecycleScope.launch {
                    DatabaseManager.imageItemDao().deleteById(item.id)
                    loadInitialData()
                }
            }

            override fun onEventClick(position: Int, item: ImageItem) {
            }
        })
    }

    private fun loadInitialData() {
        lifecycleScope.launch {
            val updatedList = DatabaseManager.imageItemDao().getAllData()
            adapter.setNewInstance(updatedList.toMutableList())
        }
    }

    private fun setupListeners() {

    }


    override fun initObservers() {

    }
}
