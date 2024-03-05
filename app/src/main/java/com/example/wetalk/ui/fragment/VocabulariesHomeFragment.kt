package com.example.wetalk.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.wetalk.data.model.objectmodel.TopicRequest
import com.example.wetalk.databinding.FragmentVocabulariesHomeBinding
import com.example.wetalk.ui.activity.MainActivity
import com.example.wetalk.ui.adapter.VocabulariesAdapter
import com.example.wetalk.ui.viewmodels.VocabulariesViewModel
import com.example.wetalk.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import java.lang.NullPointerException

@AndroidEntryPoint
class VocabulariesHomeFragment : Fragment() {
    private var _binding: FragmentVocabulariesHomeBinding ?=null
    private val binding get() = _binding!!
    private var id = 0
    private val viewModel: VocabulariesViewModel by viewModels()
    private var resultlist: ArrayList<TopicRequest> = ArrayList()
    private lateinit var vocabulariesAdapter: VocabulariesAdapter
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
        _binding = FragmentVocabulariesHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        id = arguments?.getInt("id", -1)!!
        vocabulariesAdapter = VocabulariesAdapter(requireContext())
        init()
        initView()

    }
    private fun initView() {
        binding.btBack.setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }
        vocabulariesAdapter.setOnItemClickAddFriend(object : VocabulariesAdapter.OnItemClick{
            override fun onItem(position: Int, topicRequest: TopicRequest) {

            }

        })
    }
    private fun init() {
        lifecycleScope.launchWhenStarted {
            viewModel.getAllVocabulariesByTopicId(id)
            viewModel.vocabularies.collect {
                when(it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        try {
                            var vocabularies = it.data!!.data
                            binding.apply {
                                val linearLayoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
                                rcvView.layoutManager = linearLayoutManager
                                rcvView.adapter = vocabulariesAdapter
                                vocabulariesAdapter.submitList(vocabularies)
                            }
                        } catch (e : Exception) {

                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            VocabulariesHomeFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}