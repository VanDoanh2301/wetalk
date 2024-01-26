package com.example.wetalk.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController

import com.example.wetalk.R
import com.example.wetalk.ui.activity.MainActivity
import com.rey.material.widget.Button
import com.rey.material.widget.ImageView

/**
 * A simple [Fragment] subclass.
 * Use the [TalkDoneFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TalkDoneFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_talk_done, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<ImageView>(R.id.login_let_me_in_btn).setOnClickListener {
//            (activity as MainActivity).supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            findNavController().navigate(R.id.talkLoginFragment)
        }
    }
}