package com.example.wetalk.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wetalk.R
import com.example.wetalk.data.model.objectmodel.UserInforRequest
import com.example.wetalk.databinding.FragmentTalkTabChatBinding
import com.example.wetalk.ui.activity.MainActivity
import com.example.wetalk.ui.viewmodels.ProfileHomeViewModel
import com.example.wetalk.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 * Use the [TalkTabChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class TalkTabChatFragment() : Fragment(){
    private var _binding: FragmentTalkTabChatBinding? = null
    private val binding get() = _binding!!
    private val TAG = "TalkTabChatFragment"
    private var user : UserInforRequest ?= null
    private val viewModel: ProfileHomeViewModel by viewModels()

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
        initView()
        onView()
    }
    private fun onView() {
        binding.edtSearch.setOnClickListener {
            BaseDialogFragment.add(activity  as MainActivity, TalkSearchUserFragment.newInstance())
        }
    }
    private fun initView() {
        //Job when start lifecycle
        lifecycleScope.launchWhenStarted {
            viewModel.getUser()
            viewModel.getInforUser.collect {
                when (it) {
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        val newUser = it.data!!
                        user = newUser
                    }
                    is Resource.Error -> {
                        user = UserInforRequest(0, "Người dùng ẩn danh","abcxz@gmail.com","000000000","HN", "USER", "18", "MALE", R.drawable.ic_avatar_error.toString())
                    }
                }
            }
        }
    }

}
