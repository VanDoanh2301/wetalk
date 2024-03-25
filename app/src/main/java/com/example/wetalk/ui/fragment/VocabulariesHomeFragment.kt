package com.example.wetalk.ui.fragment

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.ContextMenu
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.wetalk.R
import com.example.wetalk.data.model.objectmodel.TopicRequest
import com.example.wetalk.data.model.objectmodel.VocabularyRequest
import com.example.wetalk.databinding.AddTopicDialogBinding
import com.example.wetalk.databinding.FragmentVocabulariesHomeBinding
import com.example.wetalk.ui.activity.MainActivity
import com.example.wetalk.ui.adapter.VocabulariesAdapter
import com.example.wetalk.ui.adapter.VocabularyArrayAdapter
import com.example.wetalk.ui.dialog.DialogBottom
import com.example.wetalk.ui.viewmodels.AdminViewModel
import com.example.wetalk.ui.viewmodels.VideoUpViewModel
import com.example.wetalk.ui.viewmodels.VocabulariesViewModel
import com.example.wetalk.util.ROLE_USER
import com.example.wetalk.util.RealPathUtil
import com.example.wetalk.util.Resource
import com.example.wetalk.util.SharedPreferencesUtils
import com.example.wetalk.util.showToast
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.callback.RequestCallback
import com.rey.material.widget.ImageView
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

@AndroidEntryPoint
class VocabulariesHomeFragment : Fragment() {
    private var _binding: FragmentVocabulariesHomeBinding? = null
    private val binding get() = _binding!!
    private var id = 0
    private val viewModel: VocabulariesViewModel by viewModels()
    private var resultlist: ArrayList<VocabularyRequest> = ArrayList()
    private lateinit var vocabulariesAdapter: VocabulariesAdapter
    private val adminViewModel: AdminViewModel by viewModels()
    private val videoViewModel: VideoUpViewModel by viewModels()
    private lateinit var imgTopicView: ImageView
    private var isVideo = false
    private var imgUrl: String? = null
    private var urlResponse = ""
    private var isAdmin = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentVocabulariesHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        id = arguments?.getInt("id", -1)!!
        vocabulariesAdapter = VocabulariesAdapter(requireContext())
        isAdmin = SharedPreferencesUtils.getString(ROLE_USER).equals("ADMIN")
        init()
        initView()
        initSearch()

    }

    private fun initSearch() {
        binding.apply {
            edtSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    val dataStr = p0.toString()
                    if (!dataStr.equals("")) {
                        val searchList = resultlist.filter {
                            it.content.contains(dataStr, ignoreCase = true)
                        }
                        vocabulariesAdapter.submitList(searchList)
                    } else {
                        vocabulariesAdapter.submitList(resultlist)
                    }

                }

                override fun afterTextChanged(p0: Editable?) {

                }

            })
        }
    }

    private fun initView() {
        binding.btBack.setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }
        vocabulariesAdapter.setOnItemClick(object : VocabulariesAdapter.OnItemClick {
            override fun onItem(position: Int, topicRequest: VocabularyRequest) {
                BaseDialogFragment.add(
                    (activity as MainActivity), TalkPlayVideoFragment.newInstance()
                        .setVideoPath(
                            topicRequest.videoLocation,
                            topicRequest.imageLocation,
                            if (topicRequest.videoLocation.equals("")) 1 else 2
                        )
                )
            }

        })
        vocabulariesAdapter.setOnMoreItem(object : VocabulariesAdapter.OnItemClick {
            override fun onItem(position: Int, topicRequest: VocabularyRequest) {
                if (isAdmin) {
                    DialogBottom.Builder(requireContext())
                        .setTextMore("Chỉnh sửa")
                        .setTextReport("Xóa")
                        .onMoreTip {
                            showUpdateDialog(position,topicRequest.id)
                        }
                        .onReport {
                            showDeleteDialog(position, topicRequest.id)
                        }
                        .show()
                } else {
                    DialogBottom.Builder(requireContext())
                        .setTextMore("Chia sẻ")
                        .setImageMore(R.drawable.setting_share)
                        .onMoreTip {
                            try {
                                val i = Intent(Intent.ACTION_SEND)
                                i.setType("text/plain")
                                i.putExtra(Intent.EXTRA_SUBJECT, "We talk")
                                var sAux = "\n\"We talk\n\n"
                                sAux =
                                    sAux + "https://play.google.com/store/apps/details?id=" + ""
                                i.putExtra(Intent.EXTRA_TEXT, sAux)
                                i.putExtra("position", position)
                                startActivityForResult(Intent.createChooser(i, "Share App"), SHARE_REQUEST_CODE)
                            } catch (e: java.lang.Exception) {
                            }
                        }
                        .show()
                }

            }

        })
    }

    private fun init() {
        lifecycleScope.launchWhenStarted {
            viewModel.getAllVocabulariesByTopicId(id)
            viewModel.vocabularies.collect {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        try {
                            var vocabularies = it.data!!.data
                            resultlist.addAll(vocabularies)
                            binding.apply {
                                val linearLayoutManager = StaggeredGridLayoutManager(
                                    2,
                                    StaggeredGridLayoutManager.VERTICAL
                                )
                                rcvView.layoutManager = linearLayoutManager
                                rcvView.adapter = vocabulariesAdapter
                                vocabulariesAdapter.submitList(vocabularies)
                            }
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


    private fun showUpdateDialog(position:Int ,groupId: Int) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Thêm từ vựng")
        val bindingDialog = AddTopicDialogBinding.inflate(layoutInflater)
        builder.setView(bindingDialog.root)
        bindingDialog.apply {
            imgTopicView = imgLocation

            imgOpen.setOnClickListener {
                openImage()
            }
            imgRecord.setOnClickListener {
                openVideo()
            }
        }
        val selectedCategory = bindingDialog.spTopic.selectedItem as TopicRequest
        val topicId = selectedCategory.id
        builder.setPositiveButton("Đồng ý",
            DialogInterface.OnClickListener { dialog, which ->
                if (bindingDialog.edTopic.text != null) {
                    if (isVideo) {
                        val vocabularyRequest  =
                            VocabularyRequest(
                                groupId,
                                bindingDialog.edTopic.text.toString(),
                                "",
                                urlResponse,id
                            )
                        adminViewModel.updateVocabulary(vocabularyRequest).observe(viewLifecycleOwner) {
                            if (it.isSuccessful) {
                                vocabulariesAdapter.updateItem(position, vocabularyRequest)
                            }
                        }
                    } else {
                        val vocabularyRequest=
                            VocabularyRequest(
                                groupId,
                                bindingDialog.edTopic.text.toString(),
                                urlResponse,
                                "", id
                            )
                        adminViewModel.updateVocabulary(vocabularyRequest).observe(viewLifecycleOwner) {
                            if (it.isSuccessful) {
                                vocabulariesAdapter.updateItem(position, vocabularyRequest)
                            }
                        }
                    }

                }
            })
        builder.setNegativeButton("Hủy bỏ",
            DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            })
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showDeleteDialog(position: Int,groupId: Int) {
           adminViewModel.deleteVocabulary(groupId).observe(viewLifecycleOwner) {
               if (it.isSuccessful) {
                   vocabulariesAdapter.removeItem(position)
               }
           }
    }

    private fun openImage() {
        PermissionX.init(this@VocabulariesHomeFragment)
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
    }

    private fun openVideo() {
        PermissionX.init(this@VocabulariesHomeFragment)
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TopicStudyFragment.PICK_IMAGE_REQUEST && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            val imageUri = data.data
            imgUrl = RealPathUtil.getRealPath(requireContext(), imageUri)
            Glide.with(requireContext()).load(imageUri).into(imgTopicView)
            isVideo = false

        }
        if (requestCode == TopicStudyFragment.PICK_VIDEO_REQUEST && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            val imageUri = data.data
            imgUrl = RealPathUtil.getRealPath(requireContext(), imageUri)
            Glide.with(requireContext()).load(imageUri).into(imgTopicView)
            isVideo = true

        }
        if (imgUrl != null) {
            getUrlFile(imgUrl!!)
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
            videoViewModel.uploadVideo(filePart)
        } catch (e: Exception) {
            requireContext().showToast()

        }
        lifecycleScope.launchWhenResumed {
            videoViewModel.uploadResult.observe(
                viewLifecycleOwner
            ) {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        urlResponse = it.data.toString()
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    companion object {
        @JvmStatic
        fun newInstance() =
            VocabulariesHomeFragment().apply {
                arguments = Bundle().apply {

                }
            }

        const val PICK_IMAGE_REQUEST = 1
        const val PICK_VIDEO_REQUEST = 2
        const val SHARE_REQUEST_CODE = 3

    }
}