package com.example.wetalk.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wetalk.data.model.objectmodel.GetAllListConversations
import com.example.wetalk.data.model.objectmodel.UserInforRequest
import com.example.wetalk.databinding.FragmentTalkTabChatBinding
import com.example.wetalk.ui.activity.MainActivity
import com.example.wetalk.ui.adapter.ConversationAdapter
import com.example.wetalk.ui.viewmodels.ChatTabViewModel
import com.example.wetalk.util.Resource
import dagger.hilt.android.AndroidEntryPoint

/**
 * A simple [Fragment] subclass.
 * Use the [TalkTabChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class TalkTabChatFragment() : Fragment() {
    private var _binding: FragmentTalkTabChatBinding? = null
    private val binding get() = _binding!!
    private val TAG = "TalkTabChatFragment"
    private var user: UserInforRequest? = null
    private lateinit var conversationAdapter: ConversationAdapter
    private var resultList : ArrayList<GetAllListConversations> = ArrayList()


    private val viewModel: ChatTabViewModel by viewModels()

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
        conversationAdapter = ConversationAdapter(requireContext())
        binding.apply {
            val linearLayoutManager = LinearLayoutManager(requireContext())
            rcvFriend.layoutManager = linearLayoutManager
            rcvFriend.adapter = conversationAdapter

        }

        initView()
        onView()
    }

    private fun onView() {
        binding.edtSearch.setOnClickListener {
            BaseDialogFragment.add(activity as MainActivity, TalkSearchUserFragment.newInstance())
        }
    }

    private fun initView() {
        lifecycleScope.launchWhenStarted {
             viewModel.getAllListConversations()
            viewModel.conversions.observe(viewLifecycleOwner) {
                when(it) {
                    is Resource.Success -> {
                          var listConversations = it.data
                        if (listConversations != null) {
                            conversationAdapter.submitList(listConversations)
                        }
                    }
                    is Resource.Error -> {

                    }
                }
            }
        }

    }

}
