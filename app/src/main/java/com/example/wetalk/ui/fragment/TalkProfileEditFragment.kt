package com.example.wetalk.ui.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import com.example.wetalk.WeTalkApp
import com.example.wetalk.data.model.objectmodel.AvatarRequest
import com.example.wetalk.data.model.objectmodel.UserInforRequest
import com.example.wetalk.data.model.objectmodel.UserUpdate
import com.example.wetalk.databinding.FragmentTalkProfileEditBinding
import com.example.wetalk.ui.activity.MainActivity
import com.example.wetalk.ui.viewmodels.TalkProfileHomeViewModel
import com.example.wetalk.util.Resource
import com.example.wetalk.util.SharedPreferencesUtils
import com.example.wetalk.util.Task
import com.rey.material.widget.ImageView
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * A simple [Fragment] subclass.
 * Use the [TalkProfileEditFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class TalkProfileEditFragment : Fragment() {


    private var _binding: FragmentTalkProfileEditBinding ?= null
    private val binding get() = _binding!!
    private lateinit var user: UserInforRequest
    private lateinit var userUpdate:UserUpdate
    private val viewModel : TalkProfileHomeViewModel by viewModels()
    private var imagePickLauncher: ActivityResultLauncher<Intent>? = null
    private var selectedImageUri: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTalkProfileEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        initAge()
        initAvatar()
        binding.btSave.setOnClickListener {
            updateUser()
            updateAvatar()
        }


    }

    private fun updateAvatar() {
        if (selectedImageUri != null) {
            val avatarRequest = AvatarRequest(selectedImageUri.toString())
            val token  = SharedPreferencesUtils.getString("isLogin")
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.updateAvatarUser("Bearer $token", avatarRequest)
                viewModel.updateAvatar.collect {
                    when (it) {
                        is Resource.Loading -> {
                        }
                        is Resource.Success -> {
                            Log.d("UPAVATAR", "UP SUCCESS")
                        }
                        is Resource.Error -> {
                            Log.d("UserRegisterRequest", it.message.toString())
                        }
                    }
                }
            }

        }
    }
    private fun updateUser() {
        if (binding.txtName.text != null) {
            userUpdate.name = binding.txtName.text.toString()
        }
        if (binding.txtDate.text != null) {
            userUpdate.birthDay = binding.txtDate.text.toString()
        }
        if (binding.txtPhone.text != null) {
            userUpdate.phoneNumber = binding.txtPhone.text.toString()
        }
        if (binding.txtGender.text != null) {
            userUpdate.gender = binding.txtGender.text.toString()
        }
        if (binding.txtAddress.text != null) {
            userUpdate.address = binding.txtAddress.text.toString()
        }
        if (binding.txtDate.text == null
            || binding.txtPhone.text == null
            ||binding.txtGender.text == null
            || binding.txtAddress.text == null
            ) {
            Toast.makeText(requireContext(), "Vui lòng điền thông tin đầy đủ", Toast.LENGTH_LONG).show()
        } else {
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                val token  = SharedPreferencesUtils.getString("isLogin")
                viewModel.updateInforUser("Bearer $token",userUpdate)
                viewModel.updateUser.collect {
                    when (it) {
                        is Resource.Loading -> {
                        }
                        is Resource.Success -> {
                          Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_LONG).show()

                        }
                        is Resource.Error -> {
                            Log.d("UserRegisterRequest", it.message.toString())
                        }
                    }
                }
            }

        }


    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initData() {
        lifecycleScope.launchWhenStarted {
            val isAccess = SharedPreferencesUtils.getString("isLogin")
            viewModel.getUser("Bearer $isAccess")
            viewModel.getInforUser.collect {
                when (it) {
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        try {
                            user = it.data!!
                            userUpdate = UserUpdate(user.name, user.phoneNumber, user.address, user.age, user.gender)
                            binding.txtName.setText(user.name)
                            val dateTime = OffsetDateTime.parse(user.age, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
                            val dateString: String = formatter.format(dateTime.toLocalDate())
                            binding.txtDate.setText(dateString)
                            binding.txtPhone.setText(user.phoneNumber ?: "")
                            binding.txtGender.setText(user.gender ?: "")
                            binding.txtAddress.setText(user.address)
                        } catch (e :Exception) {

                        }

                    }
                    is Resource.Error -> {
                        Log.d("UserRegisterRequest", it.message.toString())
                    }
                }
            }
        }
    }
    private fun initAge() {
        binding.txtDate.setOnClickListener {

            WeTalkApp.showDatePicker(activity as MainActivity, object : Task<Long> {
                override fun callback(result: Long) {
                    val format =
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val dateString: String = format.format(result)
                    binding.txtDate.setText(dateString)
                }

            })
        }
    }
    private fun initAvatar() {
        imagePickLauncher = registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null && data.data != null) {
                    selectedImageUri = data.data
                    setProfilePic(requireContext(), selectedImageUri!!, binding.imgAvata)
                }
            }
        }
        binding.profilePicLayout.setOnClickListener { v ->
            val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
            galleryIntent.type = "image/*"
            imagePickLauncher?.launch(galleryIntent)
        }
    }
    private fun setProfilePic(context: Context, imageUri: Uri, imageView: ImageView) {
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform())
            .into(imageView)
    }
}