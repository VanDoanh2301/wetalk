package com.example.wetalk.ui.fragment

import TopicAdapter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wetalk.R
import com.example.wetalk.data.model.objectmodel.TopicRequest
import com.example.wetalk.databinding.FragmentTalkTopicBinding
import com.example.wetalk.ui.viewmodels.TalkTopicViewModel
import com.example.wetalk.util.Resource
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 * Use the [TalkTopicFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class TalkTopicFragment : Fragment() {
    private var _binding:FragmentTalkTopicBinding ? = null
    private val binding get() = _binding!!
    private val viewModel : TalkTopicViewModel by viewModels()
    private lateinit var adapter: TopicAdapter
    private var topicRequests : ArrayList<TopicRequest> ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTalkTopicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = TopicAdapter(requireContext())

        binding.btBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        lifecycleScope.launchWhenStarted {
            viewModel.getAllTopic()
            viewModel.topic.collect {
                when(it) {
                    is Resource.Success -> {
                        topicRequests = it.data!!.data
                        Log.d("Topic", topicRequests.toString())
                        try {
                            adapter.notifyData(topicRequests!!)
                            binding.rcvView.layoutManager = LinearLayoutManager(requireContext())
                            binding.rcvView.adapter = adapter
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), "Topic is Null", Toast.LENGTH_SHORT).show()
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
                Log.d("id", topicRequests!![position].id.toString())
                findNavController().navigate(R.id.action_talkTopicFragment_to_talkTestFragment, bundle)
            }

        })
    }
    companion object {
        @JvmStatic
        fun newInstance() =
            TalkTopicFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}