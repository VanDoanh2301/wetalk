package com.example.wetalk.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.wetalk.R
import com.example.wetalk.data.model.objectmodel.UserInforRequest
import com.example.wetalk.databinding.FragmentTalkTabChatBinding
import com.example.wetalk.databinding.FragmentTalkTabProfileBinding
/**
 * A simple [Fragment] subclass.
 * Use the [TalkTabProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TalkTabProfileFragment() : Fragment() {
    private var _binding : FragmentTalkTabProfileBinding? =null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTalkTabProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.tvName.text = user.name
//        Glide.with(requireContext()).load(user.avatarLocation)
//            .apply(RequestOptions.circleCropTransform())
//            .into(binding.imgAvata)
    }
    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            TalkTabProfileFragment().apply {
                arguments = bundleOf()
            }

    }
}