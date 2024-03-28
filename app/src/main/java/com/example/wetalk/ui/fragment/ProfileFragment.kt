package com.example.wetalk.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.wetalk.databinding.FragmentTalkTabProfileBinding
import com.example.wetalk.ui.viewmodels.ProfileHomeViewModel
import com.example.wetalk.util.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment() : Fragment() {
    private var _binding : FragmentTalkTabProfileBinding? =null
    private val binding get() = _binding!!
    private val viewModel: ProfileHomeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTalkTabProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       init()
    }
    private fun init() {
        lifecycleScope.launchWhenStarted {
            viewModel.getUser()
            viewModel.getInforUser.collect {
                when (it) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        val newUser = it.data!!
                        Glide.with(requireContext()).load(newUser.avatarLocation).into(binding.imgAvata)
                        binding.tvName.text = newUser.name
                    }


                    is Resource.Error -> {

                    }
                }
            }
        }
    }
    companion object {
        @JvmStatic
        fun newInstance() =
            ProfileFragment().apply {
                arguments = bundleOf()
            }

    }
}