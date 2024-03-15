package com.example.wetalk.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wetalk.R
import com.example.wetalk.data.model.objectmodel.ChatMessage
import com.example.wetalk.data.model.objectmodel.Message
import com.example.wetalk.databinding.ItemChatRecevieBinding
import com.example.wetalk.databinding.ItemChatSendBinding
import com.example.wetalk.databinding.ItemConversationBinding
import com.example.wetalk.util.AVATAR_SENDER
import com.example.wetalk.util.SEND_STATUS
import com.example.wetalk.util.SharedPreferencesUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MessagePagingAdapter(var context: Context): PagingDataAdapter<Message, RecyclerView.ViewHolder>(
    COMPARATOR) {
    private val TYPE_1 = 1
    private val TYPE_2 = 2
    inner class ItemChat1ViewHolder(var binding: ItemChatSendBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, chatMessage: Message) {
            binding.apply {
                tvSend.text = chatMessage.content
                val status = SharedPreferencesUtils.getString(SEND_STATUS)
                if (status.equals("done")) {
                    tvStatus.visibility = View.VISIBLE
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(2000) // Đợi 2 giây
                        tvStatus.visibility = View.GONE
                    }
                } else {
                    tvStatus.visibility = View.GONE
                }
            }
        }
    }

    inner class ItemChat2ViewHolder(var binding: ItemChatRecevieBinding) : RecyclerView.ViewHolder(binding.root) {
        private var isAvatarSet = false
        fun bind(position: Int, chatMessage: Message) {
            binding.apply {
                tvReceive.text = chatMessage.content
                if (position == 0 || getItem(position - 1)!!.contactId!= chatMessage.contactId) {
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
        return if (getItem(position )!!.contactId != SharedPreferencesUtils.getCurrentUser()) {
            TYPE_2
        } else {
            TYPE_1
        }
    }
    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<Message>() {
            override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
                return oldItem.messageId == newItem.messageId
            }

            override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
                return oldItem == newItem
            }
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
            var topic = getItem(position)
            item1ViewHolder.bind(position, topic!!)
        } else if (holder is ItemChat2ViewHolder) {
            val item2ViewHolder = holder as ItemChat2ViewHolder
            var topic = getItem(position)
            item2ViewHolder.bind(position, topic!!)
        }
    }


}