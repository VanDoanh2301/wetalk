package com.example.wetalk.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wetalk.R
import com.example.wetalk.data.model.objectmodel.TopicRequest
import com.example.wetalk.data.model.objectmodel.UserInforRequest
import com.example.wetalk.databinding.ItemPendingFriendBinding
import com.example.wetalk.databinding.ItemVideoTutorialBinding

class VocabulariesAdapter(var context: Context) :
    RecyclerView.Adapter<VocabulariesAdapter.ViewHolder>() {
    private var onItemClick: OnItemClick? = null
    private var users: List<TopicRequest> = emptyList()

    //SetUp ViewHolder
    inner class ViewHolder(var binding: ItemVideoTutorialBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(topicRequest: TopicRequest, position: Int) {
            binding.apply {
                if (topicRequest.imageLocation.equals("") && topicRequest.videoLocation.equals("")) {
                    imgVideo.setImageResource(R.drawable.ic_study)
                } else if (topicRequest.imageLocation.equals("")) {
                    Glide.with(context).load(topicRequest.videoLocation).into(imgVideo)
                } else {
                    Glide.with(context).load(topicRequest.imageLocation).into(imgVideo)
                }
                tvTitle.text = topicRequest.content
                itemView.setOnClickListener {
                    if (onItemClick != null) {
                        onItemClick!!.onItem(position, topicRequest)
                    }
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //Create view for adapter
        val inflater = LayoutInflater.from(context)
        val binding = ItemVideoTutorialBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        //Result size list users
        return users.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Get user by position
        val user = users[position]
        holder.bind(user, position)
    }

    /** Custom on Click Item */
    fun setOnItemClickAddFriend(onItemClick: OnItemClick) {
        this.onItemClick = onItemClick
    }

    fun setOnItmViewUser(onItemClick: OnItemClick) {
        this.onItemClick = onItemClick
    }

    interface OnItemClick {
        fun onItem(position: Int, topicRequest: TopicRequest)

    }

    fun submitList(newList: List<TopicRequest>) {
        val diffCallback = ResultPregnancyDiffCallback(users, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        users = newList
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ResultPregnancyDiffCallback(
        private val oldList: List<TopicRequest>,
        private val newList: List<TopicRequest>
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
}