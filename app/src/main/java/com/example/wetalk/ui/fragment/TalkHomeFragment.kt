package com.example.wetalk.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter

import com.example.wetalk.R
import com.example.wetalk.databinding.FragmentTalkHomeBinding
import com.example.wetalk.ui.activity.MainActivity

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
            pagerMain.adapter = object : FragmentStatePagerAdapter(
                (activity as MainActivity).supportFragmentManager,
                BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
            ) {
                override fun getItem(position: Int): Fragment {
                    // Trả về fragment tại vị trí position
                    return when (position) {
                        0 -> (activity as MainActivity).supportFragmentManager.findFragmentById(R.id.talkStudyFragment)
                            ?: TalkChatFragment()

                        1 -> (activity as MainActivity).supportFragmentManager.findFragmentById(R.id.talkStudyFragment)
                            ?: TalkStudyFragment()

                        2 -> (activity as MainActivity).supportFragmentManager.findFragmentById(R.id.talkProfileFragment)
                            ?: TalkProfileFragment()

                        else -> throw IllegalArgumentException("Invalid position: $position")
                    }
                }

                override fun getCount(): Int {
                    return 3
                }
            }
        }
    }

}