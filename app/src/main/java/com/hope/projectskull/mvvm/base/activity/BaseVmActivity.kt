package com.hope.projectskull.mvvm.base.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hope.projectskull.mvvm.base.viewmodel.BaseViewModel
import java.lang.reflect.ParameterizedType

abstract class BaseVmActivity<VM : BaseViewModel> : AppCompatActivity() {

    private lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[getViewModelClass()]

        initObservers()
        initViews()
        registerUiChange()
    }
    private fun registerUiChange() {
        viewModel.loadingChange.showDialog.observe(this, Observer {
            showLoading(it)
        })
        viewModel.loadingChange.dismissDialog.observe(this, Observer {
            dismissLoading()
        })
    }
    /**
     * Initialize observers for LiveData
     */
    open fun initObservers() {}

    /**
     * Initialize views
     */
    abstract fun initViews()

    abstract fun showLoading(message: String = "Loading...")
    abstract fun dismissLoading()
    /**
     * Get ViewModel class type using reflection
     */
    @Suppress("UNCHECKED_CAST")
    private fun getViewModelClass(): Class<VM> {
        return (javaClass.genericSuperclass as ParameterizedType)
            .actualTypeArguments[0] as Class<VM>
    }
}
