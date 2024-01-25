package com.example.wetalk.ui.fragment

import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.sign_lang_ml.Classifier
import com.example.wetalk.R
import com.example.wetalk.data.local.StorageImageItem
import com.example.wetalk.ui.activity.MainActivity
import com.example.wetalk.util.RealPathUtil
import com.example.wetalk.util.Task
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2
import org.opencv.android.JavaCameraView
import org.opencv.android.OpenCVLoader
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import java.io.IOException


/**
 * A simple [Fragment] subclass.
 * Use the [TalkCameraOpenCvFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TalkCameraOpenCvFragment : Fragment(), CvCameraViewListener2 {
    private val BUTTON_SIZE = 80
    private val CLASSIFY_INTERVAL = 20
    private var classifier: Classifier? = null
    private var frame: Mat? = null
    private var mRGBA: Mat? = null
    private var openCvCameraView: JavaCameraView? = null
    private val TAG = "OpenCV_Fragment"
    private val buttonLayout: LinearLayout? = null
    private val debugLayout: LinearLayout? = null
    private var probTextView: TextView? = null
    private var resultTextView: TextView? = null
    private val dialog: AlertDialog? = null
    private val paths = ArrayList<String>()
    private var devicePath: String? = null
    private var uri: Uri? = null
    private var task : Task<Uri> ? = null
    private val isDebug = false
    private val isEdge = false
    private val isSave = false

    private var text = ""
    private var counter = 0


    fun setTask(task: Task<Uri>)  : TalkCameraOpenCvFragment {
        this.task = task
        return this
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }
    private val baseloadercallback: BaseLoaderCallback = object : BaseLoaderCallback(context) {
        override fun onManagerConnected(status: Int) {
            super.onManagerConnected(status)
            if (status == SUCCESS) openCvCameraView!!.enableView() else super.onManagerConnected(
                status
            )
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_talk_camera_open_cv, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layout = view.findViewById<FrameLayout>(R.id.fragmentLayout)

        openCvCameraView = view.findViewById<JavaCameraView>(R.id.my_camera_view)// Assuming 0 is the camera index
        openCvCameraView!!.visibility = SurfaceView.VISIBLE
        openCvCameraView!!.setCvCameraViewListener(this)
        resultTextView = TextView(requireContext())
        resultTextView!!.setTextColor(Color.WHITE)
        resultTextView!!.setTextSize(20f)
        resultTextView!!.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT,
            Gravity.TOP + Gravity.CENTER_HORIZONTAL
        )
        layout.addView(resultTextView)

        probTextView = TextView(requireContext())
        probTextView!!.setTextColor(Color.WHITE)
        probTextView!!.setTextSize(20f)
        probTextView!!.setPadding(
            0,
            0,
            0,
            BUTTON_SIZE + 150
        )
        probTextView!!.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT,
            Gravity.BOTTOM + Gravity.CENTER_HORIZONTAL
        )
        layout.addView(probTextView)
        val btnRecord  = view.findViewById<Button>(R.id.recordButton)
        btnRecord.setOnClickListener {

            val i: Intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            startActivityForResult(i, 1111)
        }
    }
    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            TalkCameraOpenCvFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onCameraViewStarted(width: Int, height: Int) {
        mRGBA = Mat()
    }

    override fun onCameraViewStopped() {
        if (mRGBA != null) mRGBA!!.release()
    }

    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame?): Mat {
        val mh = mRGBA!!.height().toFloat()
        val cw = Resources.getSystem().displayMetrics.widthPixels.toFloat()
        val scale = mh / cw * 0.7f

        mRGBA = inputFrame!!.rgba()
        frame = classifier!!.processMat(mRGBA!!)

        if (!isDebug) {
            if (counter == CLASSIFY_INTERVAL) {
                runInterpreter()
                counter = 0
            } else {
                counter++
            }
        }

        Imgproc.rectangle(
            mRGBA,
            Point(
                (mRGBA!!.cols() / 2f - mRGBA!!.cols() * scale / 2).toDouble(),
                (
                        mRGBA!!.rows() / 2f - mRGBA!!.cols() * scale / 2).toDouble()
            ),
            Point(
                (mRGBA!!.cols() / 2f + mRGBA!!.cols() * scale / 2).toDouble(),
                (
                        mRGBA!!.rows() / 2f + mRGBA!!.cols() * scale / 2).toDouble()
            ),
            Scalar(0.0, 255.0, 0.0), 1
        )
        if (isEdge) {
            mRGBA = classifier!!.debugMat(mRGBA!!)
        }

        System.gc()
        return mRGBA!!
    }
    private fun runInterpreter() {
        if (classifier != null) {
            classifier!!.classifyMat(frame!!)
            when (classifier!!.result) {
                "SPACE" -> text += " "
                "BACKSPACE" -> text = text.substring(0, text.length - 1)
                "NOTHING" -> {}
                else -> text = classifier!!.result.toString()
            }
            (activity as MainActivity).runOnUiThread(Runnable { resultTextView!!.text = text })
            Log.d(
                "OPENCV_Fragment",
                "Guess: " + classifier!!.result + " Probability: " + classifier!!.probability
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "Connected camera.")
            baseloadercallback.onManagerConnected(BaseLoaderCallback.SUCCESS)
        } else {
            Log.d(TAG, "Camera not connected.")
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, requireActivity(), baseloadercallback)
        }
        val windowVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
        (activity as MainActivity).window.decorView.setSystemUiVisibility(windowVisibility)
        try {
            classifier = Classifier(requireActivity())
        } catch (e: IOException) {
            Log.e(
                TAG,
                "Failed to initialize classifier",
                e
            )
        }
    }


    override fun onPause() {
        super.onPause()
        if (openCvCameraView != null) {
            openCvCameraView!!.disableView()
        }
        if (classifier != null) {
            classifier!!.close()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (openCvCameraView != null) {
            openCvCameraView!!.disableView()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        paths.clear()
        if (requestCode == 1111) {
            try {
                uri = data!!.data
                task!!.callback(uri!!)
                requireActivity().onBackPressed()
            } catch (e: Exception) {
            }
        }
    }
}