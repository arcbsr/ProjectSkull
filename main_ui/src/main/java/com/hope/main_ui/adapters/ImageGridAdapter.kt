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
import com.hope.main_ui.views.CustomImageViewerPopup
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.util.SmartGlideImageLoader


class ImageGridAdapter :
    BaseViewBindingAdapter<ImageItem, ImageGridAdapter.ViewHolder>(R.layout.item_image) {


    private lateinit var onEventClickListener: OnEventClickListener
    fun setOnInviteClickListener(onInviteClickListener: OnEventClickListener) {
        this.onEventClickListener = onInviteClickListener
    }

    interface OnEventClickListener {
        fun onEventClickReload(position: Int, item: ImageItem)
        fun onEventClickDelete(position: Int, item: ImageItem)
        fun onEventClick(position: Int, item: ImageItem)
    }

    override fun convert(holder: ViewHolder, item: ImageItem) {
        item.let {
            holder.viewBind.apply {
                val message = it
                chatBubble.text = it.prompt
                if (it.imageUrl.isNullOrEmpty()) {
                    imageCard.visibility = View.GONE
                    imReload.visibility = View.VISIBLE
                } else {
                    imageCard.visibility = View.VISIBLE
                    imReload.visibility = View.GONE
                    Glide
                        .with(context)
                        .load(it.imageUrl)
                        .centerCrop()
                        .placeholder(com.hope.resources.R.drawable.im_placeholder)
                        .into(imageItem)
                    imageItem.setOnClickListener { _ ->
//                        XPopup.Builder(context)
//                            .asImageViewer(imageItem, message.imageUrl, SmartGlideImageLoader())
//                            .show()
                        val viewerPopup = CustomImageViewerPopup(context)
                        viewerPopup.setSingleSrcView(imageItem, message.imageUrl)
//                viewerPopup.isInfinite(true);
                        viewerPopup.setXPopupImageLoader(SmartGlideImageLoader())
//                viewerPopup.isShowIndicator(false);//是否显示页码指示器
//                viewerPopup.isShowPlaceholder(false);//是否显示白色占位块
                viewerPopup.isShowSaveButton(false);//是否显示保存按钮
//                viewerPopup.isShowIndicator(false);//是否显示页码指示器
//                viewerPopup.isShowPlaceholder(false);//是否显示白色占位块
//                viewerPopup.isShowSaveButton(false);//是否显示保存按钮
                        XPopup.Builder(context).isDestroyOnDismiss(true).asCustom(viewerPopup)
                            .show()
                    }
                }
                Glide
                    .with(context)
                    .load(it.aiPhoto)
                    .centerCrop()
                    .placeholder(com.hope.resources.R.drawable.ic_profile)
                    .into(chatBubbleIcon)
                imReload.setOnClickListener { _ ->
                    onEventClickListener.onEventClickReload(holder.layoutPosition, item)
                }
                imDelete.setOnClickListener { _ ->
                    onEventClickListener.onEventClickDelete(holder.layoutPosition, item)
                }
                holder.itemView.setOnClickListener { _ ->
                    onEventClickListener.onEventClick(holder.layoutPosition, item)
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