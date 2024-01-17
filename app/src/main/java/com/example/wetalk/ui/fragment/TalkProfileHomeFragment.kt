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
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.wetalk.R
import com.example.wetalk.data.model.objectmodel.UserInforRequest
import com.example.wetalk.databinding.FragmentTalkProfileHomeBinding
import com.example.wetalk.ui.viewmodels.TalkProfileHomeViewModel
import com.example.wetalk.util.DialogCenter
import com.example.wetalk.util.Resource
import com.example.wetalk.util.SharedPreferencesUtils
import com.rey.material.widget.ImageView
import dagger.hilt.android.AndroidEntryPoint
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


/**
 * A simple [Fragment] subclass.
 * Use the [TalkProfileHomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class TalkProfileHomeFragment : Fragment() {
    private var _binding: FragmentTalkProfileHomeBinding ? = null
    private val binding get() =  _binding!!
    private val viewModel : TalkProfileHomeViewModel by viewModels()
    private lateinit var user:UserInforRequest

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTalkProfileHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.tvNext.setOnClickListener {
            findNavController().navigate(R.id.action_talkProfileHomeFragment_to_talkProfileEditFragment)
        }
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
                            binding.txtName.text = user.name
                            if (user.age ==null ||  user.phoneNumber ==null || user.gender== null) {
                                DialogCenter.Builder(requireContext())
                                    .title("Gợi ý")
                                    .cancelable(true)
                                    .canceledOnTouchOutside(true)
                                    .content("Cập nhật thông tin cá nhân của bạn")
                                    .doneText("Cập nhật")
                                    .onPositive {

                                    }
                                    .onDone {
                                        findNavController().navigate(R.id.action_talkProfileHomeFragment_to_talkProfileEditFragment)
                                    }
                                    .show()
                            }
                            val dateTime = OffsetDateTime.parse(user.age, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())

                            val dateString: String = formatter.format(dateTime.toLocalDate())
                            binding.txtDate.text = dateString
                            binding.txtDate.text = dateString
                            binding.txtPhone.text = user.phoneNumber
                            binding.txtGenner.text = user.gender
                            Glide.with(requireContext()).load(user.avatarLocation).apply(RequestOptions.circleCropTransform())
                                .into(binding.imgAvata)
                        } catch (e :Exception) {
                             Log.d("EXCEPTION", e.message.toString())
                        }

                    }
                    is Resource.Error -> {
                      Log.d("UserRegisterRequest", it.message.toString())
                    }
                }
            }
        }

        binding.btBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

}