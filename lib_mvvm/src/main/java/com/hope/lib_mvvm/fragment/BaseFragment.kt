package com.hope.lib_mvvm.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.hope.lib_mvvm.viewmodel.BaseViewModel

abstract class BaseFragment<VM : BaseViewModel, DB : ViewDataBinding> : BaseVmDbFragment<VM, DB>() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize the ViewBinding for this fragment
        mDatabind = inflateBindingWithGeneric(inflater)
        return mDatabind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the lifecycle owner for ViewBinding
        mDatabind.lifecycleOwner = viewLifecycleOwner

        // Initialize views and observers
        initViews()
        initObservers()
        registerUiChange()
    }

    abstract fun showLoading(message: String = "Loading...")
    abstract fun dismissLoading()
    private fun registerUiChange() {
        viewModel.loadingChange.showDialog.observe(this, Observer {
            showLoading(it)
        })
        viewModel.loadingChange.dismissDialog.observe(this, Observer {
            dismissLoading()
        })
    }

    /**
     * Initialize Views in the Fragment.
     * This method should be implemented by child classes.
     */
    abstract fun initViews()

    /**
     * Initialize LiveData observers in the Fragment.
     * This method should be implemented by child classes if necessary.
     */
    open fun initObservers() {}
}
