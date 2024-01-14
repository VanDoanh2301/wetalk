package com.example.wetalk.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.wetalk.R
import com.example.wetalk.databinding.FragmentTalkChatBinding
import com.example.wetalk.databinding.FragmentTalkHomeBinding

/**
 * A simple [Fragment] subclass.
 * Use the [TalkChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TalkChatFragment : Fragment() {
    private var _binding: FragmentTalkChatBinding ? =null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTalkChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarMain.btnMenu.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }
}