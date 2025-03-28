package com.hope.projectskull.mvvm.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hope.projectskull.mvvm.base.viewmodel.BaseViewModel
import java.lang.reflect.ParameterizedType

abstract class BaseVmDbFragment<VM : BaseViewModel, DB : ViewDataBinding> : Fragment() {

    protected lateinit var viewModel: VM
    protected lateinit var mDatabind: DB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize ViewModel
        viewModel = ViewModelProvider(this).get(getViewModelClass())
    }

    /**
     * Inflate the ViewBinding for this fragment using reflection
     */
    @Suppress("UNCHECKED_CAST")
    protected fun inflateBindingWithGeneric(inflater: LayoutInflater): DB {
        val superClass = javaClass.genericSuperclass as ParameterizedType
        val clazz = superClass.actualTypeArguments[1] as Class<DB>
        val method = clazz.getMethod("inflate", LayoutInflater::class.java)
        return method.invoke(null, inflater) as DB
    }

    /**
     * Get ViewModel class type using reflection
     */
    @Suppress("UNCHECKED_CAST")
    private fun getViewModelClass(): Class<VM> {
        return (javaClass.genericSuperclass as ParameterizedType)
            .actualTypeArguments[0] as Class<VM>
    }
}
