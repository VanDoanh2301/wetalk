package com.example.wetalk.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wetalk.R
import com.example.wetalk.data.model.objectmodel.Conversations
import com.example.wetalk.data.model.objectmodel.UserInforRequest
import com.example.wetalk.databinding.FragmentTalkTabChatBinding
import com.example.wetalk.ui.activity.MainActivity
import com.example.wetalk.ui.adapter.ConversationAdapter
import com.example.wetalk.ui.viewmodels.ChatTabViewModel
import com.example.wetalk.util.Resource
import com.example.wetalk.util.SharedPreferencesUtils
import dagger.hilt.android.AndroidEntryPoint

/**
 * A simple [Fragment] subclass.
 * Use the [TabChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class TabChatFragment() : Fragment() {
    private var _binding: FragmentTalkTabChatBinding? = null
    private val binding get() = _binding!!
    private val TAG = "TalkTabChatFragment"
    private var user: UserInforRequest? = null
    private lateinit var conversationAdapter: ConversationAdapter
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
            BaseDialogFragment.add(activity as MainActivity, SearchUserFragment.newInstance())
        }
        conversationAdapter.setOnClickItem(object : ConversationAdapter.OnClickItem{
            override fun onClickItem(
                position: Int,
                getAllListConversations:Conversations
            ) {
                SharedPreferencesUtils.saveRoom(getAllListConversations.conversationId)
                val bundle = bundleOf("conversationId" to getAllListConversations)
                findNavController().navigate(R.id.action_talkMainChatFragment_to_talkChatHomeFragment, bundle)
            }

        })
    }

    private fun initView() {
        lifecycleScope.launchWhenStarted {
             viewModel.getAllListConversations()
             viewModel.conversions.observe(viewLifecycleOwner) {
                when(it) {
                    is Resource.Success -> {
                        var listConversations = it.data?.data
                        if (listConversations != null) {
                            conversationAdapter.submitList(listConversations)
                            binding.rcvFriend.visibility = View.VISIBLE
                            binding.lnlNotFriend.visibility = View.GONE
                        } else {
                            binding.rcvFriend.visibility = View.GONE
                            binding.lnlNotFriend.visibility = View.VISIBLE
                        }
                    }
                    is Resource.Error -> {
                        binding.lnlNotFriend.visibility = View.VISIBLE
                    }
                }
            }
        }

    }

}
