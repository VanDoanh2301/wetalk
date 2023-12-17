package com.example.wetalk.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.example.wetalk.data.local.PracticeQuest
import com.example.wetalk.data.local.Question
import com.example.wetalk.databinding.FragmentTalkTestBinding
import com.example.wetalk.ui.activity.MainActivity
import com.example.wetalk.ui.adapter.MenuPracticeAdapter
import com.example.wetalk.ui.adapter.ViewPagerPracticeAdapter

/**
 * A simple [Fragment] subclass.
 * Use the [TalkTestFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TalkTestFragment : Fragment() {
    private var _binding: FragmentTalkTestBinding? =null
    private val binding get() = _binding!!
    private var currentIndex = 0
    private var practiceQuests: ArrayList<PracticeQuest>? = null
    private lateinit var menuTestHardAdapter: MenuPracticeAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTalkTestBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        practiceQuests = getDataTest()
        initPagerHome();
    }

    private fun getDataTest(): ArrayList<PracticeQuest> {
        var practices = ArrayList<PracticeQuest>()

        practices.add(PracticeQuest(0,
            Question(
            0,
            "Day la gi?",
            "A",
            "B",
            "C",
            "D",
            "A",
            "",
            "",
            ""),
            "A",
            false,
            0
            ))
        practices.add(PracticeQuest(1,
            Question(
                1,
                "Video tren la gi?",
                "A",
                "B",
                "C",
                "D",
                "B",
                "",
                "",
                ""),
            "B",
            false,
            0
        ))
        return practices
    }

    private fun initPagerHome() {
        val adapter = ViewPagerPracticeAdapter(
            (activity as MainActivity).supportFragmentManager,
            0,
            practiceQuests!!)
        binding.mainContent.viewpager.addOnPageChangeListener(viewPagerPageChangeListener)
        binding.mainContent.viewpager.adapter = adapter
        menuTestHardAdapter = MenuPracticeAdapter(requireContext(), practiceQuests!!)
        menuTestHardAdapter.setClickListener(object : MenuPracticeAdapter.ItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                binding.mainContent.viewpager.postDelayed(Runnable {
                    currentIndex = position
                    binding.mainContent.viewpager.setCurrentItem(position, false)
                }, 0)
            }
        })
        binding.mainContent.iconNext.setOnClickListener(View.OnClickListener {
            if (currentIndex < practiceQuests!!.size - 1) {
                currentIndex++
                binding.mainContent.viewpager.postDelayed(Runnable {
                    binding.mainContent.viewpager.setCurrentItem(currentIndex, true) }, 0)
            }
        })
        binding.mainContent.iconPrevious.setOnClickListener(View.OnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                binding.mainContent.viewpager.postDelayed(Runnable {
                    binding.mainContent.viewpager.setCurrentItem(currentIndex, true) }, 0)
            }
        })
    }

    var viewPagerPageChangeListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageSelected(position: Int) {
            if (currentIndex != position) {
//                showSpotLight()
            }
            currentIndex = position
            menuTestHardAdapter.setIndexSelection(position)
            binding.mainContent.bottomTitle.text = "Question " + (position + 1) + " / " + practiceQuests!!.size
            menuTestHardAdapter.setCurrent_selected(position)
            if (currentIndex == 0) {
                binding.mainContent.iconPrevious.setColorFilter(Color.parseColor("#BEBEBE"))
            } else {
                binding.mainContent.iconPrevious.setColorFilter(Color.parseColor("#ffffff"))
            }
            if (currentIndex == practiceQuests!!.size - 1) {
                binding.mainContent.iconNext.setColorFilter(Color.parseColor("#BEBEBE"))
            } else {
                binding.mainContent.iconNext.setColorFilter(Color.parseColor("#ffffff"))
            }
        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
        override fun onPageScrollStateChanged(arg0: Int) {}
    }

}