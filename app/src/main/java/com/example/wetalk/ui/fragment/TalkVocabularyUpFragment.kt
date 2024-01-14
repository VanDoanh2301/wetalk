package com.example.wetalk.ui.fragment

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wetalk.data.local.StorageImageItem
import com.example.wetalk.data.local.VideoBody
import com.example.wetalk.data.local.VideoBodyItem
import com.example.wetalk.data.local.VideoLocal
import com.example.wetalk.databinding.FragmentTalkVocabularyUpBinding
import com.example.wetalk.ui.activity.MainActivity
import com.example.wetalk.ui.adapter.TalkDialogTag
import com.example.wetalk.ui.customview.TalkBodyEditView
import com.example.wetalk.ui.viewmodels.TalkVocabularyViewModel
import com.example.wetalk.util.DialogClose
import com.example.wetalk.util.DialogVideo
import com.example.wetalk.util.RealPathUtil
import com.example.wetalk.util.Resource
import com.example.wetalk.util.Task
import com.example.wetalk.util.Utils
import com.example.wetalk.util.helper.FileHelper
import com.example.wetalk.util.helper.KeyboardHeightProvider
import com.example.wetalk.util.helper.permission_utils.Func
import com.example.wetalk.util.helper.permission_utils.PermissionUtil
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * A simple [Fragment] subclass.
 * Use the [TalkVocabularyUpFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class TalkVocabularyUpFragment : Fragment() {

    private val viewModel: TalkVocabularyViewModel by viewModels()
    private lateinit var keyboardHeightProvider: KeyboardHeightProvider
    private lateinit var videoLocal: VideoLocal
    private lateinit var talkBodyEditView: TalkBodyEditView
    private lateinit var menuCallback: MenuCallback
    private var _binding: FragmentTalkVocabularyUpBinding? = null
    private val binding get() = _binding!!
    private lateinit var alertDialog: AlertDialog
    private val paths = ArrayList<String>()
    private var devicePath: String? = null
    private var uri: Uri? = null
    private lateinit var talkImageItems: ArrayList<StorageImageItem>
    private lateinit var mRequestObject: PermissionUtil.PermissionRequestObject

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTalkVocabularyUpBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        talkBodyEditView = binding.bodyView


        keyboardHeightProvider = KeyboardHeightProvider(requireActivity());
        videoLocal = VideoLocal(
            -1, System.currentTimeMillis(), "",
            VideoBody(ArrayList<VideoBodyItem>()), "", 1
        )
        lifecycleScope.launchWhenStarted {
            viewModel.videoLocal.collect {
                // Update UI with the videoLocal data
                talkBodyEditView.preview((activity as MainActivity), it ?: videoLocal)
                videoLocal = it
            }

        }
        lifecycleScope.launchWhenResumed {
            viewModel.talkImageItems.collect { talkImageItems ->
                if (talkImageItems == null) {

                    talkBodyEditView.visibility = View.GONE
                } else {
                    talkBodyEditView.visibility = View.VISIBLE
                    talkBodyEditView.addImage(talkImageItems)
                }

            }
        }

        viewModel.uploadResult.observe(
            viewLifecycleOwner
        ) {
            when (it) {
                is Resource.Loading -> {

                }

                is Resource.Success -> {
                    val progressDialog = ProgressDialog(requireContext())
                    progressDialog.setTitle("Đang tải video lên")
                    progressDialog.setMessage("Xin chờ...")
                    progressDialog.show()
                    lifecycleScope.launch {
                        delay(3000)
                        Toast.makeText(
                            requireContext(),
                            "Đăng video thành công",
                            Toast.LENGTH_SHORT
                        ).show()
                        progressDialog.dismiss()
                    }
                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.imgRecord.setOnClickListener {
            val i: Intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            startActivityForResult(i, 1111)
        }
//        openDialog()
        onRecord()
        onCallBack()
        onFolder();
        onBack();
        onUploadVideo();
        openStarVideo()

    }
    private fun openStarVideo() {
        binding.btnStar.setOnClickListener {
            if (!binding.tvName.text.equals("Hastag")) {
                showVideoDialog(binding.tvName.text.toString())
            } else {
                Toast.makeText(requireContext(), "Vui lòng chọn Hastag để xem video mẫu", Toast.LENGTH_LONG).show()
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
                Log.e("FirebaseStorage", "Error downloading video: ${it.message}")
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
    private fun openDialog() {
        DialogClose.Builder(requireContext())
            .title("Gợi ý")
            .cancelable(true)
            .canceledOnTouchOutside(true)
            .content("Ấn Hastag để chọn chủ đề bạn muốn cung cấp và bạn có thể xem video mẫu để thực hiện")
            .doneText("Oke")
            .onDone {

            }
            .show()
    }

    private fun onUploadVideo() {
        binding.cvSave.setOnClickListener {
            try {
                val file = File(devicePath)
                val requestFile =
                    RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
                val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
                viewModel.uploadVideo(filePart)
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Vui lòng chọn video cung cấp của bạn",
                    Toast.LENGTH_LONG
                ).show()
            }

        }
    }

    private fun onBack() {
        binding.btBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun onFolder() {
        binding.imgOpen.setOnClickListener {
            mRequestObject = PermissionUtil.with(activity as MainActivity)
                .request(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .onAllGranted(object : Func() {
                    override fun call() {
                        Utils.hideKeyboard(activity)
                        menuCallback.clearCursor()
                        menuCallback.scrollBottom()
                        BaseFragment.add(
                            activity as MainActivity,
                            TalkSelectVideoFragment.newInstance().setVideoTask(
                                object : Task<ArrayList<StorageImageItem>> {
                                    override fun callback(result: ArrayList<StorageImageItem>) {
                                        menuCallback.addMedia(result)
                                        devicePath = result[0].devicePath
                                    }
                                }
                            )
                        )

                    }
                }).ask(12)
        }
        binding.imgOpenTag.setOnClickListener {
            showDialogTag()
        }
    }


    private fun onCallBack() {
        menuCallback = object : MenuCallback {
            override fun addMedia(result: ArrayList<StorageImageItem>) {
                FileHelper.checkAndCopyImage(
                    activity as MainActivity,
                    result,
                    object : Task<ArrayList<StorageImageItem>> {
                        override fun callback(result: ArrayList<StorageImageItem>) {
                            viewModel.addImageItems(result)
                        }
                    })
            }

            override fun addTag() {}
            override fun list(lineType: Int) {}
            override fun clearCursor() {}
            override fun scrollBottom() {}
        }
    }

    /** Permission open camera */
    private fun onRecord() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                111
            )
        } else {
            binding.imgRecord.isEnabled = true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        paths.clear()
        if (requestCode == 1111) {
            try {
                uri = data!!.data
                paths.add(data!!.data.toString())
                talkImageItems = ArrayList<StorageImageItem>()
                for (devicePath in paths) {
                    talkImageItems.add(
                        StorageImageItem(
                            true,
                            devicePath,
                            devicePath,
                            if (paths.size > 2) 25 else 45,
                            0
                        )
                    )
                }
                viewModel.addImageItems(talkImageItems)
                devicePath = RealPathUtil.getRealPath(requireContext(), uri)
            } catch (e: Exception) {
            }
        }
//        talkBodyEditView.addImage(talkImageItems)

    }

    interface MenuCallback {
        fun addMedia(diaryImageItems: ArrayList<StorageImageItem>)
        fun addTag()
        fun list(lineType: Int)
        fun clearCursor()
        fun scrollBottom()
    }

    private fun showDialogTag() {
        val builder = AlertDialog.Builder(
            requireContext()
        )
        val recyclerView = RecyclerView(requireContext())
        recyclerView.layoutManager = GridLayoutManager(context, 5)
        var dataList = ArrayList<String>()
        dataList = charactersList

        val talkDialogTag = object : TalkDialogTag(requireContext()) {
            override fun OnClickItemTag(position: Int) {
                val item = dataList[position]
                binding.tvName.text = "$item"
                videoLocal.videoTag = binding.tvName.text.toString()
                alertDialog.dismiss()
            }
        }
        talkDialogTag.setData(dataList)
        recyclerView.adapter = talkDialogTag
        builder.setView(recyclerView)
        builder.setPositiveButton("Đóng", null)
        alertDialog = builder.create()
        alertDialog.show()
    }
    private val charactersList: ArrayList<String> by lazy {
        val result = ArrayList<String>()

        for (baseChar in 'A'..'Z') {
            result.add(baseChar.toString())  // Add the base character without diacritic
        }
        result.add("\u1EA5")
        result.add("\u00E0")
        result.add("\u00E3")
        result.add("\u1EA1")

        for (digit in 0..9) {
            result.add(digit.toString())
        }

        result
    }

    override fun onPause() {
        super.onPause()
        keyboardHeightProvider.setKeyboardHeightObserver(null)
    }

}