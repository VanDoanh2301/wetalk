package com.example.wetalk.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.example.wetalk.R
import com.example.wetalk.databinding.FragmentTalkTabChatBinding
import com.example.wetalk.ui.activity.MainActivity

/**
 * A simple [Fragment] subclass.
 * Use the [TalkTabChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TalkTabChatFragment : Fragment() {

    private var _binding:FragmentTalkTabChatBinding ? =null
    private val binding get() =  _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTalkTabChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        intiSearch()


    }
    private fun intiSearch() {
        //OnClick Search View
        binding.edtSearch.setOnClickListener {
           BaseFragment.add(activity as MainActivity, TalkSearchUserFragment.newInstance())
        }
    }
    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TalkTabChatFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}