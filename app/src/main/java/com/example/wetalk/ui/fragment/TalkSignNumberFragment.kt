package com.example.wetalk.ui.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.wetalk.databinding.FragmentTalkSignNumberBinding
import com.example.wetalk.ui.adapter.StudyAdapter
import com.example.wetalk.util.DialogVideo
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
        binding.btBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
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
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Đang tải")
        progressDialog.setMessage("Xin chờ...")
        progressDialog.show()

        // Use a callback to get the video URL asynchronously
        getVideoURL(letter) { videoUrl ->
            progressDialog.dismiss()

            DialogVideo.Builder(requireContext())
                .title("Số $letter")
                .urlVideo(videoUrl)
                .show()
        }
    }
}