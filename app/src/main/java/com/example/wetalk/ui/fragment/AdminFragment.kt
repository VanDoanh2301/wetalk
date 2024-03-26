package com.example.wetalk.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.wetalk.R
import com.example.wetalk.data.model.objectmodel.UserInforRequest
import com.example.wetalk.databinding.FragmentTalkAdminBinding
import com.example.wetalk.ui.viewmodels.ProfileHomeViewModel
import com.example.wetalk.util.EMAIL_USER
import com.example.wetalk.util.Resource
import com.example.wetalk.util.SharedPreferencesUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminFragment : Fragment() {
    private val viewModel: ProfileHomeViewModel by viewModels()

    private var _binding: FragmentTalkAdminBinding? = null
    private val binding get() =  _binding!!
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
        _binding = FragmentTalkAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        onClickView()
    }

    private fun init() {
        lifecycleScope.launchWhenStarted {
            viewModel.getUser()
            viewModel.getInforUser.collect {
                when (it) {
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        val user = it.data!!
                         updateView(user)
                    }


                    is Resource.Error -> {

                    }
                }
            }
        }
    }

    private fun updateView(user: UserInforRequest) {
        binding.tvName.text = user?.name
        binding.textView2.text = user?.email
        if (user.role.equals("ADMIN")) {
            isAdmin = true
        } else {
            isAdmin = false
        }
        Glide.with(requireContext())
            .load(user?.avatarLocation)
            .apply(RequestOptions.circleCropTransform())
            .into(binding.imgAvata)

        user?.id?.let { it1 -> SharedPreferencesUtils.setCurrentUser(it1) }
        SharedPreferencesUtils.setString(EMAIL_USER, user!!.email)
    }

    private fun onClickView() {
    binding.apply {
        //Go to Topic
        btTopic.setOnClickListener {
            val bundle = bundleOf(
              "isAdmin" to isAdmin
            )
            findNavController().navigate(R.id.action_talkAdminFragment_to_talkSignFragment, bundle)
        }
        btHome.setOnClickListener {
            findNavController().navigate(R.id.action_talkAdminFragment_to_talkHomeFragment)
        }
        btTest.setOnClickListener {
            findNavController().navigate(R.id.action_talkAdminFragment_to_createTestFragment)
        }

    }

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AdminFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}