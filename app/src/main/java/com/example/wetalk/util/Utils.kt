package com.example.wetalk.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.util.DisplayMetrics
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.wetalk.WeTalkApp
import com.rey.material.BuildConfig
import java.io.File
import java.io.FileOutputStream


object Utils {
    fun dpToPx(dp: Float): Float {
        return dp * (WeTalkApp.get().resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }
    fun hideKeyboard(activity: Activity?) {
        if (activity != null) {
            val view = activity.currentFocus
            val inputManager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (view != null && inputManager != null) {
                inputManager.hideSoftInputFromWindow(
                    view.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        }
    }
    fun shareFileOutApp(context: Context, path: String?, type: String) {
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "$type/*"
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(File(path)))
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(Intent.createChooser(shareIntent, "Share..."))
    }

    fun shareImageFileInApp(context: Context, path: String?) {
        val bm = BitmapFactory.decodeFile(path)
        val uri: Uri = getImageToShare(bm)!!
        bm.recycle()
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.type = "image/png"
        context.startActivity(Intent.createChooser(intent, "Share Image"))
    }
    private fun getImageToShare(bitmap: Bitmap): Uri? {
        val imageFolder: File = File(WeTalkApp.get().cacheDir, "shared_images")
        var uri: Uri? = null
        try {
            imageFolder.mkdirs()
            val file = File(imageFolder, "shared_diary.png")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 70, outputStream)
            outputStream.flush()
            outputStream.close()
            uri = FileProvider.getUriForFile(
                WeTalkApp.get(),
                BuildConfig.APPLICATION_ID + ".fileProvider",
                file
            )
        } catch (e: Exception) {
            Toast.makeText(WeTalkApp.get(), "" + e.message, Toast.LENGTH_LONG).show()
        }
        return uri
    }

    fun setColorFilter(img: ImageView?, color: Int) {
        if (img == null) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            img.setColorFilter(BlendModeColorFilter(color, BlendMode.SRC_IN))
        } else {
            img.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        }
    }

    fun setColorFilter(img: Drawable?, color: Int) {
        if (img == null) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            img.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_IN)
        } else {
            img.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        }
    }

    fun setColorFilter(img: Drawable?, color: String?) {
        setColorFilter(img, Color.parseColor(color))
    }

    fun setColorFilter(img: ImageView?, color: String?) {
        setColorFilter(img, Color.parseColor(color))
    }

}
