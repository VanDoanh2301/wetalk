package com.example.wetalk.ui.fragment


import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.wetalk.R
import com.example.wetalk.data.model.objectmodel.UserInforRequest
import com.example.wetalk.data.model.postmodel.PasswordPost
import com.example.wetalk.databinding.FragmentTalkProfileHomeBinding
import com.example.wetalk.ui.viewmodels.ProfileHomeViewModel
import com.example.wetalk.util.Resource
import com.example.wetalk.util.SharedPreferencesUtils
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


/**
 * A simple [Fragment] subclass.
 * Use the [ProfileHomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class ProfileHomeFragment : Fragment() {
    private var _binding: FragmentTalkProfileHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileHomeViewModel by viewModels()
    private lateinit var user: UserInforRequest
    private lateinit var changePasswordRequest: PasswordPost

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTalkProfileHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        onClickView()

    }
    private fun onClickView() {
        binding.btBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.tvNext.setOnClickListener {
        }
        binding.openMenu.setOnClickListener { view ->
            showPopupMenu(view)
        }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.inflate(R.menu.menu_profile) // Thay thế your_menu_resource bằng ID của menu của bạn
        popupMenu.setOnMenuItemClickListener { item ->
            // Xử lý khi một item được chọn
            when (item.itemId) {
                R.id.menu_item_1 -> {
                    findNavController().navigate(R.id.action_talkProfileHomeFragment_to_talkProfileEditFragment)
                    true
                }
                R.id.menu_item_2 -> {
                    showChangePasswordDialog();
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun showChangePasswordDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Thay đổi mật khẩu")
        // Inflate layout cho dialog
        val view: View = layoutInflater.inflate(R.layout.change_password_dialog, null)
        builder.setView(view)
        val edtPassOld = view.findViewById<TextInputEditText>(R.id.ed_pass_old)
        val edtPassNew = view.findViewById<TextInputEditText>(R.id.ed_pass_new)
        val edtPassConfim = view.findViewById<TextInputEditText>(R.id.ed_pass_confim)
        val oldPassword = edtPassOld.text.toString()
        val newPassword = edtPassNew.text.toString()
        val confirmPassword = edtPassConfim.text.toString()
        if (confirmPassword == newPassword) {
            changePasswordRequest = PasswordPost(oldPassword, newPassword)
        } else {
            edtPassConfim.error = "Mật khẩu xác nhận không khớp"
        }
        builder.setPositiveButton("Đồng ý",
            DialogInterface.OnClickListener { dialog, which ->
                viewModel.changePassword(changePasswordRequest)
            })
        builder.setNegativeButton("Hủy bỏ",
            DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            })
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun initUI() {
        lifecycleScope.launchWhenStarted {
            val isAccess = SharedPreferencesUtils.getString("isLogin")
            viewModel.getUser()
            viewModel.getInforUser.collect {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        try {
                            user = it.data!!
                            updateUI(user)

                        } catch (e: Exception) {
                            Log.d("EXCEPTION", e.message.toString())
                        }
                    }
                    is Resource.Error -> { Log.d("UserRegisterDTO", it.message.toString()) }
                }
            }
        }
    }

    private fun updateUI(user: UserInforRequest) {
        binding.txtName.text = user.name
        if (user.age == null || user.phoneNumber == null || user.gender == null) {
            binding.txtDate.text = "Cập nhật ngày sinh"
            binding.txtGenner.text = "Cập nhật giới tính"
            binding.txtPhone.text = "Cập nhật số điện thoại"

            binding.txtPhone.setOnClickListener {
                findNavController().navigate(R.id.action_talkProfileHomeFragment_to_talkProfileEditFragment)
            }
            binding.txtGenner.setOnClickListener {
                binding.txtPhone.performClick()
            }
            binding.txtDate.setOnClickListener {
                binding.txtPhone.performClick()
            }

        }
        val dateTime = OffsetDateTime.parse(
            user.age,
            DateTimeFormatter.ISO_OFFSET_DATE_TIME
        )
        val formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())

        val dateString: String = formatter.format(dateTime.toLocalDate())
        binding.txtDate.text = dateString
        binding.txtDate.text = dateString
        binding.txtPhone.text = user.phoneNumber
        if (user.gender.equals("MALE")) {
            binding.txtGenner.text = "Nam"
        } else {
            binding.txtGenner.text = "Nữ"
        }

        Glide.with(requireContext()).load(user.avatarLocation)
            .apply(RequestOptions.circleCropTransform())
            .into(binding.imgAvata)
    }

}