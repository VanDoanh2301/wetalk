package com.example.wetalk.ui.fragment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wetalk.R
import com.example.wetalk.data.local.SettingObject
import com.example.wetalk.databinding.FragmentSettingBinding
import com.example.wetalk.ui.adapter.SettingAdapter

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? =null
    private val biding get() = _binding!!
    private lateinit var settingAdapter: SettingAdapter
    private var settingObjects : ArrayList<SettingObject>  = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSettingBinding.inflate(inflater, container,false)
        return biding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        fetchData()
        onClickView()

    }
    private fun onClickView() {
        settingAdapter.setOnClickItem(object : SettingAdapter.OnClickItem {
            override fun onClick(position: Int, settingObject: SettingObject) {
                when(position) {
                    0 -> {
                        try {
                            val intent = Intent(Intent.ACTION_SEND)
                            val emails_in_to = arrayOf<String>("")
                            intent.putExtra(Intent.EXTRA_EMAIL, emails_in_to)
                            intent.putExtra(Intent.EXTRA_SUBJECT, "Help")
                            intent.putExtra(
                                Intent.EXTRA_TEXT,
                                "Please help me with your application.."
                            )
                            intent.setType("text/html")
                            intent.setPackage("com.google.android.gm")
                            startActivity(intent)
                        } catch (e: Exception) {
                            val mailer = Intent(Intent.ACTION_SEND)
                            mailer.setType("text/plain")
                            mailer.putExtra(
                                Intent.EXTRA_EMAIL,
                                arrayOf<String>("")
                            )
                            mailer.putExtra(Intent.EXTRA_SUBJECT, "Help")
                            mailer.putExtra(
                                Intent.EXTRA_TEXT,
                                "Please help me with your application.."
                            )
                            startActivity(Intent.createChooser(mailer, "Help"))
                        }

                    }
                    1 -> {
                        try {
                            startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("market://developer?id=" + ""))
                                )

                        } catch (anfe: ActivityNotFoundException) {
                            startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(
                                        "https://play.google.com/store/apps/developer?id="

                                    )
                                )
                            )
                        }
                    }
                    2 -> {

                    }
                    3 -> {
                        try {
                            val i = Intent(Intent.ACTION_SEND)
                            i.setType("text/plain")
                            i.putExtra(Intent.EXTRA_SUBJECT, "")
                            var sAux = "\n\n\n"
                            sAux =
                                sAux + "https://play.google.com/store/apps/details?id=" + ""
                            i.putExtra(Intent.EXTRA_TEXT, sAux)
                            startActivity(Intent.createChooser(i, "Share App"))
                        } catch (e: java.lang.Exception) {
                        }
                    }
                }
            }

        })
    }
    private fun init() {
        biding.apply {
            settingAdapter = SettingAdapter(requireContext())
            val linearLayoutManager = LinearLayoutManager(requireContext())
            rcvView.layoutManager = linearLayoutManager
            rcvView.adapter = settingAdapter
            settingAdapter.submitList(settingObjects)
        }
    }
    private fun fetchData() {
        var settingList : ArrayList<SettingObject> = ArrayList()
//        settingList.add(SettingObject(0, R.drawable.ic_lang, "Select Language"))
        settingList.add(SettingObject(1, R.drawable.ic_feedback, "Feedback"))
        settingList.add(SettingObject(2, R.drawable.ic_other_app, "Other Apps"))
        settingList.add(SettingObject(4, R.drawable.ic_share_app, "Share"))
        settingObjects.addAll(settingList)
    }
    companion object {
        @JvmStatic
        fun newInstance() =
            SettingFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}