package com.example.wetalk.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wetalk.R
import com.example.wetalk.data.model.objectmodel.UserInforRequest
import com.example.wetalk.databinding.ItemPendingFriendBinding

class PendingAdapter(var context: Context) : RecyclerView.Adapter<PendingAdapter.ViewHolder>(){
    private var onItemClick: OnItemClick? =null
    private var users : List<UserInforRequest>  = emptyList()

    //SetUp ViewHolder
    inner class ViewHolder(var binding: ItemPendingFriendBinding) : RecyclerView.ViewHolder(binding.root) {
        /** Config view
         * [user] : Object UserInforRequest
         * [position] : Position item user in List
         */
        fun bind(user : UserInforRequest, position: Int) {
            binding.apply {
                //Set text name
                userNameText.text = user.name
                //Set text phonenumber
                if (user.phoneNumber != null) {
                    phoneText.text = user.phoneNumber
                } else {
                    phoneText.visibility = View.GONE
                }

                //Set ImageView
                if (user.avatarLocation == null) {
                    imgUp.setImageResource(R.drawable.ic_avatar_error)
                } else {
                    Glide.with(context).load(user.avatarLocation).into(imgUp)
                }

                tvAddFriend.setOnClickListener {
                    if (onItemClick != null) {
                        onItemClick!!.onItem(position, user)
                    }
                }
                itemView.setOnClickListener {
                    if (onItemClick != null) {
                        onItemClick!!.onUser(position, user)
                    }
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //Create view for adapter
        val inflater = LayoutInflater.from(context)
        val binding = ItemPendingFriendBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        //Result size list users
        return users!!.size
    }

    fun removeItemAt(position: Int) {
        if (position in 0 until users.size) {
            val mutableList = users.toMutableList()
            mutableList.removeAt(position)
            users = mutableList.toList()
            notifyItemRemoved(position)
        }
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Get user by position
        val user  = users!![position]
        holder.bind(user, position)
    }

    /** Custom on Click Item */
    fun setOnItemClickAddFriend(onItemClick: OnItemClick) {
        this.onItemClick = onItemClick
    }
    fun setOnItemViewUser(onItemClick: OnItemClick) {
        this.onItemClick = onItemClick
    }
    interface OnItemClick{
        fun  onItem(position: Int, user: UserInforRequest)
        fun  onUser(position: Int, user: UserInforRequest)
    }

    fun submitList(newList: List<UserInforRequest>) {
        val diffCallback = ResultPregnancyDiffCallback(users!!, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        users = newList
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ResultPregnancyDiffCallback(
        private val oldList: List<UserInforRequest>,
        private val newList: List<UserInforRequest>
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