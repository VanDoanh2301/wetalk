package com.example.wetalk.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.example.wetalk.R
import com.example.wetalk.data.local.ImageHome
import com.rey.material.widget.ImageView
import com.rey.material.widget.TextView

class ImageAdapter(private val context: Context, private val images: List<ImageHome>) :
    PagerAdapter() {
    override fun getCount(): Int {
        return images?.size ?: 0
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    @SuppressLint("MissingInflatedId")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.layout_item_image, container, false)
        val img = view.findViewById<ImageView>(R.id.img_view_1)
        val txtName =  view.findViewById<TextView>(R.id.txt_view_3)
        val image = images!![position]
        if (image != null) {
            Glide.with(context).load(image.resourceId).into(img)
            txtName.text = image.name
        }

        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}