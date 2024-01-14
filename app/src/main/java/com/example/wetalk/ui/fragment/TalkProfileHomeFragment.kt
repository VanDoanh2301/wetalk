package com.example.wetalk.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.wetalk.R
import com.example.wetalk.data.model.objectmodel.User
import com.example.wetalk.data.model.objectmodel.UserRequest
import com.example.wetalk.databinding.FragmentTalkProfileHomeBinding
import com.example.wetalk.ui.viewmodels.TalkProfileHomeViewModel
import com.example.wetalk.util.DialogCenter
import com.example.wetalk.util.Resource
import com.example.wetalk.util.SharedPreferencesUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

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
    private lateinit var user:UserRequest
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
                            binding.txtDate.text = user.age
                            binding.txtPhone.text = user.phoneNumber
                            binding.txtGenner.text = user.gender
                        } catch (e :Exception) {

                        }

                    }
                    is Resource.Error -> {
                      Log.d("User", it.message.toString())
                    }
                }
            }
        }

        binding.btBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }
}