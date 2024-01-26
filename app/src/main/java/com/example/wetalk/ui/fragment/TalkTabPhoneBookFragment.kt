package com.example.wetalk.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.wetalk.R



/**
 * A simple [Fragment] subclass.
 * Use the [TalkTabPhoneBookFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TalkTabPhoneBookFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_talk_tab_phone_book, container, false)
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TalkTabPhoneBookFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}