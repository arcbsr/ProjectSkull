package com.hope.main_ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.hope.db_libs.dbmanager.ImageItem
import com.hope.lib_mvvm.adapter.BaseViewBindingAdapter
import com.hope.lib_mvvm.adapter.BaseViewBindingHolder
import com.hope.main_ui.R
import com.hope.main_ui.databinding.ItemImageBinding


class ImageGridAdapter :
    BaseViewBindingAdapter<ImageItem, ImageGridAdapter.ViewHolder>(R.layout.item_image) {


    private lateinit var onEventClickListener: OnEventClickListener
    fun setOnInviteClickListener(onInviteClickListener: OnEventClickListener) {
        this.onEventClickListener = onInviteClickListener
    }

    interface OnEventClickListener {
        fun onEventClickReload(position: Int, item: ImageItem)
        fun onEventClickDelete(position: Int, item: ImageItem)
    }

    override fun convert(holder: ViewHolder, item: ImageItem) {
        item?.let {
            holder.viewBind.apply {
                chatBubble.text = it.details
                if (it.imageUrl.isNullOrEmpty()) {
                    imageCard.visibility = View.GONE
                    layoutAction.visibility = View.VISIBLE
                } else {
                    imageCard.visibility = View.VISIBLE
                    layoutAction.visibility = View.GONE
                    Glide
                        .with(context)
                        .load(it.imageUrl)
                        .centerCrop()
                        .placeholder(com.hope.resources.R.drawable.im_placeholder)
                        .into(imageItem)
                }
                Glide
                    .with(context)
                    .load(it.localPath)
                    .centerCrop()
                    .placeholder(com.hope.resources.R.drawable.ic_profile)
                    .into(chatBubbleIcon)
                imReload.setOnClickListener {
                    onEventClickListener.onEventClickReload(holder.layoutPosition, item)
                }
                imDelete.setOnClickListener {
                    onEventClickListener.onEventClickDelete(holder.layoutPosition, item)
                }
            }
        }
    }

    override fun getViewBinding(
        viewType: Int,
        from: LayoutInflater?,
        parent: ViewGroup?
    ): ViewHolder {
        return ViewHolder(
            ItemImageBinding.inflate(
                from!!, parent, false
            )
        )
    }

    inner class ViewHolder(viewBind: ItemImageBinding) :
        BaseViewBindingHolder<ItemImageBinding>(viewBind)
}