package com.example.wetalk.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.wetalk.data.local.SettingObject
import com.example.wetalk.databinding.ItemSettingBinding

class SettingAdapter(var context: Context) : RecyclerView.Adapter<SettingAdapter.ViewHolder>() {
    private var resultList: List<SettingObject> = emptyList()
    private var onClickItem: OnClickItem? = null
     fun setOnClickItem(onClickItem: OnClickItem) {
        this.onClickItem = onClickItem
    }
    inner class ViewHolder(var binding: ItemSettingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, settingObject: SettingObject) {
            binding.apply {
                imgTopic.setImageResource(settingObject.imgResource)
                tvTittle.text = settingObject.tvTitle
                if (position == 0 ) {
                    lineTop.visibility = View.VISIBLE
                }
                if (position == resultList.size -1) {
                    lineBottom.visibility = View.GONE
                }
                actionBar.setOnClickListener {
                    onClickItem!!.onClick(position, settingObject)
                }
             }
        }
    }
    fun submitList(newList: List<SettingObject>) {
        val diffCallback = ResultPregnancyDiffCallback(resultList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        resultList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ResultPregnancyDiffCallback(
        private val oldList: List<SettingObject>,
        private val newList: List<SettingObject>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            // Xác định xem hai đối tượng có đại diện cho cùng một mục hay không
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            // Xác định xem hai đối tượng có nội dung giống nhau hay không
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var inflater = LayoutInflater.from(context)
        val binding = ItemSettingBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return resultList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var settingObject = resultList!![position]
        holder.bind(position, settingObject)
    }
    interface OnClickItem{
        fun onClick(position: Int, settingObject: SettingObject)
    }
}