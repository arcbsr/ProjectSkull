package com.hope.lib_mvvm.adapter

import androidx.viewbinding.ViewBinding
import com.chad.library.adapter.base.viewholder.BaseViewHolder

open class BaseViewBindingHolder<VB : ViewBinding>(var viewBind: VB) : BaseViewHolder(
    viewBind.root
)