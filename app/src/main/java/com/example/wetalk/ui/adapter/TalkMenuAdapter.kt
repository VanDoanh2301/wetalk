package com.example.wetalk.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wetalk.data.local.MenuHome
import com.example.wetalk.databinding.ItemMenuHomeBinding

class TalkMenuAdapter(var context: Context, var menus: ArrayList<MenuHome>) :
    RecyclerView.Adapter<TalkMenuAdapter.ViewHolder>() {
    private var onItemClick: OnItemClick? =null
    inner class ViewHolder(var binding: ItemMenuHomeBinding) :
        RecyclerView.ViewHolder(binding.root) , View.OnClickListener{
        fun bind(position: Int, menuHome: MenuHome) {
            binding.apply {
                imgIcon.setImageResource(menuHome.icon)
                tvName.setText(menuHome.name)
            }
        }

        override fun onClick(p0: View?) {
            if (onItemClick != null) {
                onItemClick!!.onItem(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ItemMenuHomeBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return menus.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val menu = menus[position]
        holder.bind(position, menu)
    }

    /** Custom on Click Item */
    fun setOnItemClick(onItemClick: OnItemClick) {
        this.onItemClick = onItemClick
    }
    interface OnItemClick{
        fun  onItem(position: Int)
    }
}