package com.example.wetalk.ui.fragment

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.marginRight
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wetalk.R
import com.example.wetalk.data.local.StorageImageItem
import com.example.wetalk.data.local.VideoBody
import com.example.wetalk.data.local.VideoBodyItem
import com.example.wetalk.data.local.VideoLocal
import com.example.wetalk.data.model.objectmodel.TopicRequest
import com.example.wetalk.data.model.postmodel.MediaValidatePost
import com.example.wetalk.databinding.DialogTopicBinding
import com.example.wetalk.databinding.FragmentTalkVocabularyUpBinding
import com.example.wetalk.ui.activity.MainActivity
import com.example.wetalk.ui.adapter.DialogTagAdapter
import com.example.wetalk.ui.customview.TalkBodyEditView
import com.example.wetalk.ui.viewmodels.TopicViewModel
import com.example.wetalk.ui.viewmodels.VideoUpViewModel
import com.example.wetalk.ui.viewmodels.VocabulariesViewModel
import com.example.wetalk.util.dialog.DialogOpenVideo
import com.example.wetalk.util.FileConfigUtils
import com.example.wetalk.util.RealPathUtil
import com.example.wetalk.util.Resource
import com.example.wetalk.util.Task
import com.example.wetalk.util.helper.FileHelper
import com.example.wetalk.util.helper.KeyboardHeightProvider
import com.example.wetalk.util.helper.permission_utils.Func
import com.example.wetalk.util.helper.permission_utils.PermissionUtil
import com.example.wetalk.util.showToast
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * A simple [Fragment] subclass.
 * Use the [TalkProvideVideoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class TalkProvideVideoFragment : Fragment() {
    private val viewModel: VideoUpViewModel by viewModels()
    private val topicViewModel: TopicViewModel by viewModels()
    private val vocabulariesViewModel: VocabulariesViewModel by viewModels()
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
    private var topicId = 0
    private var resultTopics: ArrayList<TopicRequest> = ArrayList()
    private var resultVocabularies: ArrayList<TopicRequest> = ArrayList()
    private lateinit var talkImageItems: ArrayList<StorageImageItem>
    private lateinit var mRequestObject: PermissionUtil.PermissionRequestObject
    private var vocabulariesRequest: TopicRequest? = null
    private val TAG = "TalkProvideVideoFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTalkVocabularyUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val originalFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
        val currentDate = LocalDate.now().format(originalFormat)
        binding.tvDate.text = currentDate.toString()
        mRequestObject = PermissionUtil.with(activity as MainActivity).request(
            Manifest.permission.CAMERA
        ).onAllGranted(object : Func() {
            override fun call() {
            }
        }).ask(12)
        mRequestObject = PermissionUtil.with(activity as MainActivity).request(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).onAllGranted(object : Func() {
            override fun call() {
            }
        }).ask(12)

        init()
        initDataTopic()
        onClickView()
        onCallBack()
        onBack();
        onUploadVideo();
        openStarVideo()
        onFolder()

    }
    private fun onClickView() {
        binding.btHistoryView.setOnClickListener {
            findNavController().navigate(R.id.action_talkVocabularyUpFragment_to_talkHistoryVocabulariesFragment)
        }
        binding.imgOpenTag.setOnClickListener {
            showDialogTag()
        }

        binding.imgOpenVocabularies.setOnClickListener {
            if (binding.tvName.text.equals("Chủ đề")) {
                requireContext().showToast("Vui lòng chọn chủ đề")
            } else {
                showDialogVocabularies()
            }
        }
    }
    private fun showDialogVocabularies() {
        val builder = AlertDialog.Builder(
            requireContext()
        )
        val inflater = LayoutInflater.from(requireContext())
        val dialogLayout = DialogTopicBinding.inflate(inflater)
        val recyclerView = dialogLayout.recyclerView
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        val dataList = ArrayList<TopicRequest>()
        dataList.addAll(resultVocabularies)
        val dialogTagAdapter = object : DialogTagAdapter(requireContext()) {
            override fun OnClickItemTag(position: Int) {
                val item = dataList[position]
                vocabulariesRequest = item
                binding.tvTitlle.text = item.content
                videoLocal.videoTag = binding.tvName.text.toString()
                alertDialog.dismiss()
            }
        }
        dialogTagAdapter.setData(dataList)
        recyclerView.adapter = dialogTagAdapter
        builder.setView(dialogLayout.root)
        builder.setPositiveButton("Đóng", null)
        builder.setPositiveButton("Lưu") { dialog, which ->
            val enteredText = dialogLayout.editText.text.toString()
            binding.tvTitlle.text = enteredText
            dialog.dismiss()
        }

        alertDialog = builder.create()
        alertDialog.show()
    }

    private fun showDialogTag() {
        val builder = AlertDialog.Builder(
            requireContext()
        )
        val inflater = LayoutInflater.from(requireContext())
        val dialogLayout = DialogTopicBinding.inflate(inflater)
        val recyclerView = dialogLayout.recyclerView
        dialogLayout.tvContent.text = "Chủ đề"
        dialogLayout.editText.visibility = View.GONE
        dialogLayout.tvHelp.visibility = View.GONE
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        var dataList = ArrayList<TopicRequest>()
        dataList.addAll(resultTopics)
        val dialogTagAdapter = object : DialogTagAdapter(requireContext()) {
            override fun OnClickItemTag(position: Int) {
                val item = dataList[position]
                binding.tvName.text = item.content
                videoLocal.videoTag = binding.tvName.text.toString()
                initDataVocabularies(item.id)
                alertDialog.dismiss()
            }
        }
        dialogTagAdapter.setData(dataList)
        recyclerView.adapter = dialogTagAdapter
        builder.setView(dialogLayout.root)
        builder.setPositiveButton("Đóng", null)
        alertDialog = builder.create()
        alertDialog.show()
    }

    private fun initDataVocabularies(topicId: Int) {
        lifecycleScope.launchWhenStarted {
            vocabulariesViewModel.getAllVocabulariesByTopicId(topicId)
            vocabulariesViewModel.vocabularies.collect {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        try {
                            var vocabularies = it.data!!.data
                            resultVocabularies.addAll(vocabularies)
                        } catch (e: Exception) {
                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }
    private fun initDataTopic() {
        lifecycleScope.launchWhenStarted {
            topicViewModel.getAllTopic()
            topicViewModel.topic.collect {
                when (it) {
                    is Resource.Success -> {
                        val topicRequests = it.data!!.data
                        try {
                            resultTopics.addAll(topicRequests)
                        } catch (e: Exception) {
                            requireContext().showToast()
                        }
                    }
                    is Resource.Loading -> {
                    }
                    is Resource.Error -> {
                        requireContext().showToast()
                    }

                }
            }

        }
    }

    private fun init() {
        talkBodyEditView = binding.bodyView
        keyboardHeightProvider = KeyboardHeightProvider(requireActivity());
        videoLocal = VideoLocal(
            -1, System.currentTimeMillis(), "", VideoBody(ArrayList<VideoBodyItem>()), "", 1
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
                    val urlStr = it.data.toString()
                    val validatePost = MediaValidatePost(urlStr, binding.tvTitlle.text.toString())
                    viewModel.getValidMedia(validatePost)
                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.imgRecord.setOnClickListener {
            onRecord()

        }
    }
    private fun onResultValid() {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Vui lòng chờ xác thực")
        progressDialog.setMessage("Xin chờ...")
        viewModel.validMedia.observe(viewLifecycleOwner) {
            when(it) {
                is Resource.Success -> {
                    progressDialog.dismiss()
                    requireContext().showToast(it.data!!.message)
                }
                is Resource.Error -> {
                    progressDialog.dismiss()
                    requireContext().showToast()
                }
                is Resource.Loading -> {
                    progressDialog.show()
                }
            }
        }
    }

    private fun onRecord() {
        mRequestObject = PermissionUtil.with(activity as MainActivity).request(
            Manifest.permission.CAMERA
        ).onAllGranted(object : Func() {
            override fun call() {
                launchCamera()
            }
        }).ask(12)

    }
    private fun launchCamera() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(intent, 1111)
    }

    private fun openStarVideo() {
        binding.btnStar.setOnClickListener {
            if (!binding.tvName.text.equals("Từ vựng") || !binding.tvName.text.equals("")) {
                    showVideoDialog(binding.tvTitlle.text.toString())
            } else {
                Toast.makeText(requireContext(), "Vui lòng chọn chủ đề để xem video mẫu", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun showVideoDialog(content:String) {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Đang tải")
        progressDialog.setMessage("Xin chờ...")
        progressDialog.show()

        if (vocabulariesRequest == null) {
            getVideoURL(content) { videoUrl ->
                progressDialog.dismiss()
                DialogOpenVideo.Builder(requireContext())
                    .title("Chữ $content")
                    .urlVideo(videoUrl)
                    .show()
            }
        } else {
            DialogOpenVideo.Builder(requireContext()).title("Chữ ${vocabulariesRequest!!.content}").urlVideo(vocabulariesRequest!!.videoLocation).show()
        }
        progressDialog.dismiss()
    }

    private fun onUploadVideo() {
        binding.cvSave.setOnClickListener {
            try {
                if (devicePath.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "Vui lòng chọn video cung cấp của bạn", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                val file = File(devicePath)
                val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
                val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
                viewModel.uploadVideo(filePart)
                onResultValid()
            } catch (e: Exception) {
                requireContext().showToast()
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
            onOpenFolder()
        }
    }

    private fun onOpenFolder() {
        mRequestObject = PermissionUtil.with(activity as MainActivity).request(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).onAllGranted(object : Func() {
            override fun call() {
                FileConfigUtils.hideKeyboard(activity)
                menuCallback.clearCursor()
                menuCallback.scrollBottom()
                BaseDialogFragment.add(
                    activity as MainActivity,
                    TalkSelectVideoDialogFragment.newInstance()
                        .setVideoTask(object : Task<ArrayList<StorageImageItem>> {
                            override fun callback(result: ArrayList<StorageImageItem>) {
                                menuCallback.addMedia(result)
                                devicePath = result[0].devicePath
                            }
                        })
                )

            }
        }).ask(12)
    }

    private fun onCallBack() {
        menuCallback = object : MenuCallback {
            override fun addMedia(result: ArrayList<StorageImageItem>) {
                FileHelper.checkAndCopyImage(activity as MainActivity,
                    result,
                    object : Task<ArrayList<StorageImageItem>> {
                        override fun callback(result: ArrayList<StorageImageItem>) {
                            viewModel.addImageItems(result)
                        }
                    })
            }
            override fun addTag() {}
            override fun list(lineType: Int) {}
            override fun clearCursor() {
               val  result: ArrayList<StorageImageItem> = ArrayList()
                viewModel.addImageItems(result)
            }
            override fun scrollBottom() {}
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
                for (devicePath in paths) { talkImageItems.add(StorageImageItem(true, devicePath, devicePath, if (paths.size > 2) 25 else 45, 0)) }
                viewModel.addImageItems(talkImageItems)
                devicePath = RealPathUtil.getRealPath(requireContext(), uri)
            } catch (e: Exception) {
            }
        }
    }

    interface MenuCallback {
        fun addMedia(imageItems: ArrayList<StorageImageItem>)
        fun addTag()
        fun list(lineType: Int)
        fun clearCursor()
        fun scrollBottom()
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

    override fun onPause() {
        super.onPause()
        keyboardHeightProvider.setKeyboardHeightObserver(null)
    }

}