package com.hope.main_ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.hope.lib_mvvm.adapter.BaseViewBindingAdapter
import com.hope.lib_mvvm.adapter.BaseViewBindingHolder
import com.hope.main_ui.R
import com.hope.main_ui.databinding.ItemUserBinding
import com.hope.model_omdb.models.Movie

data class User(val name: String, val email: String)

class MovieAdapter :
    BaseViewBindingAdapter<Movie, MovieAdapter.ViewHolder> {


    constructor() : super(R.layout.item_user) {}

//    private lateinit var onInviteClickListener: OnInviteClickListener
//    fun setOnInviteClickListener(onInviteClickListener: OnInviteClickListener) {
//        this.onInviteClickListener = onInviteClickListener
//    }
//
//    interface OnInviteClickListener {
//        fun onInviteClick(position: Int, type: Int)
//    }

    override fun convert(holder: ViewHolder, item: Movie) {
        item?.let {
            holder.viewBind.apply {
                userName.text = it.title
                userStatus.text = it.imdbID
                userEmail.text = it.type + " (" + it.year + ")"
                Glide
                    .with(context)
                    .load(it.poster)
                    .centerCrop()
                    .placeholder(0)
                    .into(profileImage);
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