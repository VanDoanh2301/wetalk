package com.example.wetalk.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.example.wetalk.R
import com.example.wetalk.data.local.VideoBodyItem
import com.example.wetalk.ui.activity.MainActivity
import com.google.android.flexbox.FlexboxLayout

abstract class ProvideImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    constructor(
        context: MainActivity,
        item: VideoBodyItem,
        isPreview: Boolean,
        deleteCallback: TalkBodyEditView.DeleteCallback?
    ) : this(context as Context) {
        removeAllViews()
        addView(LayoutInflater.from(context).inflate(R.layout.talk_item_talk_image, null))

        val box: FlexboxLayout = findViewById(R.id.image_layout)

        for (image in item.storageImage.imageItems) {
            box.addView(object : TalkImageViewItem(context, image, isPreview) {
                override fun deleteImage() {
                    deleteCallback?.onDelete(image.path)
                    box.removeView(this)
                    item.storageImage.imageItems.remove(image)
                    if (item.storageImage.imageItems.isEmpty()) {
                        this@ProvideImageView.deleteImage()
                    }
                }
            })
        }
    }

    abstract fun deleteImage()
}
