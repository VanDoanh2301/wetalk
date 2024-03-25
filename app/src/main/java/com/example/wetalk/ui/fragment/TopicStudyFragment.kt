package com.example.wetalk.ui.fragment

import TopicAdapter
import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.wetalk.R
import com.example.wetalk.data.model.objectmodel.TopicRequest
import com.example.wetalk.data.model.postmodel.TopicPost
import com.example.wetalk.databinding.AddTopicDialogBinding

import com.example.wetalk.databinding.FragmentTalkSignBinding
import com.example.wetalk.ui.adapter.VocabularyArrayAdapter
import com.example.wetalk.ui.viewmodels.AdminViewModel
import com.example.wetalk.ui.viewmodels.TopicViewModel
import com.example.wetalk.ui.viewmodels.VideoUpViewModel
import com.example.wetalk.util.RealPathUtil
import com.example.wetalk.util.Resource
import com.example.wetalk.ui.dialog.DialogBottom
import com.example.wetalk.util.ROLE_USER
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

/**
 * A simple [Fragment] subclass.
 * Use the [TopicStudyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class TopicStudyFragment : Fragment() {
    private var _binding: FragmentTalkSignBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TopicAdapter
    private val viewModel: TopicViewModel by viewModels()
    private val adminViewModel: AdminViewModel by viewModels()
    private val videoViewModel: VideoUpViewModel by viewModels()
    private var topicRequests: ArrayList<TopicRequest>? = null
    private var isAdmin = false
    private var imgUrl: String? = null
    private var urlResponse = ""
    private var spTopics: ArrayList<TopicRequest> = ArrayList()
    private lateinit var imgTopicView: ImageView
    private var isVideo = false
    private lateinit var topicSelect: TopicRequest


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTalkSignBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = TopicAdapter(requireContext())
        binding.btBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        isAdmin = SharedPreferencesUtils.getString(ROLE_USER).equals("ADMIN")
        if (isAdmin != null) {
            if (isAdmin) {
                binding.btAddTopic.visibility = View.VISIBLE
            } else {
                binding.btAddTopic.visibility = View.GONE
            }
        }


        initData()
        onAddData()
    }

    private fun onAddData() {
        binding.btAddTopic.setOnClickListener {
            addData()
        }
    }

    private fun addData() {
        DialogBottom.Builder(requireContext())
            .setTextMore("Thêm chủ đề")
            .setTextReport("Thêm từ vựng")
            .onMoreTip {
                addTopic(false, -1)
            }
            .onReport {
               addVocabulary()
            }
            .show()
    }

    private fun addVocabulary() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        val bindingDialog = AddTopicDialogBinding.inflate(layoutInflater)
        bindingDialog.tvTittle.hint = "Từ vựng"
        builder.setView(bindingDialog.root)
        bindingDialog.apply {
            imgTopicView = imgLocation
            lnlVocabulary.visibility = View.VISIBLE
            if (topicSelect.imageLocation != null && topicSelect.videoLocation != null) {
                if (topicSelect.imageLocation.isNotEmpty()) {
                    Glide.with(requireContext()).load(topicSelect.imageLocation).into(imgTopicView)
                }
                if (topicSelect.videoLocation.isNotEmpty()) {
                    Glide.with(requireContext()).load(topicSelect.videoLocation).into(imgTopicView)
                }
            }

            val vocabularyArrayAdapter = VocabularyArrayAdapter(requireContext(), spTopics)
            vocabularyArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spTopic.adapter = vocabularyArrayAdapter

            imgOpen.setOnClickListener {
                openImage()
            }
            imgRecord.setOnClickListener {
                openVideo()
            }
        }
        val selectedCategory =bindingDialog.spTopic.selectedItem as TopicRequest
        val topicId = selectedCategory.id
        builder.setPositiveButton("Đồng ý",
            DialogInterface.OnClickListener { dialog, which ->
                    if (bindingDialog.edTopic.text != null) {
                        if (isVideo) {
                            val topicPost =
                                TopicRequest(
                                    topicId ?: 1,
                                    bindingDialog.edTopic.text.toString(),
                                    "",
                                    urlResponse
                                )
                            adminViewModel.addVocabulary(topicPost).observe(viewLifecycleOwner) {
                                if (it.isSuccessful) {
                                    val bundle = bundleOf(
                                        "id" to topicId
                                    )
                                    findNavController().navigate(
                                        R.id.action_talkSignFragment_to_vocabulariesHomeFragment,
                                        bundle
                                    )

                                }
                            }
                        } else {
                            val topicPost =
                                TopicRequest(
                                    topicId ?: 1,
                                    bindingDialog.edTopic.text.toString(),
                                    urlResponse,
                                    ""
                                )
                            adminViewModel.addVocabulary(topicPost).observe(viewLifecycleOwner) {
                                if (it.isSuccessful) {
                                    val bundle = bundleOf(
                                        "id" to topicId
                                    )
                                    findNavController().navigate(
                                        R.id.action_talkSignFragment_to_vocabulariesHomeFragment,
                                        bundle
                                    )
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

    private fun addTopic(isUpdate: Boolean, groupId: Int) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())


        val bindingDialog = AddTopicDialogBinding.inflate(layoutInflater)
        bindingDialog.tvTittle.hint= "Chủ đề"
        builder.setView(bindingDialog.root)
        bindingDialog.apply {
            imgTopicView = imgLocation
            lnlVocabulary.visibility = View.GONE
            if (isUpdate) {
                edTopic.setText(topicSelect.content)
                if (topicSelect.imageLocation != null && topicSelect.videoLocation != null) {
                    if (topicSelect.imageLocation.isNotEmpty()) {
                        Glide.with(requireContext()).load(topicSelect.imageLocation).into(imgTopicView)
                    }
                    if (topicSelect.videoLocation.isNotEmpty()) {
                        Glide.with(requireContext()).load(topicSelect.videoLocation).into(imgTopicView)
                    }
                }

            } else {

            }
            imgOpen.setOnClickListener {
                openImage()

            }
            imgRecord.setOnClickListener {
                openVideo()
            }
        }
        builder.setPositiveButton("Đồng ý",
            DialogInterface.OnClickListener { dialog, which ->
                if (isUpdate) {
                    if (bindingDialog.edTopic.text != null) {
                        if (isVideo) {
                            val topicPost =
                                TopicRequest(
                                    topicSelect.id,
                                    bindingDialog.edTopic.text.toString(),
                                    "",
                                    urlResponse
                                )
                            adminViewModel.updateTopic(topicPost).observe(viewLifecycleOwner) {
                                if (it.isSuccessful) {
                                    requireContext().showToast("Cập nhật thành công")
                                    adapter.updateItem(groupId, topicPost)
                                } else {
                                    requireContext().showToast(it.message())
                                }
                            }
                        } else {
                            val topicPost =
                                TopicRequest(
                                    topicSelect.id,
                                    bindingDialog.edTopic.text.toString(),
                                    urlResponse,
                                    ""
                                )
                            adminViewModel.updateTopic(topicPost).observe(viewLifecycleOwner) {
                                if (it.isSuccessful) {
                                    requireContext().showToast("Cập nhật thành công")
                                    adapter.updateItem(groupId, topicPost)
                                } else {
                                    requireContext().showToast(it.message())
                                }
                            }
                        }

                    }
                } else {
                    if (bindingDialog.edTopic.text != null) {
                        if (isVideo) {
                            val topicPost =
                                TopicRequest(
                                    groupId,
                                    bindingDialog.edTopic.text.toString(),
                                    "",
                                    urlResponse
                                )
                            adminViewModel.addTopic(topicPost).observe(viewLifecycleOwner) {
                                if (it.isSuccessful) {
                                    adapter.addItem(topicPost)
                                }
                            }
                        } else {
                            val topicPost =
                                TopicRequest(
                                    groupId,
                                    bindingDialog.edTopic.text.toString(),
                                    urlResponse,
                                    ""
                                )
                            adminViewModel.addTopic(topicPost).observe(viewLifecycleOwner) {
                                if (it.isSuccessful) {
                                    adapter.addItem(topicPost)
                                }
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

    private fun openImage() {
        PermissionX.init(this@TopicStudyFragment)
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
                            PICK_IMAGE_REQUEST
                        )

                    } else {
                        requireContext().showToast("Bạn cần chấp nhận tất cả các quyền")
                    }
                }
            })
    }

    private fun openVideo() {
        PermissionX.init(this@TopicStudyFragment)
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
                            PICK_VIDEO_REQUEST
                        )

                    } else {
                        requireContext().showToast("Bạn cần chấp nhận tất cả các quyền")
                    }
                }
            })
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (isAdmin) {
            when (item.itemId) {
                0 -> {
                    showUpdateDialog(item.groupId)
                }

                1 -> {
                    showDeleteDialog(item.groupId)
                }
            }
            return true
        } else {
            return  false
        }


    }

    private fun showUpdateDialog(groupId: Int) {
        topicSelect = adapter.getItemSelect(groupId)
        addTopic(true, groupId)
    }

    private fun showDeleteDialog(groupId: Int) {
        topicSelect = adapter.getItemSelect(groupId)
        adminViewModel.deleteTopic(topicSelect.id).observe(viewLifecycleOwner) {
            if (it.isSuccessful) {
                adapter.removeItem(groupId)
            }
        }
    }

    private fun initData() {
        lifecycleScope.launchWhenStarted {
            viewModel.getAllTopic()
            viewModel.topic.collect {
                when (it) {
                    is Resource.Success -> {
                        topicRequests = it.data!!.data
                        spTopics.addAll(topicRequests!!)
                        try {
                            adapter.submitList(topicRequests!!)
                            binding.rcvView.layoutManager = LinearLayoutManager(requireContext())
                            binding.rcvView.adapter = adapter
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
        onClickItem()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            val imageUri = data.data
            imgUrl = RealPathUtil.getRealPath(requireContext(), imageUri)
            Glide.with(requireContext()).load(imageUri).into(imgTopicView)
            isVideo = false

        }
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == AppCompatActivity.RESULT_OK && data != null) {
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

    private fun onClickItem() {
        adapter.setItemClickListener(object : TopicAdapter.ItemClickListener {
            override fun onItemClick(position: Int) {

                val bundle = bundleOf(
                    "id" to topicRequests!![position].id,
                )
                findNavController().navigate(
                    R.id.action_talkSignFragment_to_vocabulariesHomeFragment,
                    bundle
                )

            }
        })
    }

    companion object {
        const val PICK_IMAGE_REQUEST = 1
        const val PICK_VIDEO_REQUEST = 2
    }
}