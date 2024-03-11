package com.example.wetalk.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wetalk.data.model.objectmodel.ChatMessage
import com.example.wetalk.databinding.FragmentTalkChatHomeBinding
import com.example.wetalk.ui.adapter.ChatMessageAdapter
import com.example.wetalk.ui.viewmodels.ChatHomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TalkOpenChatFragment : Fragment() {
    private var _binding: FragmentTalkChatHomeBinding ?= null
    private val binding get() = _binding!!
    private lateinit var chatMessageAdapter: ChatMessageAdapter
    private var resultList: ArrayList<ChatMessage> = ArrayList()
    private val viewModel:ChatHomeViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTalkChatHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatMessageAdapter = ChatMessageAdapter(requireContext())
        init()
        initChat()
    }
    private fun init() {
        binding.apply {
            val linearLayoutManager = LinearLayoutManager(requireContext())
            chatRecyclerView.layoutManager = linearLayoutManager
            chatRecyclerView.adapter = chatMessageAdapter
            chatMessageAdapter.submitList(resultList)
            chatMessageAdapter.registerAdapterDataObserver(object :
                RecyclerView.AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    super.onItemRangeInserted(positionStart, itemCount)
                    chatRecyclerView.smoothScrollToPosition(0)
                }
            })
        }
    }
    private fun initChat() {
       binding.apply {

       }

    }

}