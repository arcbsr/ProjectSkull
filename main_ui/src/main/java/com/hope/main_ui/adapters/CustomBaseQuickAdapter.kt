package com.hope.main_ui.adapters

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.hope.main_ui.R
import com.hope.models.models.Movie

class CustomBaseQuickAdapter  : BaseQuickAdapter<Movie, BaseViewHolder>(R.layout.item_user) {

    override fun convert(holder: BaseViewHolder, item: Movie) {
        holder.setText(R.id.userName, item.title)
        holder.setText(R.id.userStatus, item.year)

        val poster = holder.getView<ImageView>(R.id.profileImage)
        Glide.with(poster.context)
            .load(item.poster)
            .centerCrop()
            .into(poster)
    }
}