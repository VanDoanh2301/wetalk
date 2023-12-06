package com.example.wetalk.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.wetalk.R
import com.example.wetalk.databinding.FragmentTalkHomeBinding
import com.example.wetalk.ui.activity.MainActivity
import com.example.wetalk.util.Utils


/**
 * A simple [Fragment] subclass.
 * Use the [TalkHomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TalkHomeFragment : Fragment() {
    private var _binding:FragmentTalkHomeBinding? =null
    private val binding get() = _binding!!

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
        initBottomNavigation()

    }

    private fun initBottomNavigation() {
        binding.fgHome.apply {
            /** Custom viewpager */
            pagerMain.adapter = object : FragmentStateAdapter(requireActivity()) {
                override fun getItemCount(): Int {
                    return 4
                }

                override fun createFragment(position: Int): Fragment {
                    return when (position) {
                        0 -> TalkChatFragment()
                        1 -> TalkStudyFragment()
                        2 -> TalkProfileFragment()
                        3 -> TalkNetWorkFragment()
                        else -> throw IllegalArgumentException("Invalid position: $position")
                    }
                }
            }
            /** config onClick tab */
            btTabMess.setOnClickListener { setCurrentTab(pagerMain, 0) }
            btTabStudy.setOnClickListener { setCurrentTab(pagerMain, 1) }
            btTabProfile.setOnClickListener { setCurrentTab(pagerMain, 2) }
            btTabNetwork.setOnClickListener { setCurrentTab(pagerMain, 3) }
            /** config tab view bottom */
            pagerMain.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    setColorImageView(imgTabMess, imgTabStudy, imgTabProfile, imgTabNetwork)
                    setColorTextView(viewTabMess, viewTabStudy, viewTabProfile, viewTabNetwork)
                    when (position) {
                        0 -> {
                            setColorTextViewSelect(viewTabMess)
                            setColorImageViewSelect(imgTabMess)
                        }
                        1 -> {
                            setColorTextViewSelect(viewTabStudy)
                            setColorImageViewSelect(imgTabStudy)
                        }
                        2 -> {
                            setColorTextViewSelect(viewTabProfile)
                            setColorImageViewSelect(imgTabProfile)
                        }
                        else -> {
                            setColorTextViewSelect(viewTabNetwork)
                            setColorImageViewSelect(imgTabNetwork)
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
            Utils.setColorFilter(view, Color.parseColor("#000000"))
        }
    }

    private fun setColorImageViewSelect(vararg views: ImageView) {
        for (view in views) {
            Utils.setColorFilter(view, "#9ACFFF")
        }
    }

}