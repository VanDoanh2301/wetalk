package com.example.wetalk.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sign_lang_ml.Classifier
import com.example.wetalk.R
import com.example.wetalk.databinding.FragmentTalkTrainHandBinding
import com.example.wetalk.ui.activity.MainActivity
import com.example.wetalk.util.DialogVideo
import com.google.firebase.storage.FirebaseStorage
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.JavaCameraView
import org.opencv.android.OpenCVLoader
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import java.io.IOException


/**
 * A simple [Fragment] subclass.
 * Use the [TalkTrainHandFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TalkTrainHandFragment : Fragment(), CameraBridgeViewBase.CvCameraViewListener2 {
    private val CLASSIFY_INTERVAL = 20
    private val TAG = "TrainHandFragment"
    private var classifier: Classifier? = null
    private var frame: Mat? = null
    private var mRGBA: Mat? = null
    private var openCvCameraView: JavaCameraView? = null
    private var scoreTextView: TextView? = null
    private var resultTextView: TextView? = null
    private var text = ""
    private var counter = 0
    private var score = 0
    private var _binding: FragmentTalkTrainHandBinding? = null
    private val binding get() = _binding!!

    private val baseloadercallback: BaseLoaderCallback = object : BaseLoaderCallback(context) {
        override fun onManagerConnected(status: Int) {
            super.onManagerConnected(status)
            if (status == SUCCESS) openCvCameraView!!.enableView() else super.onManagerConnected(
                status
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this
        _binding = FragmentTalkTrainHandBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI(view)
        initHelp()

    }

    private fun initHelp() {
      binding.imgHelp.setOnClickListener {
          showVideoDialog(resultTextView!!.text.toString())
      }
        binding.btnMenu.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun initUI(view: View) {
        openCvCameraView =
            view.findViewById<JavaCameraView>(R.id.my_camera_view)// Assuming 0 is the camera index
        openCvCameraView!!.visibility = SurfaceView.VISIBLE
        openCvCameraView!!.setCvCameraViewListener(this)
        openCvCameraView!!.setCameraIndex(1)
        resultTextView = view.findViewById(R.id.tv_random)
        scoreTextView = view.findViewById(R.id.tv_score)
        val s = "Điểm: $score"
        scoreTextView!!.text = s

    }

    override fun onCameraViewStarted(width: Int, height: Int) {
        mRGBA = Mat()
    }

    override fun onCameraViewStopped() {
        if (mRGBA != null) mRGBA!!.release()
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame?): Mat {
        val mh = mRGBA!!.height().toFloat()
        val cw = Resources.getSystem().displayMetrics.widthPixels.toFloat()
        val scale = mh / cw * 0.7f

        mRGBA = inputFrame!!.rgba()
        frame = classifier!!.processMat(mRGBA!!)

        if (counter == CLASSIFY_INTERVAL) {
            runInterpreter()
            counter = 0
        } else {
            counter++
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
        System.gc()
        return mRGBA!!
    }

    private fun runInterpreter() {
        if (classifier != null) {
            classifier!!.classifyMat(frame!!)
            if (classifier!!.result != "NOTHING" && classifier!!.result == text) {
                score++
                text = classifier!!.randomLabel
                (activity as MainActivity).runOnUiThread(Runnable {
                    val s = "Score: $score!"
                    scoreTextView!!.text = s
                    resultTextView!!.text = text
                })
            }
            Log.d(
                TAG,
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
            OpenCVLoader.initAsync(
                OpenCVLoader.OPENCV_VERSION,
                requireContext(),
                baseloadercallback
            )
        }
        val windowVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
        (activity as MainActivity).getWindow().getDecorView()
            .setSystemUiVisibility(windowVisibility)
        try {
            classifier = Classifier(requireActivity())
            text = classifier!!.randomLabel
            resultTextView!!.text = text
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

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            TalkTrainHandFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    private fun getVideoURL(letter: String, callback: (String) -> Unit) {
        val storage = FirebaseStorage.getInstance()
        val videoRef = storage.reference.child("videos/${letter}.mp4")

        videoRef.downloadUrl
            .addOnSuccessListener { uri ->
                callback(uri.toString())
            }
            .addOnFailureListener {
            }
    }
    private fun showVideoDialog(letter: String) {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Đang tải")
        progressDialog.setMessage("Xin chờ...")
        progressDialog.show()

        // Use a callback to get the video URL asynchronously
        getVideoURL(letter) { videoUrl ->
            progressDialog.dismiss()

            DialogVideo.Builder(requireContext())
                .title("Chữ $letter")
                .urlVideo(videoUrl)
                .show()
        }
    }
}