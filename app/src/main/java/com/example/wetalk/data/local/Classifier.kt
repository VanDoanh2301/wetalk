package com.example.sign_lang_ml

import android.app.Activity
import android.content.res.Resources
import android.util.Log
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Rect
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.util.Random

internal class Classifier(activity: Activity) {
    private var tflite: Interpreter? =null
    private var labels: List<String> ? =null
    private var imgData: ByteBuffer? = null
    private var probArray: Array<FloatArray> ? =null

    init {
        val tfliteModel: MappedByteBuffer = FileUtil.loadMappedFile(activity, MODEL)
        val tfliteOptions: Interpreter.Options = Interpreter.Options()
        tfliteOptions.setNumThreads(4)
        tflite = Interpreter(tfliteModel, tfliteOptions)
        labels = FileUtil.loadLabels(activity, LABEL)
        imgData = ByteBuffer.allocateDirect(DIM_HEIGHT * DIM_WIDTH * BYTES)
        imgData!!.order(ByteOrder.nativeOrder())
        probArray = Array(1) { FloatArray(labels!!.size) }
    }

    fun classifyMat(mat: Mat) {
        if (tflite != null) {
            convertMatToByteBuffer(mat)
            runInterface()
        }
    }

    val result: String?
        get() = Companion.result
    val probability: Float
        get() = Companion.probability

    fun close() {
        if (tflite != null) {
            tflite!!.close()
            tflite = null
        }
    }

    fun processMat(mat: Mat): Mat {
        val mh = mat.height().toFloat()
        val cw = Resources.getSystem().displayMetrics.widthPixels.toFloat()
        val scale = mh / cw * 0.7f
        val roi = Rect(
            (mat.cols() / 2 - mat.cols() * scale / 2).toInt(),
            (mat.rows() / 2 - mat.cols() * scale / 2).toInt(),
            (mat.cols() * scale).toInt(),
            (mat.cols() * scale).toInt()
        )
        val sub = mat.submat(roi)
        sub.copyTo(mat.submat(roi))
        val edges = Mat(sub.size(), CvType.CV_8UC1)
        Imgproc.Canny(sub, edges, 50.0, 200.0)
        val element1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(3.0, 3.0))
        Imgproc.dilate(edges, edges, element1)
        //Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new org.opencv.core.Size(2,2));
        //Imgproc.erode(edges, edges, element);
        Core.rotate(edges, edges, Core.ROTATE_90_CLOCKWISE)
        Imgproc.resize(edges, edges, Size(DIM_WIDTH.toDouble(), DIM_HEIGHT.toDouble()))
        return edges
    }

    fun debugMat(mat: Mat): Mat {
        val edges = Mat(mat.size(), CvType.CV_8UC1)
        Imgproc.Canny(mat, edges, 50.0, 200.0)
        val element1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(3.0, 3.0))
        Imgproc.dilate(edges, edges, element1)
        //Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new org.opencv.core.Size(2,2));
        //Imgproc.erode(edges, edges, element);
        return edges
    }

    val randomLabel: String
        get() {
            val r = Random()
            var rt = ""
            while (rt == "" || rt == "NOTHING") rt = labels!![r.nextInt(labels!!.size)]
            return rt
        }

    private fun convertMatToByteBuffer(mat: Mat) {
        imgData!!.rewind()
        for (i in 0 until DIM_HEIGHT) {
            for (j in 0 until DIM_WIDTH) {
                Log.d(TAG, "" + mat[i, j][0])
                imgData!!.putFloat(mat[i, j][0].toFloat() / 255.0f)
            }
        }
    }

    private fun runInterface() {
        if (imgData != null) {
            tflite!!.run(imgData, probArray)
        }
        processResults(probArray!![0])
        for (i in labels!!.indices) {
            Log.d(TAG, labels!![i] + ": " + probArray!![0][i])
        }
        Log.d(TAG, "Guess: " + this.result)
    }

    private fun processResults(prob: FloatArray) {
        var max = 0
        for (i in prob.indices) {
            if (prob[i] > prob[max]) {
                max = i
            }
        }
        if (prob[max] > 0.8f) {
            Companion.result = labels!![max]
            Companion.probability = prob[max]
        } else {
            Companion.result = "NOTHING"
            Companion.probability = 1.0f
        }
    }

    companion object {
        private const val TAG = "Tflite"
        private const val MODEL = "mobilenet.tflite"
        private const val LABEL = "labels.txt"
        private const val DIM_HEIGHT = 80
        private const val DIM_WIDTH = 80
        private const val BYTES = 4
        private var result: String? = null
        private var probability = 0f
    }
}
