package com.example.wetalk.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wetalk.R
import com.example.wetalk.data.model.objectmodel.GetAllListConversations
import com.example.wetalk.data.model.objectmodel.GroupAttachmentConversation
import com.example.wetalk.data.model.objectmodel.UserInforRequest
import com.example.wetalk.databinding.ItemConversationBinding
import com.example.wetalk.databinding.ItemFriendBinding
import com.example.wetalk.util.EMAIL_USER
import com.example.wetalk.util.SharedPreferencesUtils

class ConversationAdapter(var context: Context) :
    RecyclerView.Adapter<ConversationAdapter.ViewHolder>() {
    private var users: List<GetAllListConversations> = emptyList()

    private var onClickItem : OnClickItem ?= null
    fun setOnClickItem(onClickItem: OnClickItem) {
        this.onClickItem = onClickItem
    }
    inner class ViewHolder(var binding: ItemConversationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: GetAllListConversations, position: Int) {
            binding.apply {
                var groupAttachmentConversations = user.grouAttachConvResList.filter {
                    it.email != SharedPreferencesUtils.getString(
                        EMAIL_USER
                    )
                }
                if (groupAttachmentConversations.size == 1) {
                    val firstElement = groupAttachmentConversations[0]
                    userNameText.text = firstElement.contactName
                    if (firstElement.lastMessageRes == null) {
                        lastMessage.visibility = View.GONE
                    } else {
                        lastMessage.text = firstElement.lastMessageRes.content
                    }

                    //Set ImageView
                    if (firstElement.avatarLocation == null) {
                        imgUp.setImageResource(R.drawable.ic_avatar_error)
                    } else {
                        Glide.with(context).load(firstElement.avatarLocation).into(imgUp)
                    }

                    openChat.setOnClickListener {
                        if (onClickItem != null) {
                            onClickItem!!.onClickItem(position, user)
                        }
                    }

                } else {

                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //Create view for adapter
        val inflater = LayoutInflater.from(context)
        val binding = ItemConversationBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        //Result size list users
        return users!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Get user by position
        val user = users!![position]
        holder.bind(user, position)
    }


    fun submitList(newList: List<GetAllListConversations>) {
        val diffCallback = ResultPregnancyDiffCallback(users!!, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        users = newList
        diffResult.dispatchUpdatesTo(this)
    }


    inner class ResultPregnancyDiffCallback(
        private val oldList: List<GetAllListConversations>,
        private val newList: List<GetAllListConversations>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].conversationId == newList[newItemPosition].conversationId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

    interface OnClickItem{
        fun onClickItem(position: Int, getAllListConversations: GetAllListConversations)
    }
}