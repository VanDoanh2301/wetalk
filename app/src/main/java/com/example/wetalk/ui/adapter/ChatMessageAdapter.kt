package com.example.wetalk.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wetalk.R
import com.example.wetalk.data.model.objectmodel.ChatMessage
import com.example.wetalk.data.model.objectmodel.UserInforRequest
import com.example.wetalk.databinding.ItemChatRecevieBinding
import com.example.wetalk.databinding.ItemChatSendBinding
import com.example.wetalk.util.AVATAR_SENDER
import com.example.wetalk.util.SEND_STATUS
import com.example.wetalk.util.SharedPreferencesUtils
import com.google.android.exoplayer2.C
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
//                  val status = SharedPreferencesUtils.getString(SEND_STATUS)
//                if (status.equals("done")) {
//                    tvStatus.visibility = View.VISIBLE
//                    CoroutineScope(Dispatchers.Main).launch {
//                        delay(2000) // Đợi 2 giây
//                        tvStatus.visibility = View.GONE
//                    }
//                } else {
//                    tvStatus.visibility = View.GONE
//                }
            }
        }
    }
    inner class ItemChat2ViewHolder(var binding: ItemChatRecevieBinding) : RecyclerView.ViewHolder(binding.root) {
        private var isAvatarSet = false
        fun bind(position: Int, chatMessage: ChatMessage) {
            binding.apply {
                tvReceive.text = chatMessage.content
                if (position == 0 || resultList[position - 1].id != chatMessage.id) {
                    val avatar = SharedPreferencesUtils.getString(AVATAR_SENDER)
                    if (avatar.isNullOrEmpty()) {
                        imgAvata.setImageResource(R.drawable.ic_avatar_error)
                    } else {
                        Glide.with(context).load(avatar).into(imgAvata)
                    }
                    isAvatarSet = true
                    cdAvatar.visibility = View.VISIBLE
                } else {
                    cdAvatar.visibility = View.INVISIBLE
                }
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
    fun addItem(newItem: ChatMessage) {
        val mutableList = resultList.toMutableList()
        mutableList.add(0, newItem)
        resultList = mutableList.toList()
        notifyItemInserted(0)
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