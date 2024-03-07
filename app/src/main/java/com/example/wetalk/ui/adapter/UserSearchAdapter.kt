package com.example.wetalk.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wetalk.data.model.objectmodel.UserInforRequest
import com.example.wetalk.databinding.ItemSearchUserBinding

class UserSearchAdapter(var context: Context, var users: ArrayList<UserInforRequest>) : RecyclerView.Adapter<UserSearchAdapter.ViewHolder>(){

    private var onItemClick: OnItemClick? =null
    private var isFriend = false

    //SetUp ViewHolder
    inner class ViewHolder(var binding: ItemSearchUserBinding) : RecyclerView.ViewHolder(binding.root) {
        /** Config view
         * [user] : Object UserInforRequest
         * [position] : Position item user in List
         */
        fun bind(user : UserInforRequest, position: Int) {
            binding.apply {
                //Set text name
                userNameText.text = user.name
                //Set text phonenumber
                phoneText.text = user.phoneNumber
                //Set ImageView
                Glide.with(context).load(user.avatarLocation).into(imgUp)

                if (isFriend) {
                    tvAddFriend.text = "Đã gửi lời mời"
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
        val binding = ItemSearchUserBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        //Result size list users
        return users.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Get user by position
        val user  = users[position]
        holder.bind(user, position)
    }

    /** Custom on Click Item */
    fun setOnItemClickAddFriend(onItemClick: OnItemClick) {
        this.onItemClick = onItemClick
    }
    fun setOnItmViewUser(onItemClick: OnItemClick) {
        this.onItemClick = onItemClick
    }

    fun updateFriend(isFriend: Boolean, position: Int) {
        this.isFriend = isFriend
        notifyItemChanged(position)
    }

    interface OnItemClick{
        fun  onItem(position: Int, user: UserInforRequest)
        fun  onUser(position: Int, user: UserInforRequest)
    }
}