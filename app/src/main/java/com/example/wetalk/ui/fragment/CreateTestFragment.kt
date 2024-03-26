package com.example.wetalk.ui.fragment

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.wetalk.R
import com.example.wetalk.data.model.objectmodel.AnswerRequest
import com.example.wetalk.data.model.objectmodel.Question
import com.example.wetalk.data.model.objectmodel.TopicRequest
import com.example.wetalk.data.model.postmodel.QuestionPost
import com.example.wetalk.databinding.FragmentCreateTestBinding
import com.example.wetalk.ui.adapter.VocabularyArrayAdapter
import com.example.wetalk.ui.viewmodels.AdminViewModel
import com.example.wetalk.ui.viewmodels.TopicViewModel
import com.example.wetalk.ui.viewmodels.VideoUpViewModel
import com.example.wetalk.util.RealPathUtil
import com.example.wetalk.util.Resource
import com.example.wetalk.util.showToast
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.callback.RequestCallback
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

@AndroidEntryPoint
class CreateTestFragment : Fragment() {
    private var _binding: FragmentCreateTestBinding? = null
    private val binding get() = _binding!!
    private var choose1 = false
    private var choose2 = false
    private var choose3 = false
    private var choose4 = false
    private var isSelected = false
    private var imgUrl = ""
    private var isVideo = false
    private var topicId = 1
    private val adminViewModel : AdminViewModel by viewModels()
    private val videoViewModel: VideoUpViewModel by viewModels()
    private val viewModel: TopicViewModel by viewModels()
    private var spTopics: ArrayList<TopicRequest> = ArrayList()
    private var urlResponse = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateTestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        initTopic()
        onEditView()

    }


    private fun onUpload() {
        binding.apply {
            val selectedCategory =binding.spTopic.selectedItem as TopicRequest
            topicId = selectedCategory.id
            val answers: ArrayList<AnswerRequest> = ArrayList()
            if (textOption1.text!!.isNotEmpty() && textOption1.text != null) {
                answers.add(AnswerRequest(
                    answerId = -1,
                    content = textOption1.text.toString(),
                    imageLocation = " " ,
                    videoLocation =  " ",
                    correct = choose1
                ))
            }
            if (textOption2.text!!.isNotEmpty() && textOption2.text != null) {
                answers.add(AnswerRequest(
                    answerId = -1,
                    content = textOption2.text.toString(),
                    imageLocation = " " ,
                    videoLocation =  " ",
                    correct = choose2
                ))
            }
            if (textOption3.text!!.isNotEmpty() && textOption3.text != null) {
                answers.add(AnswerRequest(
                    answerId = -1,
                    content = textOption3.text.toString(),
                    imageLocation = " " ,
                    videoLocation =  " ",
                    correct = choose3
                ))
            }
            if (textOption4.text!!.isNotEmpty() && textOption4.text != null) {
                answers.add(AnswerRequest(
                    answerId = -1,
                    content = textOption4.text.toString(),
                    imageLocation = " " ,
                    videoLocation =  " ",
                    correct = choose4
                ))
            }

            val question = QuestionPost(
                questionId = -1,
                content =textViewTitle.text.toString(),
                explanation = " ",
                imageLocation = if (isVideo) " " else urlResponse,
                videoLocation = if (isVideo) urlResponse else " ",
                topicId = topicId,
                answers = answers

            )
            adminViewModel.addQuestion(question).observe(viewLifecycleOwner) {
                if (it.isSuccessful) {
                    requireContext().showToast("Thêm kiểm tra thành công")
                    val bundle = bundleOf(
                        "id" to topicId
                    )
                    findNavController().navigate(R.id.action_createTestFragment_to_talkTestFragment, bundle)
                }
            }
        }

    }
    private fun onEditView() {
     binding.apply {
         imgOpen.setOnClickListener {
             openImage()
         }
         imgRecord.setOnClickListener {
             openVideo()
         }
         btnSave.setOnClickListener {
             if (isSelected) {
                 onUpload()
             } else {
                 requireContext().showToast("Vui lòng chọn đáp án đúng")
             }

         }
         btnBack.setOnClickListener {
             requireActivity().onBackPressed()
         }

     }
    }

    private fun openImage() {
        PermissionX.init(this@CreateTestFragment)
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
        PermissionX.init(this@CreateTestFragment)
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
    private fun init() {
        binding.apply {
            tnIndex1.setOnClickListener {
                updateBackground()
                chooseAtIndex(1)
                tnIndex1.setBackgroundResource(R.drawable.circle_number_select)
                tnIndex1.setTextColor(Color.parseColor("#FFFFFF"))
                isSelected = true

            }
            tnIndex2.setOnClickListener {
                updateBackground()
                chooseAtIndex(2)
                tnIndex2.setBackgroundResource(R.drawable.circle_number_select)
                tnIndex2.setTextColor(Color.parseColor("#FFFFFF"))
                isSelected = true
            }
            tnIndex3.setOnClickListener {
                updateBackground()
                chooseAtIndex(3)
                tnIndex3.setBackgroundResource(R.drawable.circle_number_select)
                tnIndex3.setTextColor(Color.parseColor("#FFFFFF"))
                isSelected = true
            }
            tnIndex4.setOnClickListener {
                updateBackground()
                chooseAtIndex(4)
                tnIndex4.setBackgroundResource(R.drawable.circle_number_select)
                tnIndex4.setTextColor(Color.parseColor("#FFFFFF"))
                isSelected = true
            }



        }
    }

    private fun updateBackground() {
        binding.apply {
            tnIndex4.setBackgroundResource(R.drawable.circle_number)
            tnIndex1.setBackgroundResource(R.drawable.circle_number)
            tnIndex2.setBackgroundResource(R.drawable.circle_number)
            tnIndex3.setBackgroundResource(R.drawable.circle_number)

            tnIndex1.setTextColor(Color.parseColor("#000000"))
            tnIndex2.setTextColor(Color.parseColor("#000000"))
            tnIndex3.setTextColor(Color.parseColor("#000000"))
            tnIndex4.setTextColor(Color.parseColor("#000000"))



        }
    }

    private fun chooseAtIndex(index: Int) {
        choose1 = false
        choose2 = false
        choose3 = false
        choose4 = false

        when (index) {
            1 -> choose1 = true
            2 -> choose2 = true
            3 -> choose3 = true
            4 -> choose4 = true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TopicStudyFragment.PICK_IMAGE_REQUEST && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            val imageUri = data.data
            imgUrl = RealPathUtil.getRealPath(requireContext(), imageUri)
            Glide.with(requireContext()).load(imageUri).into(binding.imgView)
            isVideo = false

        }
        if (requestCode == TopicStudyFragment.PICK_VIDEO_REQUEST && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            val imageUri = data.data
            imgUrl = RealPathUtil.getRealPath(requireContext(), imageUri)
            Glide.with(requireContext()).load(imageUri).into(binding.imgView)
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
    private fun initTopic() {
        lifecycleScope.launchWhenStarted {
            viewModel.getAllTopic()
            viewModel.topic.collect {
                when (it) {
                    is Resource.Success -> {
                        val  topicRequests = it.data!!.data
                        if (topicRequests != null) {
                            spTopics.addAll(topicRequests!!)
                            binding.apply {
                                val vocabularyArrayAdapter = VocabularyArrayAdapter(requireContext(), spTopics)
                                vocabularyArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                spTopic.adapter = vocabularyArrayAdapter

                            }
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
    companion object {
        @JvmStatic
        fun newInstance() =
            CreateTestFragment().apply {
                arguments = Bundle().apply {

                }
            }
        const val PICK_IMAGE_VIDEO_REQUEST = 1
    }
}