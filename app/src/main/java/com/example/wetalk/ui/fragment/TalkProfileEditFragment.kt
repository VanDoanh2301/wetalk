package com.example.wetalk.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController

import com.example.wetalk.R
import com.example.wetalk.WeTalkApp
import com.example.wetalk.data.model.objectmodel.UserRequest
import com.example.wetalk.databinding.FragmentTalkProfileEditBinding
import com.example.wetalk.ui.activity.MainActivity
import com.example.wetalk.ui.viewmodels.TalkProfileHomeViewModel
import com.example.wetalk.util.DialogCenter
import com.example.wetalk.util.Resource
import com.example.wetalk.util.SharedPreferencesUtils
import com.example.wetalk.util.Task
import dagger.hilt.android.AndroidEntryPoint
import java.text.ParseException
import java.text.SimpleDateFormat
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
    private lateinit var user: UserRequest
    private val viewModel : TalkProfileHomeViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTalkProfileEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        initAge()
        binding.btSave.setOnClickListener {
            updateUser()
        }


    }

    private fun updateUser() {
        if (binding.txtName.text != null) {
            user.name = binding.txtName.text.toString()
        }
        if (binding.txtDate.text != null) {
            user.age = binding.txtDate.text.toString()
        }
        if (binding.txtPhone.text != null) {
            user.phoneNumber = binding.txtPhone.text.toString()
        }
        if (binding.txtGender.text != null) {
            user.gender = binding.txtGender.text.toString()
        }
        if (binding.txtAddress.text != null) {
            user.address = binding.txtAddress.text.toString()
        }
        if (binding.txtDate.text == null
            || binding.txtPhone.text == null
            ||binding.txtGender.text == null
            || binding.txtAddress.text == null
            ) {
            Toast.makeText(requireContext(), "Vui lòng điền thông tin đầy đủ", Toast.LENGTH_LONG).show()
        } else {
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.updateInforUser(user)
                viewModel.updateUser.collect {
                    when (it) {
                        is Resource.Loading -> {
                        }
                        is Resource.Success -> {
                          Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_LONG).show()

                        }
                        is Resource.Error -> {
                            Log.d("User", it.message.toString())
                        }
                    }
                }
            }

        }


    }
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
                            binding.txtName.setText(user.name)
                            binding.txtDate.setText(user.age.toString())
                            binding.txtPhone.setText(user.phoneNumber ?: "")
                            binding.txtGender.setText(user.gender ?: "")
                        } catch (e :Exception) {

                        }

                    }
                    is Resource.Error -> {
                        Log.d("User", it.message.toString())
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
                        SimpleDateFormat("EE, dd MMM yyyy", Locale.getDefault())
                    val dateString: String = format.format(result)
                    binding.txtDate.setText(dateString)
                }

            })
        }
    }
}