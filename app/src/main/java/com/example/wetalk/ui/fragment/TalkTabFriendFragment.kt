package com.example.wetalk.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wetalk.R
import com.example.wetalk.data.model.objectmodel.UserInforRequest
import com.example.wetalk.databinding.FragmentTalkTabPhoneBookBinding
import com.example.wetalk.ui.adapter.FriendAdapter
import com.example.wetalk.ui.adapter.PendingAdapter
import com.example.wetalk.ui.adapter.UserSearchAdapter
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 * Use the [TalkTabFriendFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class TalkTabFriendFragment : Fragment() {
    private var _binding: FragmentTalkTabPhoneBookBinding? = null
    private val binding get() = _binding!!
    private lateinit var friendAdapter: FriendAdapter
    private lateinit var pendingAdapter: PendingAdapter
    private var userPending: List<UserInforRequest> = ArrayList()
    private var userFriend: List<UserInforRequest> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTalkTabPhoneBookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()

    }

    private fun init() {
        binding.apply {
            pendingAdapter = PendingAdapter(requireContext())
            val linearLayoutPending = LinearLayoutManager(requireContext())
            rcvPending.layoutManager = linearLayoutPending
            rcvPending.adapter = pendingAdapter
            pendingAdapter.submitList(userPending)


            friendAdapter = FriendAdapter(requireContext())
            val linearLayoutFriend = LinearLayoutManager(requireContext())
            rcvFriend.layoutManager = linearLayoutFriend
            rcvFriend.adapter = friendAdapter
            friendAdapter.submitList(userFriend)

        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            TalkTabFriendFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}