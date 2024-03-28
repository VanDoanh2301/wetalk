package com.example.wetalk.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.wetalk.R
import com.example.wetalk.data.model.objectmodel.CollectData
import com.example.wetalk.databinding.ItemCollectDataBinding
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class CollectDataAdapter(var context: Context) :
    RecyclerView.Adapter<CollectDataAdapter.ViewHolder>() {
    private var listTest: List<CollectData> = emptyList()

    private var onClickItem:OnClickItem ?= null
   fun setOnClickItem(onClickItem: OnClickItem) {
        this.onClickItem = onClickItem
    }
    //SetUp ViewHolder
    inner class ViewHolder(var binding: ItemCollectDataBinding) :
        RecyclerView.ViewHolder(binding.root)  {

        fun bind(collectData: CollectData, position: Int) {
            binding.apply {
                progressBar.visibility = View.VISIBLE
                Glide.with(context).load(collectData.dataLocation)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            progressBar.visibility = View.GONE
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            progressBar.visibility = View.GONE
                           return false
                        }

                    })
                    .into(imgTopic)
                tvContent.text = "Content: " + collectData.vocabularyContent
                tvEmail.text = "Editor: " + collectData.editor
                val dateTime = OffsetDateTime.parse(
                    collectData.created,
                    DateTimeFormatter.ISO_OFFSET_DATE_TIME
                )
                val formatter =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
                val dateString: String = formatter.format(dateTime.toLocalDate())
                tvTime.text = "Created: "+ dateString
                val statusText = when (collectData.status) {
                    100 -> "Đang chờ duyệt"
                    200 -> "Đã được duyệt"
                    300 -> "Từ chối"
                    else -> "Trạng thái không xác định"
                }

                tvStatus.text = "Status: $statusText"

                when (collectData.status) {
                    100 -> tvStatus.setTextColor(Color.parseColor("#FFEB3B"))
                    200 -> tvStatus.setTextColor(Color.parseColor("#4CAF50"))
                    300 -> tvStatus.setTextColor(Color.parseColor("#D32F2F"))
                    else -> tvStatus.setTextColor(Color.parseColor("#D32F2F"))
                }

                btMore.setOnClickListener {
                    if (onClickItem != null) {
                        onClickItem!!.onClickItem(position, collectData, it)
                    }
                }
            }

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //Create view for adapter
        val inflater = LayoutInflater.from(context)
        val binding = ItemCollectDataBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listTest.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Get user by position
        val user = listTest[position]
        holder.bind(user, position)
    }


    fun submitList(newList: List<CollectData>) {
        val diffCallback = ResultPregnancyDiffCallback(listTest, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        listTest = newList
        diffResult.dispatchUpdatesTo(this)
    }
    fun updateItem(position: Int, newItem: CollectData) {
        if (position in 0 until listTest.size) {
            val newList = listTest.toMutableList()
            newList[position] = newItem
            listTest = newList.toList()
            notifyItemChanged(position)
        }
    }
    fun removeItem(position: Int) {
        if (position in 0 until listTest.size) {
            val newList = listTest.toMutableList()
            newList.removeAt(position)
            listTest = newList.toList()
            notifyItemRemoved(position)
        }
    }

    inner class ResultPregnancyDiffCallback(
        private val oldList: List<CollectData>,
        private val newList: List<CollectData>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            // Xác định xem hai đối tượng có đại diện cho cùng một mục hay không
            return oldList[oldItemPosition].dataCollectionId== newList[newItemPosition].dataCollectionId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            // Xác định xem hai đối tượng có nội dung giống nhau hay không
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
    interface OnClickItem {
        fun onClickItem(position: Int, collectData: CollectData, view:View)
    }
}