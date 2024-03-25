package com.example.wetalk.ui.fragment

import TopicAdapter
import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.wetalk.R
import com.example.wetalk.data.model.objectmodel.TopicRequest
import com.example.wetalk.data.model.postmodel.PasswordPost
import com.example.wetalk.databinding.AddTopicDialogBinding
import com.example.wetalk.databinding.FragmentTalkTopicBinding
import com.example.wetalk.ui.viewmodels.AdminViewModel
import com.example.wetalk.ui.viewmodels.TopicViewModel
import com.example.wetalk.util.RealPathUtil
import com.example.wetalk.util.Resource
import com.example.wetalk.util.dialog.DialogBottom
import com.example.wetalk.util.showToast
import com.google.android.material.textfield.TextInputEditText
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.callback.RequestCallback
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 * Use the [TopicFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class TopicFragment : Fragment() {
    private var _binding:FragmentTalkTopicBinding ? = null
    private val binding get() = _binding!!
    private val viewModel : TopicViewModel by viewModels()
    private val adminViewModel : AdminViewModel by viewModels()
    private lateinit var adapter: TopicAdapter
    private var topicRequests : ArrayList<TopicRequest> ?= null
    private var isAdmin = false
    private var imgUrl :String ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTalkTopicBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isAdmin = arguments?.getBoolean("isAdmin")!!

        if (isAdmin != null) {
            if (isAdmin) {
                binding.btAddTopic.visibility = View.VISIBLE
            } else {
                binding.btAddTopic.visibility = View.GONE
            }
        }

        init()
        onClickItem()
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
                    addTopic()
                }
                .onReport {

                }
                .show()
    }
    private fun addTopic() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Thêm chủ đề")
        // Inflate layout cho dialog
        val bindingDialog = AddTopicDialogBinding.inflate(layoutInflater)
        builder.setView(bindingDialog.root)
        bindingDialog.apply {

            imgOpen.setOnClickListener {
                PermissionX.init(this@TopicFragment)
                    .permissions(
                        Manifest.permission.READ_MEDIA_VIDEO,
                        Manifest.permission.READ_MEDIA_AUDIO,
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
                                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                                startActivityForResult(intent, PICK_IMAGE_REQUEST)
                                Glide.with(requireContext()).load(imgUrl).into(imgLocation)
                            } else {
                                requireContext().showToast("Bạn cần chấp nhận tất cả các quyền")
                            }
                        }
                    })
            }

            if (edTopic.text != null) {

            }
        }
        builder.setPositiveButton("Đồng ý",
            DialogInterface.OnClickListener { dialog, which ->

            })
        builder.setNegativeButton("Hủy bỏ",
            DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            })
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun init() {
        adapter = TopicAdapter(requireContext())
        binding.btBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        lifecycleScope.launchWhenStarted {
            viewModel.getAllTopic()
            viewModel.topic.collect {
                when(it) {
                    is Resource.Success -> {
                        topicRequests = it.data!!.data
                        Log.d("Topic", topicRequests.toString())
                        try {
                            adapter.notifyData(topicRequests!!)
                            binding.rcvView.layoutManager = LinearLayoutManager(requireContext())
                            binding.rcvView.adapter = adapter
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), "Topic is Null", Toast.LENGTH_SHORT).show()
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
    private fun onClickItem() {
        adapter.setItemClickListener(object : TopicAdapter.ItemClickListener {
            override fun onItemClick(position: Int) {

                val bundle = bundleOf(
                    "id" to topicRequests!![position].id,
                )
                Log.d("id", topicRequests!![position].id.toString())
                findNavController().navigate(R.id.action_talkTopicFragment_to_talkTestFragment, bundle)
            }

        })
    }
    // Xử lý kết quả trả về từ Intent
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            // Lấy URI của ảnh đã chọn
            val imageUri = data.data
            imgUrl = RealPathUtil.getRealPath(requireContext(), imageUri)



        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            TopicFragment().apply {
                arguments = Bundle().apply {
                }
            }
        const val PICK_IMAGE_REQUEST = 1
    }
}