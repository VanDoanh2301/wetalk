package com.example.wetalk.util.helper


import android.app.Activity
import android.app.ProgressDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import com.example.wetalk.R
import com.example.wetalk.WeTalkApp
import com.example.wetalk.data.local.StorageImageItem
import com.example.wetalk.util.Task
import java.io.File
import java.io.FileOutputStream

object FileHelper {
    fun getDataFolder(): String {
        val file = File(WeTalkApp.get().filesDir, "data")
        if (!file.isDirectory) {
            file.mkdirs()
        }
        return file.absolutePath + File.separator
    }


    fun getNewImagePreviewPath(): String {
        return getDataFolder() + "diary_image_" + System.currentTimeMillis() + ".webp"
    }

    fun getNewVideoPreviewPath(): String {
        return getDataFolder() + "diary_video_" + System.currentTimeMillis() + ".webp"
    }



    private fun getFileExtension(file: File): String {
        val name = file.name
        val lastIndexOf = name.lastIndexOf(".")
        if (lastIndexOf == -1) {
            return "" // empty extension
        }
        return name.substring(lastIndexOf)
    }



    fun checkAndSavePreview(item: StorageImageItem, bitmap: Bitmap) {
        val vdFile = File(if (item.isVideo) getNewVideoPreviewPath() else getNewImagePreviewPath())
        val w = bitmap.width
        val h = bitmap.height
        val newW = Math.min(w, 720)
        val resizeBitmap = Bitmap.createScaledBitmap(bitmap, newW, newW * h / w, false)
        try {
            val fOut = FileOutputStream(vdFile)
            resizeBitmap.compress(Bitmap.CompressFormat.WEBP, 70, fOut)
            fOut.flush()
            fOut.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        bitmap.recycle()
        resizeBitmap.recycle()
        item.path = vdFile.absolutePath
    }

    fun checkAndCopyImage(activity: Activity, result: ArrayList<StorageImageItem>, success: Task<ArrayList<StorageImageItem>>) {
        val progressDialog = ProgressDialog(activity)
        progressDialog.setMessage(activity.getString(R.string.Importing))
        progressDialog.show()
        progressDialog.setCancelable(false)
        Thread {
            for (imageItem in result) {
                val path = imageItem.devicePath
                val file = File(path)
                if (!imageItem.isVideo) {
                    if (file.exists()) {
                        val resizeBitmap: Bitmap
                        val imgFile = File(getNewImagePreviewPath())
                        val bitmap = BitmapFactory.decodeFile(path)
                        resizeBitmap = try {
                            val w = bitmap.width
                            val h = bitmap.height
                            val newW = Math.min(w, 720)
                            Bitmap.createScaledBitmap(bitmap, newW, newW * h / w, false)
                        } catch (e: Exception) {
                            Bitmap.createBitmap(128, 128, Bitmap.Config.RGB_565)
                        }
                        try {
                            val fOut = FileOutputStream(imgFile)
                            resizeBitmap.compress(Bitmap.CompressFormat.WEBP, 70, fOut)
                            fOut.flush()
                            fOut.close()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        bitmap?.recycle()
                        resizeBitmap?.recycle()
                        imageItem.path = imgFile.absolutePath
                    }
                }
            }
            Handler(activity.mainLooper).post {
                progressDialog.dismiss()
                success.callback(result)
            }
        }.start()
    }
}