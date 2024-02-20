package com.example.wetalk.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.wetalk.data.model.objectmodel.UserInforRequest
import com.example.wetalk.databinding.FragmentTalkMainChatBinding
import com.example.wetalk.util.FileConfigUtils


/**
 * A simple [Fragment] subclass.
 * Use the [TalkMainChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TalkMainChatFragment : Fragment() {
    private var _binding: FragmentTalkMainChatBinding ? =null
    private val binding get() = _binding!!
    private lateinit var user: UserInforRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            user = bundle.getParcelable("userData") ?: throw IllegalArgumentException("User data not found in arguments")
        }
        Log.d("TAG", user.toString())
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
                        0 -> TalkTabChatFragment(user)
                        1 -> TalkTabPhoneBookFragment()
                        2 -> TalkTabProfileFragment(user)
                        else -> throw IllegalArgumentException("Invalid position: $position")
                    }
                }
            }
            /** config onClick tab */
            btTabMess.setOnClickListener { setCurrentTab(pagerMain, 0) }
            btTabFriend.setOnClickListener { setCurrentTab(pagerMain, 1) }
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