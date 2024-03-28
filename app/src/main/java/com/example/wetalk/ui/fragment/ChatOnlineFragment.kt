package com.example.wetalk.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.wetalk.R
import com.example.wetalk.data.model.objectmodel.ChatMessage
import com.example.wetalk.data.model.objectmodel.Conversations
import com.example.wetalk.databinding.FragmentTalkChatHomeBinding
import com.example.wetalk.ui.adapter.ChatMessageAdapter
import com.example.wetalk.ui.viewmodels.ChatHomeViewModel
import com.example.wetalk.ui.viewmodels.ChatStatus
import com.example.wetalk.util.AVATAR_SENDER
import com.example.wetalk.util.EMAIL_USER
import com.example.wetalk.util.Resource
import com.example.wetalk.util.SEND_STATUS
import com.example.wetalk.util.SharedPreferencesUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatOnlineFragment : Fragment() {
    private var _binding: FragmentTalkChatHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var chatMessageAdapter: ChatMessageAdapter
    private var resultList: ArrayList<ChatMessage> = ArrayList()
    private lateinit var conversations: Conversations
    private var TAG = "TalkOpenChatFragment"
    private val viewModel: ChatHomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        conversations = arguments?.getParcelable("conversationId")!!
    }

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
        chatMessageAdapter =  ChatMessageAdapter(requireContext())

        init()
        initSender()
        initStatus()
        initMessage()
        viewModel.connectSocket()
        viewModel.chatMessages.observe(viewLifecycleOwner) {
            chatMessageAdapter.addItem(it)
            binding.chatRecyclerView.scrollToPosition(0)
        }
        binding.messageSendBtn.setOnClickListener {
            var chatMessage = ChatMessage(
                SharedPreferencesUtils.getCurrentUser()!!,
                binding.chatMessageInput.text.toString(),
                "TEXT",
                null
            )
            chatMessageAdapter.addItem(chatMessage)
            binding.chatRecyclerView.scrollToPosition(0)

            viewModel.sendMessageClient(chatMessage)
        }
        binding.backBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun initMessage() {
        lifecycleScope.launchWhenStarted {
            viewModel.getAllMessage(conversations.conversationId)
            viewModel.messages.observe(viewLifecycleOwner) {
                when (it) {
                    is Resource.Success -> {
                        var messages = it.data?.data
                        messages!!.forEach { message ->
                            val chatMessage = ChatMessage(
                                message.contactId ?: -1,
                                message.content ?: "",
                                message.messageType ?: "TEXT",
                                message.mediaLocation ?: null
                            )
                            resultList.add(chatMessage)
                        }
                        binding.apply {
                            val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
                            linearLayoutManager.stackFromEnd = false
                            chatMessageAdapter.submitList(resultList)
                            chatRecyclerView.layoutManager = linearLayoutManager
                            chatRecyclerView.adapter = chatMessageAdapter
                            chatRecyclerView.setHasFixedSize(true)
                        }


                    }

                    is Resource.Error -> {

                    }
                }
            }
        }



    }

    private fun initStatus() {
        viewModel.chatStatus.observe(viewLifecycleOwner) {
            when (it) {
                ChatStatus.ERROR -> {
                    SharedPreferencesUtils.setString(SEND_STATUS, "error")
                }

                ChatStatus.DONE -> {
                    SharedPreferencesUtils.setString(SEND_STATUS, "done")
                }

                ChatStatus.LOADING -> {

                }
            }
        }
    }

    private fun initSender() {
        val senderUser = conversations.grouAttachConvResList.filter {
            it.email != SharedPreferencesUtils.getString(
                EMAIL_USER
            )
        }
        val avataSender = senderUser.get(0).avatarLocation
        if (avataSender != null) {
            Glide.with(requireContext()).load(avataSender).into(binding.profilePicLayout.imgAvata)

        } else {
            binding.profilePicLayout.imgAvata.setImageResource(R.drawable.ic_avatar_error)
        }
        SharedPreferencesUtils.setString(
            AVATAR_SENDER,
            if (avataSender != null) avataSender else ""
        )
        val nameSender = senderUser.get(0).contactName
        binding.otherUsername.text = nameSender
    }

    private fun init() {
        binding.apply {


//            val scrollListener = object : RecyclerView.OnChildAttachStateChangeListener {
//                override fun onChildViewAttachedToWindow(view: View) {
//                    // Kiểm tra nếu view đính kèm là item cuối cùng trong danh sách
//                    val lastPosition = resultList.size - 1
//                    if (binding.chatRecyclerView.getChildAdapterPosition(view) == lastPosition) {
//                        // Cuộn xuống vị trí mới
//                        binding.chatRecyclerView.post {
//                            binding.chatRecyclerView.smoothScrollToPosition(lastPosition)
//                        }
//                    }
//                }
//
//                override fun onChildViewDetachedFromWindow(view: View) {
//                    // Không cần xử lý
//                }
//            }
//            chatRecyclerView.addOnChildAttachStateChangeListener(scrollListener)
//        }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }


}