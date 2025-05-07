package com.hope.main_ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hope.lib_mvvm.adapter.BaseViewBindingAdapter
import com.hope.lib_mvvm.adapter.BaseViewBindingHolder
import com.hope.main_ui.R
import com.hope.main_ui.databinding.ItemUserBinding

data class User(val name: String, val email: String)

class UserAdapter :
    BaseViewBindingAdapter<User, UserAdapter.ViewHolder> {


    constructor() : super(R.layout.item_user) {}

//    private lateinit var onInviteClickListener: OnInviteClickListener
//    fun setOnInviteClickListener(onInviteClickListener: OnInviteClickListener) {
//        this.onInviteClickListener = onInviteClickListener
//    }
//
//    interface OnInviteClickListener {
//        fun onInviteClick(position: Int, type: Int)
//    }

    override fun convert(holder: ViewHolder, item: User) {
        item?.let {
            holder.viewBind.apply {
                userName.text = it.name
                userEmail.text = it.email
            }
        }
    }

    override fun getViewBinding(
        viewType: Int,
        from: LayoutInflater?,
        parent: ViewGroup?
    ): ViewHolder {
        return ViewHolder(
            ItemUserBinding.inflate(
                from!!, parent, false
            )
        )
    }

    inner class ViewHolder(viewBind: ItemUserBinding) :
        BaseViewBindingHolder<ItemUserBinding>(viewBind)
}