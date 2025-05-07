package com.hope.main_ui.fragments


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.launcher.ARouter
import com.hope.lib_mvvm.fragment.BaseFragment
import com.hope.main_ui.R
import com.hope.main_ui.adapters.MovieAdapter
import com.hope.main_ui.databinding.LayoutMainfragmentBinding
import com.hope.main_ui.viewmodels.MovieListViewModel
import com.hope.main_ui.viewmodels.MovieState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment(private val searchQuery: String) :
    BaseFragment<MovieListViewModel, LayoutMainfragmentBinding>() {
    private val adapter = MovieAdapter()
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
//        adapter.addFooterView(headView,-1, LinearLayout.VERTICAL)
        val footerView = View(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                resources.getDimensionPixelSize(com.hope.resources.R.dimen.dp_80)
            )
        }
        adapter.addFooterView(footerView)


        viewModel.fetchMovieList(searchQuery)
    }

    override fun initObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.mState.collect { state ->
                when (state) {
                    is MovieState.Loading -> {
                        // Show loading UI
                    }

                    is MovieState.Success -> {
                        val movies = state.movies
                        Log.w("Rafiur>>", movies.toString())
                        adapter.setNewInstance(movies.toMutableList())
                    }

                    is MovieState.Error -> {
                        // Show error message
                        Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                    }

                    is MovieState.EndOfSearch -> {
                        // No more data
                    }
                }
            }
        }
    }
}
