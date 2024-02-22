package com.example.wetalk.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager


import com.example.wetalk.data.local.DataModel
import com.example.wetalk.data.local.DataModelType
import com.example.wetalk.data.model.objectmodel.UserInforRequest
import com.example.wetalk.databinding.FragmentTalkTabChatBinding
import com.example.wetalk.repository.FireBaseRepository
import com.example.wetalk.service.MainService
import com.example.wetalk.service.MainServiceRepository
import com.example.wetalk.ui.activity.MainActivity
import com.example.wetalk.ui.adapter.UserStatusAdapter
import com.example.wetalk.util.getCameraAndMicPermission
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 * Use the [TalkTabChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class TalkTabChatFragment(private val user: UserInforRequest) : Fragment(),
    UserStatusAdapter.Listener,
    MainService.Listener {

    private var _binding: FragmentTalkTabChatBinding? = null
    private val binding get() = _binding!!
    @Inject
    lateinit var fireBaseRepository: FireBaseRepository
    @Inject
    lateinit var mainServiceRepository: MainServiceRepository
    private var statusAdapter: UserStatusAdapter? = null
    private var username: String? = null
    private val TAG = "TalkTabChatFragment"

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
        initView()


    }

    private fun initView() {
        if (user != null) {
            username = user.name
        } else {
            username = ""
        }
        subscribeObservers()
        startMyService()

    }

    private fun subscribeObservers() {
        setupRecyclerView()
        MainService.listener = this
        fireBaseRepository.observeUsersStatus {
            Log.d(TAG, "subscribeObservers: $it")
            if (it != null) {
                binding.rvFriend.visibility = View.VISIBLE
                binding.lnlNotFriend.visibility = View.GONE
                statusAdapter?.updateList(it)
            } else {
                binding.rvFriend.visibility = View.GONE
                binding.lnlNotFriend.visibility = View.VISIBLE
            }

        }
    }

    private fun setupRecyclerView() {
        statusAdapter = UserStatusAdapter(this)
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvFriend.layoutManager = layoutManager
        binding.rvFriend.adapter = statusAdapter

    }

    private fun startMyService() {
        mainServiceRepository.startService(username!!)
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
        fun newInstance(user: UserInforRequest) =
            TalkTabChatFragment(user).apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onVideoCallClicked(username: String) {
        //check if permission of mic and camera is taken

//        (activity as MainActivity).getCameraAndMicPermission {
//            fireBaseRepository.sendConnectionRequest(username, true) {
//                if (it) {
//                    startActivity(Intent(requireActivity(), CallActivity::class.java).apply {
//                        putExtra("target", username)
//                        putExtra("isVideoCall", true)
//                        putExtra("isCaller", true)
//                    })
//
//                }
//            }
//
//        }
    }

    override fun onAudioCallClicked(username: String) {

    }

    override fun onCallReceived(model: DataModel) {
        (activity as MainActivity).runOnUiThread {
            binding.apply {
                declineButton.setOnClickListener {
                    incomingCallLayout.isVisible = false
                }
                val isVideoCall = model.type == DataModelType.StartVideoCall
                val isVideoCallText = if (isVideoCall) "Video" else "Audio"
                incomingCallTitleTv.text = "${model.sender} is $isVideoCallText Calling you"
                incomingCallLayout.isVisible = true
                acceptButton.setOnClickListener {
                    (activity as MainActivity).getCameraAndMicPermission {
                        incomingCallLayout.isVisible = false
                        //create an intent to go to video call activity
//                        startActivity(Intent(requireActivity(), CallActivity::class.java).apply {
//                            putExtra("target", username)
//                            putExtra("isVideoCall", true)
//                            putExtra("isCaller", true)
//                        })
                    }
                }
            }
        }

    }
}
