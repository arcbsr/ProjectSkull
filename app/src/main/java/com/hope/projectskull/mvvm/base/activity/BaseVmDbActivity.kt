package com.hope.projectskull.mvvm.base.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import com.hope.projectskull.mvvm.base.viewmodel.BaseViewModel
import java.lang.reflect.ParameterizedType

abstract class BaseVmDbActivity<VM : BaseViewModel, DB : ViewDataBinding> : BaseVmActivity<VM>() {

    protected lateinit var mDatabind: DB

    override fun onCreate(savedInstanceState: Bundle?) {

        // Initialize ViewBinding using reflection
        mDatabind = inflateBindingWithGeneric(layoutInflater)
        setContentView(mDatabind.root)  // Set the root view of the layout

        // Set the lifecycle owner for LiveData binding
        mDatabind.lifecycleOwner = this
        super.onCreate(savedInstanceState)
    }

    /**
     * Inflate ViewBinding using reflection
     */
    @Suppress("UNCHECKED_CAST")
    private fun inflateBindingWithGeneric(inflater: LayoutInflater): DB {
        val superClass = javaClass.genericSuperclass as ParameterizedType
        val clazz = superClass.actualTypeArguments[1] as Class<DB>
        val method = clazz.getMethod("inflate", LayoutInflater::class.java)
        return method.invoke(null, inflater) as DB
    }

}
