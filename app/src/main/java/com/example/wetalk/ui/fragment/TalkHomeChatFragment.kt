package com.example.wetalk.ui.fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.wetalk.data.model.objectmodel.UserInforRequest
import com.example.wetalk.databinding.FragmentTalkMainChatBinding
import com.example.wetalk.ui.viewmodels.ProfileHomeViewModel
import com.example.wetalk.util.FileConfigUtils
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 * Use the [TalkHomeChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class TalkHomeChatFragment : Fragment() {
    private var _binding: FragmentTalkMainChatBinding ? =null
    private val binding get() = _binding!!
    private var user: UserInforRequest? = null
    private val viewModel: ProfileHomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTalkMainChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       initBottomNavigation()
        binding.btnMenu.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }


    private fun initBottomNavigation() {
        binding.apply {
            /** Custom viewpager */
            pagerMain.adapter = object : FragmentStateAdapter(requireActivity()) {
                override fun getItemCount(): Int {
                    return 3
                }

                override fun createFragment(position: Int): Fragment {
                    return when (position) {
                        0 -> TalkTabChatFragment()
                        1 -> TalkTabFriendFragment()
                        2 -> TalkTabProfileFragment()
                        else -> throw IllegalArgumentException("Invalid position: $position")
                    }
                }
            }
            /** config onClick tab */
            btTabMess.setOnClickListener { setCurrentTab(pagerMain, 0) }
            btTabFriend.setOnClickListener {
                setCurrentTab(pagerMain, 1)
                TalkTabFriendFragment.newInstance().updateUI(true)
            }
            btTabProfile.setOnClickListener { setCurrentTab(pagerMain, 2) }
            /** config tab view bottom */
            pagerMain.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    setColorImageView(imgTabMess, imgPhonebook, imgTabProfile)
                    setColorTextView(viewTabMess, viewTabPhonebook, viewTabProfile)
                    when (position) {
                        0 -> {
                            setColorTextViewSelect(viewTabMess)
                            setColorImageViewSelect(imgTabMess)
                        }
                        1 -> {
                            setColorTextViewSelect(viewTabPhonebook)
                            setColorImageViewSelect(imgPhonebook)
                        }
                        2 -> {
                            setColorTextViewSelect(viewTabProfile)
                            setColorImageViewSelect(imgTabProfile)
                        }
                    }
                }
            })
            setColorTextViewSelect(viewTabMess)
            setColorImageViewSelect(imgTabMess)
        }
    }


    private fun setCurrentTab(viewPager: ViewPager2, i: Int) {
        viewPager.currentItem = i
    }


    private fun setColorTextView(vararg views: TextView) {
        for (view in views) {
            view.setTextColor(Color.parseColor("#000000"))
        }
    }

    private fun setColorTextViewSelect(vararg views: TextView) {
        for (view in views) {
            view.setTextColor(Color.parseColor("#9ACFFF"))
        }
    }

    private fun setColorImageView(vararg views: ImageView) {
        for (view in views) {
            FileConfigUtils.setColorFilter(view, Color.parseColor("#000000"))
        }
    }

    private fun setColorImageViewSelect(vararg views: ImageView) {
        for (view in views) {
            FileConfigUtils.setColorFilter(view, "#9ACFFF")
        }
    }

}