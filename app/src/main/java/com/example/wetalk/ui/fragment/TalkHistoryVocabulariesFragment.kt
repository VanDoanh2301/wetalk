package com.example.wetalk.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.wetalk.R
import com.example.wetalk.data.model.objectmodel.TopicRequest
import com.example.wetalk.databinding.FragmentTalkHistoryVocabulariesBinding
import com.example.wetalk.ui.adapter.VocabulariesAdapter
import com.example.wetalk.ui.viewmodels.VocabulariesHistoryViewModel
import com.example.wetalk.util.Resource
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 * Use the [TalkHistoryVocabulariesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class TalkHistoryVocabulariesFragment : Fragment() {
    private var _binding: FragmentTalkHistoryVocabulariesBinding? =null
    private val binding get() =  _binding!!
    private var resultlist: ArrayList<TopicRequest> = ArrayList()
    private val TAG = "TalkViewHistoryFragment"
    private val viewModel : VocabulariesHistoryViewModel by viewModels()
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
        _binding =  FragmentTalkHistoryVocabulariesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        onView()
    }
    private fun onView() {
        binding.btBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }
    private fun init() {
        lifecycleScope.launchWhenStarted {
            viewModel.getVocabulariesData()
            viewModel.vocabularies.observe(viewLifecycleOwner) {
                when(it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        try {
                            var vocabularies = it.data!!.data
                            resultlist.addAll(vocabularies)
                            binding.apply {
                                val linearLayoutManager = StaggeredGridLayoutManager(
                                    2,
                                    StaggeredGridLayoutManager.VERTICAL
                                )
                                rcvView.layoutManager = linearLayoutManager
                                rcvView.adapter = vocabulariesAdapter
                                vocabulariesAdapter.submitList(vocabularies)
                            }
                        } catch (e: Exception) {

                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "Lỗi náy chủ ${it.message.toString()}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TalkHistoryVocabulariesFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}