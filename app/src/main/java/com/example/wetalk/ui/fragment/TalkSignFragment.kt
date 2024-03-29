package com.example.wetalk.ui.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager

import com.example.wetalk.databinding.FragmentTalkSignBinding
import com.example.wetalk.ui.adapter.StudyAdapter
import com.example.wetalk.util.DialogVideo
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
        binding.btBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
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
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Đang tải")
        progressDialog.setMessage("Xin chờ...")
        progressDialog.show()

        // Use a callback to get the video URL asynchronously
        getVideoURL(letter) { videoUrl ->
            progressDialog.dismiss()

            DialogVideo.Builder(requireContext())
                .title("Dấu $letter")
                .urlVideo(videoUrl)
                .show()
        }
    }

}