package com.example.wetalk.ui.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.wetalk.databinding.ItemVideosBinding


abstract class VideoChooseAdapter(
    private val context: Context,
    private var videos: ArrayList<String>
) : RecyclerView.Adapter<VideoChooseAdapter.ViewHolder>() {

    private var pathSelect = ArrayList<String>()

    inner class ViewHolder(var binding: ItemVideosBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ItemVideosBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return videos.size!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        /** Show video */
        Glide.with(context)
            .load(videos[position])
            .apply(
                RequestOptions()
                    .override(153, 160)
                    .centerCrop()
                    .dontAnimate()
                    .skipMemoryCache(true)
            )
            .transition(DrawableTransitionOptions.withCrossFade())
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<Drawable?>,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any,
                    target: Target<Drawable?>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            })
            .into(holder.binding.imgBackground)

        /** On Click */
        holder.itemView.setOnClickListener { view: View? ->
            OnItemClick(
                position,
                videos[position]
            )
        }

        /** Check paths */
        if (pathSelect.contains(videos[position])) {
            holder.binding.imgCheck.visibility = View.VISIBLE
        } else {
            holder.binding.imgCheck.visibility = View.GONE
        }
    }

     fun notifyData(pathSelect: ArrayList<String>) {
        this.pathSelect = pathSelect
        notifyDataSetChanged()
    }
    abstract fun OnItemClick(position: Int, path: String?)
}