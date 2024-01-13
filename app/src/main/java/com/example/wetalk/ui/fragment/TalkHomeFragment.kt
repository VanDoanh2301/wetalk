package com.example.wetalk.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.wetalk.R
import com.example.wetalk.data.local.MenuHome
import com.example.wetalk.databinding.FragmentTalkHomeBinding
import com.example.wetalk.ui.activity.MainActivity
import com.example.wetalk.ui.adapter.TalkMenuAdapter
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
        onClickView()

    }
    private fun onClickView() {
        binding.apply {
            btnChat.setOnClickListener {
             findNavController().navigate(R.id.action_talkHomeFragment_to_talkChatHomeFragment)
            }
            btnAthen.setOnClickListener {
                findNavController().navigate(R.id.action_talkHomeFragment_to_talkSignLanguageFragment)
            }
            numberBtn.setOnClickListener {
                findNavController().navigate(R.id.action_talkHomeFragment_to_talkSignNumberFragment)
            }
            btnCharac.setOnClickListener {
                findNavController().navigate(R.id.action_talkHomeFragment_to_talkSignFragment)
            }
            btnTest.setOnClickListener {
                findNavController().navigate(R.id.action_talkHomeFragment_to_talkTopicFragment)
            }
            btnProvide.setOnClickListener {
                findNavController().navigate(R.id.action_talkHomeFragment_to_talkVocabularyUpFragment)
            }
        }
    }
//    private fun getMenus(): ArrayList<MenuHome> {
//        val menus: ArrayList<MenuHome> = ArrayList<MenuHome>()
//        menus.add(MenuHome(R.drawable.menu_hangtag, requireContext().getString(R.string.hashtag)))
//        menus.add(MenuHome(R.drawable.menu_passcode, requireContext().getString(R.string.pass_code)))
//        menus.add(MenuHome(R.drawable.menu_notification, requireContext().getString(R.string.notification)))
//        menus.add(MenuHome(R.drawable.menu_atlas, requireContext().getString(R.string.atlas)))
//        menus.add(MenuHome(R.drawable.menu_backup, requireContext().getString(R.string.backup_restore)))
//        menus.add(MenuHome(R.drawable.setting_share, requireContext().getString(R.string.setting)))
//        menus.add(MenuHome(R.drawable.ic_logout, "Đăng xuất"))
//        return menus
//    }


}