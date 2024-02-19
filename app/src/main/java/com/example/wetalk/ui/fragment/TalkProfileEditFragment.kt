package com.example.wetalk.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.wetalk.R

import com.example.wetalk.WeTalkApp
import com.example.wetalk.data.model.objectmodel.AvatarRequest
import com.example.wetalk.data.model.objectmodel.UserInforRequest
import com.example.wetalk.data.model.postmodel.UserUpdateDTO
import com.example.wetalk.databinding.FragmentTalkProfileEditBinding
import com.example.wetalk.ui.activity.MainActivity
import com.example.wetalk.ui.viewmodels.TalkProfileHomeViewModel
import com.example.wetalk.util.RealPathUtil
import com.example.wetalk.util.Resource
import com.example.wetalk.util.SharedPreferencesUtils
import com.example.wetalk.util.Task
import com.rey.material.widget.ImageView
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDateTime
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


    private var _binding: FragmentTalkProfileEditBinding? = null
    private val binding get() = _binding!!
    private lateinit var user: UserInforRequest
    private lateinit var userUpdateDTO: UserUpdateDTO
    private val viewModel: TalkProfileHomeViewModel by viewModels()
    private var imagePickLauncher: ActivityResultLauncher<Intent>? = null
    private var selectedImageUri: Uri? = null
    private var devicePath = ""
    private var urlImg = ""
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
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {

        } else {
            // Request the READ_EXTERNAL_STORAGE permission
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                123
            )
        }

        initData()
        initAge()
        initAvatar()
        initGender()
        binding.btSave.setOnClickListener {
            updateUser()
            updateAvatar()
        }

        binding.btBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

    }

    private fun updateAvatar() {
        if (selectedImageUri != null) {
            val avatarRequest = AvatarRequest(urlImg)
            val token = SharedPreferencesUtils.getString("isLogin")
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
                            Log.d("UserRegisterDTO", it.message.toString())
                        }
                    }
                }
            }

        }
    }

    private fun updateUser() {
        if (binding.txtName.text != null) {
            userUpdateDTO.name = binding.txtName.text.toString()
        }
        if (binding.txtDate.text != null) {
            userUpdateDTO.birthDay = binding.txtDate.text.toString()
        }
        if (binding.txtPhone.text != null) {
            userUpdateDTO.phoneNumber = binding.txtPhone.text.toString()
        }
        if (binding.txtGender.text != null) {
            if (binding.txtGender.text.toString().equals("Nam")) {
                userUpdateDTO.gender = "MALE"
            } else {
                userUpdateDTO.gender = "FEMALE"
            }
        }
        if (binding.txtAddress.text != null) {
            userUpdateDTO.address = binding.txtAddress.text.toString()
        }
        if (binding.txtDate.text == null
            || binding.txtPhone.text == null
            || binding.txtGender.text == null
            || binding.txtAddress.text == null
        ) {
            Toast.makeText(requireContext(), "Vui lòng điền thông tin đầy đủ", Toast.LENGTH_LONG)
                .show()
        } else {
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                val token = SharedPreferencesUtils.getString("isLogin")
                viewModel.updateInforUser("Bearer $token", userUpdateDTO)
                viewModel.updateUser.collect {
                    when (it) {
                        is Resource.Loading -> {
                        }

                        is Resource.Success -> {
                            Toast.makeText(
                                requireContext(),
                                "Cập nhật thành công",
                                Toast.LENGTH_LONG
                            ).show()

                        }

                        is Resource.Error -> {
                            Log.d("UserRegisterDTO", it.message.toString())
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
                            userUpdateDTO = UserUpdateDTO(
                                user.name,
                                user.phoneNumber ?: "",
                                user.address ?: "",
                                user.age ?: "",
                                user.gender ?: ""
                            )
                            binding.txtName.setText(user.name)
                            if (user.age != null && user.age != "") {
                                val dateTime = OffsetDateTime.parse(
                                    user.age,
                                    DateTimeFormatter.ISO_OFFSET_DATE_TIME
                                )
                                val formatter =
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
                                val dateString: String = formatter.format(dateTime.toLocalDate())
                                binding.txtDate.setText(dateString)
                            } else {
                                val formatter =
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
                                val dateString: String = LocalDateTime.now().format(formatter)
                                binding.txtDate.setText(dateString)
                            }

                            binding.txtPhone.setText(user.phoneNumber ?: "")
                            if (user.gender.equals("MALE")) {
                                binding.txtGender.setText("Nam")
                            } else {
                                binding.txtGender.setText("Nữ")
                            }

                            binding.txtGender.setText(user.gender ?: "")
                            binding.txtAddress.setText(user.address ?: "")
                            binding.txtPhone.hint =
                                if (user.phoneNumber == null) "Số điện thoại" else ""
                            binding.txtGender.hint = if (user.gender == null) "Giới tính" else ""
                            binding.txtAddress.hint = if (user.address == null) "Quê quán" else ""

                            Glide.with(requireContext()).load(user.avatarLocation)
                                .apply(RequestOptions.circleCropTransform())
                                .into(binding.imgAvata)
                        } catch (e: Exception) {

                        }

                    }

                    is Resource.Error -> {
                        Log.d("UserRegisterDTO", it.message.toString())
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
                    devicePath = RealPathUtil.getRealPath(requireContext(), data.data)
                    getUrlFile()

                }
            }
        }
        binding.profilePicLayout.setOnClickListener { v ->
            launchGalleryIntent()
        }
    }
    private fun initGender() {
        binding.txtGender.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), it)
            popupMenu.inflate(R.menu.menu_gender) // Thay thế your_menu_resource bằng ID của menu của bạn
            popupMenu.setOnMenuItemClickListener { item ->
                // Xử lý khi một item được chọn
                when (item.itemId) {
                    R.id.menu_item_1 -> {
                        binding.txtGender.setText("Nam")
                        true
                    }
                    R.id.menu_item_2 -> {
                        binding.txtGender.setText("Nữ")
                        true
                    }

                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    private fun launchGalleryIntent() {
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        imagePickLauncher?.launch(galleryIntent)
    }

    private fun getUrlFile() {
        try {
            // Kiểm tra xem đường dẫn tệp có tồn tại hay không
            if (devicePath.isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Vui lòng chọn ảnh của bạn",
                    Toast.LENGTH_LONG
                ).show()
                return
            }

            // Tạo Multipart Request
            val file = File(devicePath)
            val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
            val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)

            // Tải Lên Tệp
            viewModel.uploadVideo(filePart)

        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Đã xảy ra lỗi khi xử lý tệp",
                Toast.LENGTH_LONG
            ).show()
        }
        lifecycleScope.launchWhenResumed {
            viewModel.uploadResult.observe(
                viewLifecycleOwner
            ) {
                when (it) {
                    is Resource.Loading -> {

                    }

                    is Resource.Success -> {
                        urlImg = it.data.toString()
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setProfilePic(context: Context, imageUri: Uri, imageView: ImageView) {
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform())
            .into(imageView)
    }

//    private fun uploadImageToFirebaseStorage(imageUri: Uri) {
//        val storageReference = FirebaseStorage.getInstance().reference
//        val imageRef = storageReference.child("images/${UUID.randomUUID()}.jpg")
//
//        imageRef.putFile(imageUri)
//            .addOnSuccessListener {
//                imageRef.downloadUrl.addOnSuccessListener { uri ->
//                    // Hiển thị hình ảnh bằng Glide
//                    setProfilePic(requireContext(), uri, binding.imgAvata)
//                }.addOnFailureListener { e ->
//                    // Xử lý lỗi khi không lấy được link hình ảnh
//                    Toast.makeText(requireContext(), "Error getting download URL: ${e.message}", Toast.LENGTH_SHORT).show()
//                }
//            }
//            .addOnFailureListener { e ->
//                // Xử lý lỗi khi tải lên hình ảnh
//                Toast.makeText(requireContext(), "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//    }
}