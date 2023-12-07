package com.example.wetalk.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.example.wetalk.data.local.StorageImageItem
import com.example.wetalk.data.local.VideoBodyItem
import com.example.wetalk.data.local.VideoLocal
import com.example.wetalk.ui.activity.MainActivity

class TalkBodyEditView : LinearLayout {
    private lateinit var   videoLocal: VideoLocal
    private lateinit var activity: MainActivity
    private val deleteCallback: DeleteCallback? = null
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    fun addImage(result: ArrayList<StorageImageItem>) {
        val item: VideoBodyItem = VideoBodyItem.newImageItem(result)
        videoLocal.videoBodies.bodyItems.add(item)
        addView(object : TalkImageView(activity, item, false, deleteCallback) {
           override fun deleteImage() {
                videoLocal.videoBodies.bodyItems.remove(item)
                this@TalkBodyEditView.removeView(this)
            }
        })
    }

    fun preview(activity: MainActivity, videoLocal: VideoLocal) {
        removeAllViews()
        this.videoLocal = videoLocal
        this.activity = activity
        orientation = HORIZONTAL
        for (bodyItem in videoLocal.videoBodies.bodyItems) {
            addImagePreview(bodyItem)
        }
    }

    private fun addImagePreview(item: VideoBodyItem) {
        addView(object : TalkImageView(activity, item, true, deleteCallback) {
            override  fun deleteImage() {}
        })
    }

    interface DeleteCallback {
        fun onDelete(path: String?)
    }
}