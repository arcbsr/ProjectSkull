package com.hope.main_ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hope.common.log.Log
import com.hope.firebase.database.aicreator.Models
import com.hope.lib_mvvm.adapter.BaseViewBindingAdapter
import com.hope.lib_mvvm.adapter.BaseViewBindingHolder
import com.hope.main_ui.R
import com.hope.main_ui.databinding.ItemAiProfleBinding
import com.hope.main_ui.utils.GlideEngine


class AIProfilesAdapter :
    BaseViewBindingAdapter<Models.AiProfile, AIProfilesAdapter.ViewHolder>(R.layout.item_ai_profle) {
    private var spanCount = 1
    fun setSpanSizeLookup(spanCount: Int) {
        this.spanCount = spanCount
    }

    private lateinit var onEventClickListener: OnEventClickListener
    fun setOnInviteClickListener(onInviteClickListener: OnEventClickListener) {
        this.onEventClickListener = onInviteClickListener
    }

    interface OnEventClickListener {
        fun onEventClickReload(position: Int, item: Models.AiProfile)
        fun onEventClickDelete(position: Int, item: Models.AiProfile)
        fun onEventClick(position: Int, item: Models.AiProfile)
    }

    override fun convert(holder: ViewHolder, item: Models.AiProfile) {
        holder.viewBind.apply {
            GlideEngine.createGlideEngine().loadImage(
                context,
                item.imageUrl,
                imAi1
            )
            txtAi1.text = item.name
            if (item.isSelected) {
                txtAi1.setBackgroundColor(context.getColor(com.hope.resources.R.color.green))
            } else {
                txtAi1.setBackgroundColor(context.getColor(com.hope.resources.R.color.orange))
            }
            holder.itemView.setOnClickListener {
//                Log.d("AIProfilesAdapter", "convert: $item")
                val position = holder.adapterPosition
                onEventClickListener.onEventClick(position, item)
                for (item in data) {
                    Log.d("AIProfilesAdapter", "convert: ${item.isSelected}")
                    item.isSelected = false
                }
                item.isSelected = true
                notifyDataSetChanged()
//                setDefaultAI(position = position)
            }
        }
    }

    override fun getViewBinding(
        viewType: Int,
        from: LayoutInflater?,
        parent: ViewGroup?
    ): ViewHolder {
        return ViewHolder(
            ItemAiProfleBinding.inflate(
                from!!, parent, false
            )
        )
    }

    fun removeSelection() {
        for (item in data) {
            Log.d("AIProfilesAdapter", "convert: ${item.isSelected}")
            item.isSelected = false
        }
        notifyDataSetChanged()
    }

    fun setDefaultAI(position:Int = 0) {
        for (item in data) {
            Log.d("AIProfilesAdapter", "convert: ${item.isSelected}")
            item.isSelected = false
        }
        data[0].isSelected = true
        notifyDataSetChanged()
        onEventClickListener.onEventClick(0, data[0])
    }

    inner class ViewHolder(viewBind: ItemAiProfleBinding) :
        BaseViewBindingHolder<ItemAiProfleBinding>(viewBind)
}