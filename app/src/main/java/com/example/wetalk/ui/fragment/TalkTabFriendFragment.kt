package com.example.wetalk.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wetalk.data.model.objectmodel.UserInforRequest
import com.example.wetalk.databinding.FragmentTalkTabPhoneBookBinding
import com.example.wetalk.ui.adapter.FriendAdapter
import com.example.wetalk.ui.adapter.PendingAdapter
import com.example.wetalk.ui.viewmodels.FriendTabViewModel
import com.example.wetalk.util.Resource
import com.example.wetalk.util.dialog.DialogInformUser
import com.example.wetalk.util.showToast
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
    private val viewModel: FriendTabViewModel by viewModels()
    private var userPending: ArrayList<UserInforRequest> = ArrayList()
    private var userFriend: ArrayList<UserInforRequest> = ArrayList()
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
        initData()
        onView()

    }
    private fun onView() {
         pendingAdapter.setOnItemClickAddFriend(object : PendingAdapter.OnItemClick{
             override fun onItem(position: Int, user: UserInforRequest) {
                 lifecycleScope.launchWhenResumed {
                      user.id?.let { viewModel.postAcceptFriend(it) }
                      viewModel.acceptFriend.collect {
                         when (it) {
                             is Resource.Loading -> {}
                             is Resource.Success -> {  //get data if response success
                                    pendingAdapter.removeItemAt(position)
                                    initData()
                             }
                             is Resource.Error -> {
                                  requireContext().showToast()
                             }
                         }
                     }

                 }
             }

             override fun onUser(position: Int, user: UserInforRequest) {

             }

         })

        friendAdapter.setOnItemViewUser(object : FriendAdapter.OnItemClick{
            override fun onItem(position: Int, user: UserInforRequest) {

            }

            override fun onUser(position: Int, user: UserInforRequest) {
                  DialogInformUser.Builder(requireContext())
                      .onName(user.name)
                      .onDate(if (user.age != null) user.age else "")
                      .onAddress(if (user.address != null) user.address else "")
                      .onPhone(if (user.phoneNumber != null) user.phoneNumber  else "")
                      .onGender(if (user.gender.equals("MALE")) "Name" else "Nữ")
                      .show()
            }

        })
    }

    private fun initData() {
        lifecycleScope.launchWhenStarted {
            viewModel.getAllFriend() //call fun get all friend
            viewModel.getAllFriendPending() //call fun get pending friend

            viewModel.friends.observe(viewLifecycleOwner) {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {  //get data if response success
                        val friends = it.data!!.data
                        if (friends != null) {
                            userFriend.addAll(friends)
                            friendAdapter.submitList(userFriend)
                        } else {
                            requireContext().showToast("Danh sách trống")
                        }
                    }
                    is Resource.Error -> {
                        requireContext().showToast()
                    }
                }
            }

            viewModel.pendingFriend.observe(viewLifecycleOwner) {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        val pendingFriends = it.data!!.data
                        if (pendingFriends != null) {
                            userPending.addAll(pendingFriends)
                            pendingAdapter.submitList(userPending)
                        } else {

                        }
                    }
                    is Resource.Error -> {requireContext().showToast()}
                }
            }
        }
    }

    private fun init() {
        binding.apply {
            pendingAdapter = PendingAdapter(requireContext())
            val linearLayoutPending = LinearLayoutManager(requireContext())
            rcvPending.layoutManager = linearLayoutPending
            rcvPending.adapter = pendingAdapter


            friendAdapter = FriendAdapter(requireContext())
            val linearLayoutFriend = LinearLayoutManager(requireContext())
            rcvFriend.layoutManager = linearLayoutFriend
            rcvFriend.adapter = friendAdapter


        }
    }

    private fun fetchDataError() : ArrayList<UserInforRequest> {
        val fris : ArrayList<UserInforRequest> = ArrayList()
        fris.add(UserInforRequest(0, "Người dùng ẩn danh","abcxz@gmail.com","000000000","HN", "USER", "18", "MALE", ""))
        fris.add(UserInforRequest(1, "Người dùng ẩn danh","abcxz@gmail.com","000000000","HN", "USER", "18", "MALE", ""))
        fris.add(UserInforRequest(2, "Người dùng ẩn danh","abcxz@gmail.com","000000000","HN", "USER", "18", "MALE", ""))
        return fris
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