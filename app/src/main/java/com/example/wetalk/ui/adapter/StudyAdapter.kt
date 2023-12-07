package com.example.wetalk.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wetalk.databinding.ItemStudyBinding

class StudyAdapter(var context: Context, var dataList: ArrayList<String>) : RecyclerView.Adapter<StudyAdapter.ViewHolder>(){
    inner class ViewHolder(var binding : ItemStudyBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding =ItemStudyBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvString.text = dataList[position]
    }
}