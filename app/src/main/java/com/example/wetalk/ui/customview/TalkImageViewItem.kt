package com.example.wetalk.ui.customview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.example.wetalk.R
import com.example.wetalk.WeTalkApp

import com.example.wetalk.data.local.StorageImageItem
import com.example.wetalk.ui.activity.MainActivity
import com.example.wetalk.ui.fragment.BaseFragment
import com.example.wetalk.util.Task
import com.example.wetalk.util.Utils
import com.xujiaji.happybubble.Auto
import com.xujiaji.happybubble.BubbleDialog
import com.xujiaji.happybubble.BubbleLayout
import java.io.File
import javax.sql.DataSource

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
        w = ((WeTalkApp.W() - Utils.dpToPx(32f)) / 100f)

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
                .apply(RequestOptions.bitmapTransform(RoundedCorners(Utils.dpToPx(10f).toInt())))
                .into(imgPaint)
        }
        set()

        val imgPlay: ImageView = findViewById(R.id.img_play)
        imgPlay.visibility = if (item.isVideo) VISIBLE else GONE

        cvFrame!!.setOnClickListener {
            if (isPreview) {
//                BaseFragment.add(
//                    context, TalkPlayFragment.newInstance()
//                        .setPath(item.isVideo?.let { if (it) item.devicePath else item.path } ?: "", item.isVideo?.let { if (it) 2 else 1 } ?: 0)
//                )
            } else {
                cvFrame!!.setCardBackgroundColor(Color.parseColor("#13B6CC"))
                showPaintMenu(this@TalkImageViewItem, object : Task<Int> {
                    override fun callback(result: Int) {
                        when (result) {
                            0 -> full()
                            1 -> zoomIn()
                            2 -> zoomOut()
                            3 -> transLeft()
                            4 -> transRight()
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
                Utils.shareFileOutApp(context, item.devicePath, "video")
            } else {
                Utils.shareImageFileInApp(context, item.path )
            }
        } else {
            Utils.shareImageFileInApp(context, item.path)
        }
    }

    /** set Frame */
    private fun set() {
        cvFrame!!.layoutParams.width = (w?.times(item!!.w))!!.toInt()
        (cvFrame!!.layoutParams as MarginLayoutParams).leftMargin = (w!! * item!!.transX).toInt()
        cvFrame!!.requestLayout()
    }

    /** transRight */
    private fun transRight() {
        val t = item!!.transX ?: 0
        if (t > 90) {
            return
        } else {
            item!!.transX = t + 1
        }
        set()
    }
    /** transLeft */
    private fun transLeft() {
        val t = item!!.transX ?: 0
        if (t < -90) {
            return
        } else {
            item!!.transX = t - 1
        }
        set()
    }
    /** Zoom In */
    private fun zoomIn() {
        val w = item!!.w ?: 0
        item!!.w = w + 2
        if (item!!.w ?: 0 > 100) {
            item!!.w = 100
        }
        set()
    }

    /** Zoom out */
    private fun zoomOut() {
        val w = item!!.w ?: 0
        item!!.w = w - 2
        if (item.w ?: 0 < 16) {
            item!!.w = 16
        }
        set()
    }

    /** Zoom Full */
    private fun full() {
        if (item!!.w ?: 0 < 50) {
            item!!.w = 50
        } else if (item!!.w == 50) {
            item!!.w = 100
        } else {
            item!!.w = 25
        }
        item!!.transX = 0
        set()
    }

    /** show paint menu example zoom, delete, share,..*/
    private fun showPaintMenu(v: View, type: Task<Int>) {
        val popup = LayoutInflater.from(context).inflate(R.layout.talk_popup_menu_paint_view, null)
        val bubbleLayout = BubbleLayout(context)
        bubbleLayout.lookWidth = 0
        bubbleLayout.lookLength = 0
        bubbleLayout.bubbleRadius = Utils.dpToPx(25f).toInt()
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
        popup.findViewById<View>(R.id.bt_full_image).setOnClickListener {
            type.callback(0)
        }
        popup.findViewById<View>(R.id.bt_zoom_in).setOnClickListener {
            type.callback(1)
        }
        popup.findViewById<View>(R.id.bt_zoom_out).setOnClickListener {
            type.callback(2)
        }
        popup.findViewById<View>(R.id.bt_start).setOnClickListener {
            type.callback(3)
        }
        popup.findViewById<View>(R.id.bt_end).setOnClickListener {
            type.callback(4)
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
