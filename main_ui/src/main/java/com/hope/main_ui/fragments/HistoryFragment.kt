package com.hope.main_ui.fragments


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.hope.main_ui.routers.RoutePath
import com.hope.lib_mvvm.fragment.BaseFragment
import com.hope.main_ui.R
import com.hope.main_ui.adapters.CustomBaseQuickAdapter
import com.hope.main_ui.adapters.MovieAdapter
import com.hope.main_ui.databinding.LayoutHistoryfragmentBinding
import com.hope.main_ui.databinding.LayoutMainfragmentBinding
import com.hope.main_ui.viewmodels.MovieListViewModel
import com.hope.main_ui.viewmodels.MovieState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@Route(path = RoutePath.HomeFragment.HISTORY)
@AndroidEntryPoint
class HistoryFragment :
    BaseFragment<MovieListViewModel, LayoutHistoryfragmentBinding>() {

    @Autowired(name = "searchQuery")
    @JvmField
    var searchQuery: String? = null
    private val adapter = MovieAdapter()
    override fun showLoading(message: String) {
        mDatabind.textView.text = message
    }

    override fun dismissLoading() {
    }

    override fun initViews() {
        ARouter.getInstance().inject(this)
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
//        adapter.addFooterView(headView,-1, LinearLayout.VERTICAL)
        val footerView = View(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                resources.getDimensionPixelSize(com.hope.resources.R.dimen.dp_80)
            )
        }
        adapter.addFooterView(footerView)

        searchQuery?.let { viewModel.fetchMovieList(it) }
    }

    override fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.mState.collect { state ->
                    when (state) {
                        is MovieState.Loading -> {
                            adapter.setEmptyView(R.layout.loading_layout_view)
                            Log.d("Rafiur>>>", "Loading...")
                        }
                        is MovieState.Success -> {
                            adapter.setList(state.movies)
//                            adapter.setEmptyView(R.layout.empty_view)
                        }
                        is MovieState.Error -> {
                            Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                        }
                        is MovieState.EndOfSearch -> {
                            Log.d("Rafiur>>>", "End of search")

                        }
                    }
                }
            }
        }

    }
}
