package com.example.wetalk.ui.fragment

import TopicAdapter
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wetalk.R
import com.example.wetalk.data.model.objectmodel.TopicRequest

import com.example.wetalk.databinding.FragmentTalkSignBinding
import com.example.wetalk.ui.adapter.StudyAdapter
import com.example.wetalk.ui.viewmodels.TopicViewModel
import com.example.wetalk.util.DialogOpenVideo
import com.example.wetalk.util.Resource
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint

/**
 * A simple [Fragment] subclass.
 * Use the [TalkSignFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class TalkSignFragment : Fragment() {
    private var _binding: FragmentTalkSignBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TopicAdapter
    private val viewModel: TopicViewModel by viewModels()
    private var topicRequests: ArrayList<TopicRequest>? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTalkSignBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = TopicAdapter(requireContext())
        binding.btBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        initData()
    }

    private fun initData() {

        lifecycleScope.launchWhenStarted {
            viewModel.getAllTopic()
            viewModel.topic.collect {
                when (it) {
                    is Resource.Success -> {
                        topicRequests = it.data!!.data
                        Log.d("Topic", topicRequests.toString())
                        try {
                            adapter.notifyData(topicRequests!!)
                            binding.rcvView.layoutManager = LinearLayoutManager(requireContext())
                            binding.rcvView.adapter = adapter
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), "Topic is Null", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                    is Resource.Loading -> {
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                }
            }

        }
        onClickItem()
    }

    private fun onClickItem() {
        adapter.setItemClickListener(object : TopicAdapter.ItemClickListener {
            override fun onItemClick(position: Int) {

                val bundle = bundleOf(
                    "id" to topicRequests!![position].id,
                )

            }
        })
    }

}