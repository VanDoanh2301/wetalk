package com.example.wetalk.ui.fragment

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.wetalk.R
import com.example.wetalk.data.model.objectmodel.TopicRequest
import com.example.wetalk.data.model.objectmodel.VocabularyRequest
import com.example.wetalk.data.model.postmodel.DataPost
import com.example.wetalk.data.model.postmodel.MediaValidatePost
import com.example.wetalk.databinding.DialogTopicBinding
import com.example.wetalk.databinding.FragmentTalkVocabularyUpBinding
import com.example.wetalk.ui.activity.MainActivity
import com.example.wetalk.ui.adapter.DialogTagAdapter
import com.example.wetalk.ui.adapter.DialogVocabularyAdapter
import com.example.wetalk.ui.dialog.DialogCloseTest
import com.example.wetalk.ui.viewmodels.TopicViewModel
import com.example.wetalk.ui.viewmodels.VideoUpViewModel
import com.example.wetalk.ui.viewmodels.VocabulariesViewModel
import com.example.wetalk.ui.dialog.DialogOpenVideo
import com.example.wetalk.util.FileConfigUtils
import com.example.wetalk.util.RealPathUtil
import com.example.wetalk.util.Resource
import com.example.wetalk.util.helper.permission_utils.Func
import com.example.wetalk.util.helper.permission_utils.PermissionUtil
import com.example.wetalk.util.showToast
import com.google.firebase.storage.FirebaseStorage
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.callback.RequestCallback
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * A simple [Fragment] subclass.
 * Use the [ProvideVideoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class ProvideVideoFragment : Fragment() {
    private val viewModel: VideoUpViewModel by viewModels()
    private val topicViewModel: TopicViewModel by viewModels()
    private val vocabulariesViewModel: VocabulariesViewModel by viewModels()
    private var _binding: FragmentTalkVocabularyUpBinding? = null
    private val binding get() = _binding!!
    private lateinit var alertDialog: AlertDialog
    private var vocabularyId = 0
    private var urlResponse = ""
    private var urlLocation :Uri ? = null
    private var imgUrl = ""
    private var isVideo = false
    private var resultTopics: ArrayList<TopicRequest> = ArrayList()
    private var resultVocabularies: ArrayList<VocabularyRequest> = ArrayList()
    private lateinit var mRequestObject: PermissionUtil.PermissionRequestObject
    private var vocabulariesRequest: VocabularyRequest? = null
    private val TAG = "ProvideVideoFragment"

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
        initDataTopic()
        onClickView()
        onBack();
        openStarVideo()

    }


    private fun onClickView() {
        binding.btHistoryView.setOnClickListener {
            findNavController().navigate(R.id.action_talkVocabularyUpFragment_to_talkHistoryVocabulariesFragment)
        }
        binding.imgOpenTag.setOnClickListener {
            showDialogTag()
        }
        binding.imgOpen.setOnClickListener {
            openFolder(it)
        }
        binding.imgRecord.setOnClickListener {
            onRecord()
        }
        binding.imgOpenVocabularies.setOnClickListener {
            if (binding.tvName.text.equals("Chủ đề")) {
                requireContext().showToast("Vui lòng chọn chủ đề")
            } else {
                showDialogVocabularies()
            }
        }
        binding.cdSave.setOnClickListener {
            if (imgUrl != null) {
                getUrlFile(imgUrl!!)
            }
        }
        binding.imgSelect.setOnClickListener {
            BaseDialogFragment.add(activity as MainActivity, PlayVideoFragment.newInstance().setVideoPath(
                if (isVideo) imgUrl else "", if (isVideo) "" else  imgUrl, if (isVideo) 2 else 1
            ))
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
        val dataList = ArrayList<VocabularyRequest>()
        dataList.addAll(resultVocabularies)
        val dialogTagAdapter = object : DialogVocabularyAdapter(requireContext()) {
            override fun OnClickItemTag(position: Int) {
                val item = dataList[position]
                vocabulariesRequest = item
                binding.tvTitlle.text = item.content
                vocabularyId = item.id
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

    private fun onResultValid(urlStrResponse:String) {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Vui lòng chờ xác thực")
        progressDialog.setMessage("Xin chờ...")
        viewModel.validMedia.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    progressDialog.dismiss()
                    requireContext().showToast(it.data!!.message)
                    if (vocabularyId > 0 ) {
                        val postData = DataPost(
                            dataCollection = urlStrResponse,
                            vocabularyId = vocabularyId
                        )
                        vocabulariesViewModel.postDataCollection(postData).observe(viewLifecycleOwner) {
                            if (it.isSuccessful) {
                                requireContext().showToast("Dữ liệu được cung câp. Chờ phê duyệt của quản trị viên")
                            }
                        }
                    }

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
        startActivityForResult(intent, PICK_CAMERA_REQUEST)
    }

    private fun openStarVideo() {
        binding.btnStar.setOnClickListener {
            if (!binding.tvName.text.equals("Từ vựng") || !binding.tvName.text.equals("")) {
                showVideoDialog(binding.tvTitlle.text.toString())
            } else {
                Toast.makeText(
                    requireContext(),
                    "Vui lòng chọn chủ đề để xem video mẫu",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }

    private fun showVideoDialog(content: String) {
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
            vocabulariesRequest!!.videoLocation?.let {
                DialogOpenVideo.Builder(requireContext()).title("Chữ ${vocabulariesRequest!!.content}")
                    .urlVideo(it).show()
            }
        }
        progressDialog.dismiss()
    }



    private fun onBack() {
        binding.btBack.setOnClickListener {
            DialogCloseTest.Builder(requireContext())
                .title("Thoát")
                .content("Bạn có chắc muốn thoát ?")
                .positiveText("Đồng ý")
                .negativeText("Đóng")
                .onPositive {
                    requireActivity().onBackPressed()
                }
                .onNegative {

                }
                .show()

        }
    }


    private fun openFolder(view: View) {
        FileConfigUtils.hideKeyboard(activity)
            val popupMenu = PopupMenu(requireContext(), view)
            popupMenu.inflate(R.menu.menu_select_folder)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_item_1 -> {
                        openImage()
                        true
                    }

                    R.id.menu_item_2 -> {
                        openVideo()
                        true
                    }

                    else -> false
                }
            }
            popupMenu.show()
    }
    private fun openImage() {
        if (Build.VERSION.SDK_INT > 33) {
            PermissionX.init(this@ProvideVideoFragment)
                .permissions(
                    Manifest.permission.READ_MEDIA_IMAGES
                )
                .request(object : RequestCallback {
                    override fun onResult(
                        allGranted: Boolean,
                        grantedList: MutableList<String>,
                        deniedList: MutableList<String>
                    ) {
                        if (allGranted) {
                            val intent =
                                Intent(
                                    Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                                )
                            startActivityForResult(
                                intent,
                                TopicStudyFragment.PICK_IMAGE_REQUEST
                            )

                        } else {
                            requireContext().showToast("Bạn cần chấp nhận tất cả các quyền")
                        }
                    }
                })
        } else {
            PermissionX.init(this@ProvideVideoFragment)
                .permissions(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .request(object : RequestCallback {
                    override fun onResult(
                        allGranted: Boolean,
                        grantedList: MutableList<String>,
                        deniedList: MutableList<String>
                    ) {
                        if (allGranted) {
                            val intent =
                                Intent(
                                    Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                                )
                            startActivityForResult(
                                intent,
                                TopicStudyFragment.PICK_IMAGE_REQUEST
                            )

                        } else {
                            requireContext().showToast("Bạn cần chấp nhận tất cả các quyền")
                        }
                    }
                })
        }

    }

    private fun openVideo() {
        if (Build.VERSION.SDK_INT > 33) {
            PermissionX.init(this@ProvideVideoFragment)
                .permissions(
                    Manifest.permission.READ_MEDIA_VIDEO
                )
                .request(object : RequestCallback {
                    override fun onResult(
                        allGranted: Boolean,
                        grantedList: MutableList<String>,
                        deniedList: MutableList<String>
                    ) {
                        if (allGranted) {
                            val intent =
                                Intent(
                                    Intent.ACTION_PICK,
                                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                                )
                            startActivityForResult(
                                intent,
                                TopicStudyFragment.PICK_VIDEO_REQUEST
                            )

                        } else {
                            requireContext().showToast("Bạn cần chấp nhận tất cả các quyền")
                        }
                    }
                })
        } else {
            PermissionX.init(this@ProvideVideoFragment)
                .permissions(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .request(object : RequestCallback {
                    override fun onResult(
                        allGranted: Boolean,
                        grantedList: MutableList<String>,
                        deniedList: MutableList<String>
                    ) {
                        if (allGranted) {
                            val intent =
                                Intent(
                                    Intent.ACTION_PICK,
                                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                                )
                            startActivityForResult(
                                intent,
                                TopicStudyFragment.PICK_VIDEO_REQUEST
                            )

                        } else {
                            requireContext().showToast("Bạn cần chấp nhận tất cả các quyền")
                        }
                    }
                })
        }

    }

    private fun getUrlFile(devicePath: String) {
        try {
            if (devicePath.isNullOrEmpty()) {
                return
            }
            val file = File(devicePath)
            val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
            val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
            viewModel.uploadVideo(filePart)
        } catch (e: Exception) {
            requireContext().showToast()

        }
        lifecycleScope.launchWhenResumed {
            viewModel.uploadResult.observe(
                viewLifecycleOwner
            ) {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        urlResponse = it.data.toString()
                        val validatePost = MediaValidatePost(urlResponse, binding.tvTitlle.text.toString())
                        viewModel.getValidMedia(validatePost)
                        onResultValid(urlResponse)
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }





    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_CAMERA_REQUEST && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            try {
                val imageUri = data.data
                urlLocation = imageUri
                imgUrl = RealPathUtil.getRealPath(requireContext(), imageUri)
                isVideo = true
            } catch (e: Exception) {
            }
        }
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            val imageUri = data.data
            imgUrl = RealPathUtil.getRealPath(requireContext(), imageUri)
            urlLocation = imageUri
            Glide.with(requireContext()).load(imageUri).into(binding.imgSelect)
            isVideo = false

        }
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            val imageUri = data.data
            imgUrl = RealPathUtil.getRealPath(requireContext(), imageUri)
            urlLocation = imageUri
            Glide.with(requireContext()).load(imageUri).into(binding.imgSelect)
            isVideo = true

        }
        if (urlLocation != null) {
            binding.imgSelect.visibility = View.VISIBLE
            binding.imgNone.visibility = View.GONE
            binding.imgPlay.visibility= View.VISIBLE
            Glide.with(requireContext()).load(urlLocation).into(binding.imgSelect)
        } else {
            binding.imgSelect.visibility = View.GONE
            binding.imgNone.visibility = View.VISIBLE
            binding.imgPlay.visibility= View.GONE

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
    companion object {
        const val PICK_IMAGE_REQUEST = 1
        const val PICK_VIDEO_REQUEST = 2
        const val PICK_CAMERA_REQUEST = 3
    }
    override fun onPause() {
        super.onPause()
    }

}