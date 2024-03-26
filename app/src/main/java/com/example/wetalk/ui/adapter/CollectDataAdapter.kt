package com.example.wetalk.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wetalk.R
import com.example.wetalk.data.model.objectmodel.CollectData
import com.example.wetalk.databinding.ItemCollectDataBinding
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class CollectDataAdapter(var context: Context) :
    RecyclerView.Adapter<CollectDataAdapter.ViewHolder>() {
    private var listTest: List<CollectData> = emptyList()

    private var onClickItem:OnClickItem ?= null
   fun setOnClickItem(onClickItem: OnClickItem) {
        this.onClickItem = onClickItem
    }
    //SetUp ViewHolder
    inner class ViewHolder(var binding: ItemCollectDataBinding) :
        RecyclerView.ViewHolder(binding.root)  {

        fun bind(collectData: CollectData, position: Int) {
            binding.apply {
                Glide.with(context).load(collectData.dataLocation).into(imgTopic)
                tvEmail.text = "Editor: " + collectData.editor
                val dateTime = OffsetDateTime.parse(
                    collectData.created,
                    DateTimeFormatter.ISO_OFFSET_DATE_TIME
                )
                val formatter =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
                val dateString: String = formatter.format(dateTime.toLocalDate())
                tvTime.text = "Created: "+ dateString

                btMore.setOnClickListener {
                    if (onClickItem != null) {
                        onClickItem!!.onClickItem(position, collectData)
                    }
                }
            }

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //Create view for adapter
        val inflater = LayoutInflater.from(context)
        val binding = ItemCollectDataBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listTest.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Get user by position
        val user = listTest[position]
        holder.bind(user, position)
    }


    fun submitList(newList: List<CollectData>) {
        val diffCallback = ResultPregnancyDiffCallback(listTest, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        listTest = newList
        diffResult.dispatchUpdatesTo(this)
    }
    fun updateItem(position: Int, newItem: CollectData) {
        if (position in 0 until listTest.size) {
            val newList = listTest.toMutableList()
            newList[position] = newItem
            listTest = newList.toList()
            notifyItemChanged(position)
        }
    }
    fun removeItem(position: Int) {
        if (position in 0 until listTest.size) {
            val newList = listTest.toMutableList()
            newList.removeAt(position)
            listTest = newList.toList()
            notifyItemRemoved(position)
        }
    }

    inner class ResultPregnancyDiffCallback(
        private val oldList: List<CollectData>,
        private val newList: List<CollectData>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            // Xác định xem hai đối tượng có đại diện cho cùng một mục hay không
            return oldList[oldItemPosition].dataCollectionId== newList[newItemPosition].dataCollectionId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            // Xác định xem hai đối tượng có nội dung giống nhau hay không
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
    interface OnClickItem {
        fun onClickItem(position: Int, collectData: CollectData)
    }
}