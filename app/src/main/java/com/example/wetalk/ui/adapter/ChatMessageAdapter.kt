package com.example.wetalk.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.wetalk.data.model.objectmodel.ChatMessage
import com.example.wetalk.databinding.ItemChatRecevieBinding
import com.example.wetalk.databinding.ItemChatSendBinding
import com.example.wetalk.util.SharedPreferencesUtils

class ChatMessageAdapter(var context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    private val TYPE_1 = 1
    private val TYPE_2 = 2
    private var resultList: List<ChatMessage> = emptyList()
    private var onClickItem: OnClickItem? = null
    fun setOnClickItem(onClickItem: OnClickItem) {
        this.onClickItem = onClickItem
    }

    inner class ItemChat1ViewHolder(var binding: ItemChatSendBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, chatMessage: ChatMessage) {
            binding.apply {
                  tvSend.text = chatMessage.content
            }
        }
    }
    inner class ItemChat2ViewHolder(var binding: ItemChatRecevieBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, chatMessage: ChatMessage) {
            binding.apply {
                tvReceive.text = chatMessage.content
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (resultList[position].id != SharedPreferencesUtils.getCurrentUser()) {
            TYPE_2
        } else {
            TYPE_1
        }
    }

    fun submitList(newList: List<ChatMessage>) {
        val diffCallback = ResultPregnancyDiffCallback(resultList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        resultList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ResultPregnancyDiffCallback(
        private val oldList: List<ChatMessage>,
        private val newList: List<ChatMessage>
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        if (viewType == TYPE_1) {
            val binding = ItemChatSendBinding.inflate(inflater, parent, false)
            return ItemChat1ViewHolder(binding)
        } else if (viewType == TYPE_2) {
            val binding = ItemChatRecevieBinding.inflate(inflater, parent, false)
            return ItemChat2ViewHolder(binding)
        }
        throw IllegalArgumentException("Invalid view type")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemChat1ViewHolder) {
            val item1ViewHolder = holder as ItemChat1ViewHolder
            var topic = resultList[position]
            item1ViewHolder.bind(position, topic)
        } else if (holder is ItemChat2ViewHolder) {
            val item2ViewHolder = holder as ItemChat2ViewHolder
            var topic = resultList[position]
            item2ViewHolder.bind(position, topic)
        }
    }

    override fun getItemCount(): Int {
        return resultList.size
    }

    interface OnClickItem{
        fun onClick(position: Int, chatMessage: ChatMessage)
    }
}