package com.example.wetalk.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wetalk.databinding.ItemTagBinding

abstract class DialogTagAdapter(
    var context: Context) :
    RecyclerView.Adapter<DialogTagAdapter.ViewHolder>() {
    private var dataList : ArrayList<String>? = null
    fun setData(dataList: ArrayList<String>) {
        this.dataList = dataList
        notifyDataSetChanged()
    }
    inner class ViewHolder(var binding: ItemTagBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ItemTagBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)

    }
    override fun getItemCount(): Int {
      return dataList!!.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = dataList!![position]
        holder.binding.txtTag.text = item
        holder.binding.cadItem.setOnClickListener{
            OnClickItemTag(position) }

    }
    abstract fun OnClickItemTag(position: Int)
}