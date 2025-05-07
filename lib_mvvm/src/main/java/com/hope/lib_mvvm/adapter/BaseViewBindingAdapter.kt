package com.hope.lib_mvvm.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder


//https://github.com/CymChad/BaseRecyclerViewAdapterHelper?tab=readme-ov-file

abstract class BaseViewBindingAdapter<T, VH : BaseViewHolder> : BaseQuickAdapter<T, VH> {
    constructor(layoutResId: Int, data: MutableList<T>) : super(layoutResId, data)
    constructor(layoutResId: Int) : super(layoutResId)

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): VH {
        return getViewBinding(viewType, LayoutInflater.from(context), parent)
    }

    protected abstract fun getViewBinding(
        viewType: Int,
        from: LayoutInflater?,
        parent: ViewGroup?
    ): VH

}