package com.example.wetalk.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.example.wetalk.R

/**
 * A simple [Fragment] subclass.
 * Use the [TalkNetWorkFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TalkNetWorkFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_talk_net_work, container, false)
    }

}