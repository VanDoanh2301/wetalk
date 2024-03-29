package com.example.wetalk.ui.customview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.wetalk.R
import com.example.wetalk.WeTalkApp

import com.example.wetalk.data.local.StorageImageItem
import com.example.wetalk.ui.activity.MainActivity
import com.example.wetalk.ui.fragment.BaseFragment
import com.example.wetalk.ui.fragment.TalkPlayFragment
import com.example.wetalk.util.Task
import com.example.wetalk.util.FileConfigUtils
import com.xujiaji.happybubble.Auto
import com.xujiaji.happybubble.BubbleDialog
import com.xujiaji.happybubble.BubbleLayout
import java.io.File

abstract class TalkImageViewItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit  var item: StorageImageItem
    private   var w: Float? = null
    private lateinit var cvFrame: CardView

    constructor(context: MainActivity, item: StorageImageItem, isPreview: Boolean) : this(context as Context) {
        removeAllViews()
        this.item = item
        addView(LayoutInflater.from(context).inflate(R.layout.talk_item_talk_image_item, null))
        cvFrame = findViewById(R.id.cv_frame)
        val imgPaint: ImageView = findViewById(R.id.img_image)
        w = ((WeTalkApp.W() - FileConfigUtils.dpToPx(32f)) / 100f)

        if (TextUtils.isEmpty(item.path)) {
            Glide.with(getContext())
                .asBitmap()
                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                .skipMemoryCache(true)
                .load(item.devicePath)
                .into(imgPaint)
        } else {
            Glide.with(getContext())
                .load(item.path)
                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                .skipMemoryCache(true)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(FileConfigUtils.dpToPx(10f).toInt())))
                .into(imgPaint)
        }
        set()

        val imgPlay: ImageView = findViewById(R.id.img_play)
        imgPlay.visibility = if (item.isVideo) VISIBLE else GONE

        cvFrame!!.setOnClickListener {
            if (isPreview) {

            } else {
                cvFrame!!.setCardBackgroundColor(Color.parseColor("#13B6CC"))
                showPaintMenu(this@TalkImageViewItem, object : Task<Int> {
                    override fun callback(result: Int) {
                        when (result) {
                            4 -> {
                                BaseFragment.add(
                                    context, TalkPlayFragment.newInstance()
                                        .setPath(item.isVideo?.let { if (it) item.devicePath else item.path } ?: "", item.isVideo?.let { if (it) 2 else 1 } ?: 0)
                                )

                            }
                            5 -> share()
                            6 -> deleteImage()
                            else -> cvFrame!!.setCardBackgroundColor(Color.TRANSPARENT)
                        }
                    }
                })
            }
        }
    }

    /** share image */
    private fun share() {
        if (item.isVideo != null) {
            val file = File(item.devicePath ?: "")
            if (file.exists()) {
                FileConfigUtils.shareFileOutApp(context, item.devicePath, "video")
            } else {
                FileConfigUtils.shareImageFileInApp(context, item.path )
            }
        } else {
            FileConfigUtils.shareImageFileInApp(context, item.path)
        }
    }

    /** set Frame */
    private fun set() {
        cvFrame!!.layoutParams.width = (w?.times(item!!.w))!!.toInt() + 500
        (cvFrame!!.layoutParams as MarginLayoutParams).leftMargin = width
        cvFrame!!.requestLayout()
    }



    /** show paint menu example zoom, delete, share,..*/
    @SuppressLint("MissingInflatedId")
    private fun showPaintMenu(v: View, type: Task<Int>) {
        val popup = LayoutInflater.from(context).inflate(R.layout.talk_popup_menu_paint_view, null)
        val bubbleLayout = BubbleLayout(context)
        bubbleLayout.lookWidth = 0
        bubbleLayout.lookLength = 0
        bubbleLayout.bubbleRadius = FileConfigUtils.dpToPx(25f).toInt()
        bubbleLayout.setBubblePadding(0)
        bubbleLayout.bubbleColor = Color.parseColor("#13B6CC")
        val bubbleDialog = BubbleDialog(context)
            .setBubbleContentView<BubbleDialog>(popup)
            .setClickedView<BubbleDialog>(v)
            .autoPosition<BubbleDialog>(Auto.UP_AND_DOWN)
            .setTransParentBackground<BubbleDialog>()
            .setBubbleLayout<BubbleDialog>(bubbleLayout)
            .setOffsetX<BubbleDialog>(0)
            .setOffsetY<BubbleDialog>(0)
        bubbleDialog.show()
        popup.findViewById<View>(R.id.bt_end).setOnClickListener {
            type.callback(4)
            bubbleDialog.dismiss()
        }
        popup.findViewById<View>(R.id.bt_share).setOnClickListener {
            type.callback(5)
            bubbleDialog.dismiss()
        }
        popup.findViewById<View>(R.id.bt_delete).setOnClickListener {
            type.callback(6)
            bubbleDialog.dismiss()
        }
        bubbleDialog.setOnDismissListener { type.callback(7) }
    }

    abstract fun deleteImage()
}
