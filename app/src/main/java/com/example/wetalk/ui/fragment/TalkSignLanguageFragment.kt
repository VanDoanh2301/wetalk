package com.example.wetalk.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.wetalk.R
import com.example.wetalk.databinding.FragmentTalkSignLanguageBinding
import com.example.wetalk.ui.adapter.StudyAdapter

/**
 * A simple [Fragment] subclass.
 * Use the [TalkSignLanguageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TalkSignLanguageFragment : Fragment() {
    private var _binding: FragmentTalkSignLanguageBinding? =null
    private val binding get() = _binding!!
    private lateinit var adapter: StudyAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTalkSignLanguageBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = StudyAdapter(requireContext(), dataList = generateAlphabet())
        adapter.notifyDataSetChanged()
        binding.rcvView.layoutManager = GridLayoutManager(requireContext(), 4)
        binding.rcvView.adapter = adapter
    }

    fun generateAlphabet(): ArrayList<String> {
        val alphabet = ArrayList<String>()
        for (char in 'A'..'Z') {
            alphabet.add(char.toString())
        }
        return alphabet
    }
}