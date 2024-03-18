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
        // Inflate the layout for this fragment
        if (!hasPermission()) {
            requestPermission()
        }
        _binding = FragmentTalkVocabularyUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val originalFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
        val currentDate = LocalDate.now().format(originalFormat)
        binding.tvDate.text = currentDate.toString()

        init()
        initDataTopic()
        onClickView()
        onRecord()
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
                Toast.makeText(requireContext(), "Vui lòng chọn chủ đề", Toast.LENGTH_LONG).show()
            } else {
                showDialogVocabularies()
            }
        }
    }

    private fun showDialogVocabularies() {
        val builder = AlertDialog.Builder(
            requireContext()
        )
        val recyclerView = RecyclerView(requireContext())
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        var dataList = ArrayList<TopicRequest>()
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
        builder.setView(recyclerView)
        builder.setTitle("Chủ đề")
        builder.setPositiveButton("Đóng", null)
        alertDialog = builder.create()
        alertDialog.show()
    }

    private fun showDialogTag() {
        val builder = AlertDialog.Builder(
            requireContext()
        )
        val recyclerView = RecyclerView(requireContext())
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
        builder.setView(recyclerView)
        builder.setTitle("Từ vựng theo chủ đề")
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
                            Toast.makeText(requireContext(), "Topic is Null", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                    is Resource.Loading -> {
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
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
                    val progressDialog = ProgressDialog(requireContext())
                    progressDialog.setTitle("Vui lòng chờ xác thực")
                    progressDialog.setMessage("Xin chờ...")
                    progressDialog.show()

                    val validatePost = MediaValidatePost(urlStr, "")
                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.imgRecord.setOnClickListener {
            if (hasPermission()) {
                launchCamera()
            } else {
                requestPermission()
            }
        }
    }

    private fun launchCamera() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(intent, 1111)
    }

    private fun openStarVideo() {
        binding.btnStar.setOnClickListener {
            if (!binding.tvName.text.equals("Từ vựng")) {
                if (vocabulariesRequest != null) {
                    showVideoDialog(vocabulariesRequest!!)
                } else {
                    Toast.makeText(requireContext(), "Vui lòng chọn từ vựng để xem video mẫu", Toast.LENGTH_LONG).show() }

            } else {
                Toast.makeText(requireContext(), "Vui lòng chọn chủ đề để xem video mẫu", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun showVideoDialog(topicRequest: TopicRequest) {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Đang tải")
        progressDialog.setMessage("Xin chờ...")
        progressDialog.show()
        DialogOpenVideo.Builder(requireContext()).title("Chữ ${topicRequest.content}").urlVideo(topicRequest.videoLocation).show()
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



            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Đã xảy ra lỗi khi xử lý tệp", Toast.LENGTH_LONG).show()
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
            override fun clearCursor() {}
            override fun scrollBottom() {}
        }
    }

    /** Permission open camera */
    private fun onRecord() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), 111)
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

    /**
     * PermissionPermission Camera, Read and Write File
     */
    private fun requestPermission() {
        if (shouldShowRequestPermissionRationale(PERMISSION_CAMERA) && shouldShowRequestPermissionRationale(
                PERMISSION_READ_EXTERNAL_STORAGE
            ) && shouldShowRequestPermissionRationale(
                PERMISSION_WRITE_EXTERNAL_STORAGE
            )
        ) {
            Toast.makeText(
                requireContext(), "Camera permission is required for this demo", Toast.LENGTH_LONG
            ).show()
        }

        requestPermissions(
            arrayOf(
                PERMISSION_CAMERA,
                PERMISSION_READ_EXTERNAL_STORAGE,
                PERMISSION_WRITE_EXTERNAL_STORAGE
            ), PERMISSIONS_REQUEST
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST) {
            if (allPermissionsGranted(grantResults)) {
            } else {
                requestPermission()
            }
        }
    }


    private fun allPermissionsGranted(grantResults: IntArray): Boolean {
        for (result in grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun hasPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            (activity as MainActivity).checkSelfPermission(PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED
            (activity as MainActivity).checkSelfPermission(PERMISSION_READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            (activity as MainActivity).checkSelfPermission(PERMISSION_WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    override fun onPause() {
        super.onPause()
        keyboardHeightProvider.setKeyboardHeightObserver(null)
    }

    companion object {
        private val PERMISSIONS_REQUEST = 1
        private val PERMISSION_CAMERA = Manifest.permission.CAMERA
        private val PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE
        private val PERMISSION_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
    }
}