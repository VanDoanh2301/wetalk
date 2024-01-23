package com.example.wetalk.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.wetalk.R
import com.example.wetalk.data.local.ImageHome
import com.example.wetalk.data.model.objectmodel.UserInforRequest
import com.example.wetalk.databinding.FragmentTalkHomeBinding
import com.example.wetalk.ui.adapter.ImageAdapter
import com.example.wetalk.ui.viewmodels.TalkProfileHomeViewModel
import com.example.wetalk.util.Resource
import com.example.wetalk.util.SharedPreferencesUtils
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import java.util.TimerTask


/**
 * A simple [Fragment] subclass.
 * Use the [TalkHomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class TalkHomeFragment : Fragment() {
    private var _binding:FragmentTalkHomeBinding? =null
    private val binding get() = _binding!!
    private var isUser = false
    private val viewModel : TalkProfileHomeViewModel by viewModels()
    private lateinit var user: UserInforRequest
    private var images: List<ImageHome> = ArrayList()
    private var timer: Timer? = null
    private lateinit var adapterImage: ImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTalkHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isUser = arguments?.getBoolean("isUser", false) ?: false
        configViewPager()
        slideBar();
        binding.textView2.text = "Người Dùng Khách"
        //Role
        if (isUser) {
            binding.btnChat.visibility = View.VISIBLE
            binding.btnHistory.visibility = View.VISIBLE
            binding.imgKey1.visibility = View.GONE
            binding.imgKey2.visibility = View.GONE
            initView()
        } else {
            binding.btnChat.visibility = View.GONE
            binding.btnHistory.visibility = View.GONE
            binding.imgKey1.visibility = View.VISIBLE
            binding.imgKey2.visibility = View.VISIBLE
        }
        //Click button
        binding.rltChat.setOnClickListener {
            if (!isUser) {
                Toast.makeText(requireContext(), "Vui lòng tạo tài khoản", Toast.LENGTH_LONG).show()
            } else {
                findNavController().navigate(R.id.action_talkHomeFragment_to_talkMainChatFragment)
            }
        }
        binding.rltHis.setOnClickListener {
            if (!isUser) {
                Toast.makeText(requireContext(), "Vui lòng tạo tài khoản", Toast.LENGTH_LONG).show()
            } else {
                findNavController().navigate(R.id.action_talkHomeFragment_to_talkProfileHomeFragment)
            }
        }


        onClickView()

    }
    private fun initView() {
        //Job when start lifecycle
        lifecycleScope.launchWhenStarted {
            val isAccess = SharedPreferencesUtils.getString("isLogin")
            viewModel.getUser("Bearer $isAccess")
            viewModel.getInforUser.collect {
                when (it) {
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        user = it.data!!
                        binding.textView2.text = user.name
                        Glide.with(requireContext()).load(user.avatarLocation).apply(RequestOptions.circleCropTransform())
                            .into(binding.imgAvata)
                    }
                    is Resource.Error -> {
                        Log.d("UserRegisterRequest", it.message.toString())
                    }
                }
            }
        }
    }

    //Change fragment
    private fun onClickView() {
        binding.apply {
            //Go to chat fragment
            btnChat.setOnClickListener {
             findNavController().navigate(R.id.action_talkHomeFragment_to_talkMainChatFragment)
            }
            //Go to study authen
            btnAthen.setOnClickListener {
                findNavController().navigate(R.id.action_talkHomeFragment_to_talkSignLanguageFragment)
            }
            //Go to study number
            numberBtn.setOnClickListener {
                findNavController().navigate(R.id.action_talkHomeFragment_to_talkSignNumberFragment)
            }
            //Go to study character
            btnCharac.setOnClickListener {
                findNavController().navigate(R.id.action_talkHomeFragment_to_talkSignFragment)
            }
            //Go to test
            btnTest.setOnClickListener {
                findNavController().navigate(R.id.action_talkHomeFragment_to_talkTopicFragment)
            }
            //Go to up video
            btnProvide.setOnClickListener {
                findNavController().navigate(R.id.action_talkHomeFragment_to_talkVocabularyUpFragment)
            }
            //Go to logout
            btnLogOut.setOnClickListener {
                //Delete token and pop fragment
                SharedPreferencesUtils.setString("isLogin", null);
                findNavController().popBackStack(R.id.talkLoginFragment, false)
                findNavController().navigate(R.id.talkLoginFragment)
            }
            //Go to profile
            btnHistory.setOnClickListener {
                findNavController().navigate(R.id.action_talkHomeFragment_to_talkProfileHomeFragment)
            }

        }
    }
    private fun configViewPager() {
        images = getListImage()
        adapterImage = ImageAdapter(requireContext(), images)
        binding.vgHome.adapter = adapterImage
        binding.circleBar.setViewPager(binding.vgHome)
        adapterImage.registerDataSetObserver(binding.circleBar.dataSetObserver)
    }
    private fun getListImage(): List<ImageHome> {
        val images: MutableList<ImageHome> = ArrayList()
        images.add(ImageHome(R.drawable.logo, "Cùng nhau học tâp ngôn ngữ kí hiệu"))
        images.add(ImageHome(R.drawable.logo, "Chia sẻ video"))
        images.add(ImageHome(R.drawable.logo, "Làm bài kiểm tra "))
        images.add(ImageHome(R.drawable.logo, "Chat và call video cho bạn bè"))
        return images
    }
    private fun slideBar() {
        images = getListImage()
        if (timer == null) {
            timer = Timer()
        }
        timer!!.schedule(object : TimerTask() {
            override fun run() {
                Handler(Looper.getMainLooper()).post(Runnable {
                    //Get currentItem
                    var currentItem: Int = binding.vgHome.currentItem
                    val total: Int = images.size - 1
                    if (currentItem < total) {
                        currentItem++
                        binding.vgHome.setCurrentItem(currentItem)
                    } else {
                        binding.vgHome.setCurrentItem(0)
                    }
                })
            }
        }, 200, 3000)
    }

}