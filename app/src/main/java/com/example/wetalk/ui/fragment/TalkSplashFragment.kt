package com.example.wetalk.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

import com.example.wetalk.R
import com.example.wetalk.util.SharedPreferencesUtils

/**
 * A simple [Fragment] subclass.
 * Use the [TalkSplashFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TalkSplashFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }
        return inflater.inflate(R.layout.fragment_talk_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (SharedPreferencesUtils.getString("isLogin") != null) {
            val bundle = bundleOf(
                "isUser" to true
            )
            findNavController().navigate(R.id.talkHomeFragment, bundle)
        } else {
            findNavController().navigate(R.id.action_talkSplashFragment_to_talkLoginFragment)
        }

    }
}