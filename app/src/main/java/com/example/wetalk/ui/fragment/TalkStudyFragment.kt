package com.example.wetalk.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.wetalk.R
import com.example.wetalk.databinding.FragmentTalkStudyBinding

/**
 * A simple [Fragment] subclass.
 * Use the [TalkStudyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TalkStudyFragment : Fragment() {
    private var _binding: FragmentTalkStudyBinding ? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTalkStudyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rltHandbook.setOnClickListener {
            findNavController().navigate(R.id.action_talkHomeFragment_to_talkVocabularyUpFragment)
        }
        binding.btnLanguage.setOnClickListener {
            findNavController().navigate(R.id.action_talkHomeFragment_to_talkSignLanguageFragment)
        }
        binding.btnNumber.setOnClickListener {
            findNavController().navigate(R.id.action_talkHomeFragment_to_talkSignNumberFragment)
        }
        binding.btnCharacted.setOnClickListener {
            findNavController().navigate(R.id.action_talkHomeFragment_to_talkSignFragment)
        }
        binding.btnTest.setOnClickListener {
            findNavController().navigate(R.id.action_talkHomeFragment_to_talkTestFragment)
        }
    }
}