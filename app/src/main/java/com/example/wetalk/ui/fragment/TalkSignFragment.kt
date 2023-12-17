package com.example.wetalk.ui.fragment

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager

import com.example.wetalk.R
import com.example.wetalk.databinding.FragmentTalkSignBinding
import com.example.wetalk.databinding.FragmentTalkSignNumberBinding
import com.example.wetalk.ui.adapter.StudyAdapter
import com.google.firebase.storage.FirebaseStorage

/**
 * A simple [Fragment] subclass.
 * Use the [TalkSignFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TalkSignFragment : Fragment() {
    private var _binding: FragmentTalkSignBinding? =null
    private val binding get() = _binding!!
    private lateinit var adapter: StudyAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTalkSignBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListSign();
    }

    private fun initListSign() {
        adapter = StudyAdapter(requireContext(), dataList = generateSign())
        adapter.notifyDataSetChanged()
        binding.rcvView.layoutManager = GridLayoutManager(requireContext(), 4)
        binding.rcvView.adapter = adapter

        adapter.setOnItemClickListener(object  : StudyAdapter.OnItemClickListener {
            override fun onItemClick(letter: String) {
                showVideoDialog(letter)
            }

        })
    }
    private fun generateSign(): ArrayList<String> {
        val alphabet = ArrayList<String>()
        for (charCode in 224..244) {
            alphabet.add(charCode.toChar().toString())
        }
        return alphabet
    }
    private fun getVideoURL(letter: String, callback: (String) -> Unit) {
        val storage = FirebaseStorage.getInstance()
        val videoRef = storage.reference.child("videos/${letter}.mp4")

        videoRef.downloadUrl
            .addOnSuccessListener { uri ->
                callback(uri.toString())
            }
            .addOnFailureListener { exception ->
            }
    }
    private fun showVideoDialog(letter: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Video for $letter")

        // Use a callback to get the video URL asynchronously
        getVideoURL(letter) { videoUrl ->
            val videoView = VideoView(requireContext())
            videoView.setVideoURI(Uri.parse(videoUrl))
            videoView.start()

            builder.setView(videoView)
            builder.setPositiveButton("Đóng") { dialog, _ -> dialog.dismiss() }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }

}