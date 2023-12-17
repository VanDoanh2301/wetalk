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
import com.example.wetalk.databinding.FragmentTalkSignLanguageBinding
import com.example.wetalk.databinding.FragmentTalkSignNumberBinding
import com.example.wetalk.ui.adapter.StudyAdapter
import com.google.firebase.storage.FirebaseStorage

/**
 * A simple [Fragment] subclass.
 * Use the [TalkSignNumberFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TalkSignNumberFragment : Fragment() {
    private var _binding: FragmentTalkSignNumberBinding? =null
    private val binding get() = _binding!!
    private lateinit var adapter: StudyAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTalkSignNumberBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListNumber();
    }

    private fun initListNumber() {
        adapter = StudyAdapter(requireContext(), dataList = generateNumber())
        adapter.notifyDataSetChanged()
        binding.rcvView.layoutManager = GridLayoutManager(requireContext(), 4)
        binding.rcvView.adapter = adapter

        adapter.setOnItemClickListener(object  : StudyAdapter.OnItemClickListener {
            override fun onItemClick(letter: String) {
                showVideoDialog(letter)
            }

        })
    }

    private fun generateNumber(): ArrayList<String> {
        val alphabet = ArrayList<String>()
        for (char in '1'..'9') {
            alphabet.add(char.toString())
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