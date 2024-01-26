package com.example.wetalk.ui.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wetalk.R
import com.example.wetalk.data.local.ChatroomModel


class RecentChatRecyclerAdapter(private val context: Context, private var chatModel: ArrayList<ChatroomModel>)
    : RecyclerView.Adapter<RecentChatRecyclerAdapter.ChatroomModelViewHolder>()
  {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatroomModelViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_chat_message, parent, false)
        return ChatroomModelViewHolder(view)
    }

      override fun getItemCount(): Int {
        return chatModel.size
      }

      override fun onBindViewHolder(holder: ChatroomModelViewHolder, position: Int) {
         val chatroomModel = chatModel[position]
      }

      inner class ChatroomModelViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private var usernameText: TextView
        private var lastMessageText: TextView
        private var lastMessageTime: TextView
        private var profilePic: ImageView

        init {
            usernameText = itemView.findViewById<TextView>(R.id.user_name_text)
            lastMessageText = itemView.findViewById<TextView>(R.id.last_message_text)
            lastMessageTime = itemView.findViewById<TextView>(R.id.last_message_time_text)
            profilePic = itemView.findViewById<ImageView>(R.id.img_avata)
        }
    }
}