package com.example.wetalk.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

import com.example.wetalk.R
import com.example.wetalk.databinding.FragmentTalkProfileBinding

/**
 * A simple [Fragment] subclass.
 * Use the [TalkProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TalkProfileFragment : Fragment() {

    private var _binding:FragmentTalkProfileBinding ? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTalkProfileBinding.inflate(inflater, container,  false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rltUser.setOnClickListener {
            findNavController().navigate(R.id.action_talkHomeFragment_to_talkProfileHomeFragment)
        }

    }
}