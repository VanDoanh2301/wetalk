package com.example.wetalk.ui.fragment

import android.R
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.wetalk.databinding.FragmentTalkHistoryVocabulariesBinding
import com.example.wetalk.ui.adapter.CollectDataAdapter
import com.example.wetalk.ui.viewmodels.CollectDataMeViewModel
import com.example.wetalk.util.Resource
import com.example.wetalk.util.showToast
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 * Use the [CollectDataMeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class CollectDataMeFragment : Fragment() {
    private var _binding: FragmentTalkHistoryVocabulariesBinding? = null
    private val binding get() = _binding!!
    private val TAG = "TalkViewHistoryFragment"
    private val viewModel: CollectDataMeViewModel by viewModels()
    private lateinit var collectDataAdapter: CollectDataAdapter
    private lateinit var collectDataAdapterApproved: CollectDataAdapter
    private lateinit var collectDataAdapterReject: CollectDataAdapter

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
        _binding = FragmentTalkHistoryVocabulariesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectDataAdapter = CollectDataAdapter(requireContext())
        collectDataAdapterApproved = CollectDataAdapter(requireContext())
        collectDataAdapterReject = CollectDataAdapter(requireContext())
        binding.rcvView.visibility = View.VISIBLE
        init()
        onView()
        initSpinner()
    }

    private fun onView() {
        binding.btnMenu.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun initSpinner() {
        val statusList = listOf(STATUS_PENDING, STATUS_APPROVED, STATUS_REJECT)
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_dropdown_item,
            statusList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.btStatus.adapter = adapter
        binding.btStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val selectedStatus = p0?.getItemAtPosition(p2).toString()
                when (selectedStatus) {
                    STATUS_PENDING -> {
                        onPending()
                    }

                    STATUS_APPROVED -> {
                        onApprove()
                    }

                    STATUS_REJECT -> {
                        onReject()
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
    }

    private fun onReject() {
        initRcView()
        binding.rcvView.visibility = View.VISIBLE
        viewModel.getCollectDataRejectMe().observe(viewLifecycleOwner) {
            if (it.isSuccessful) {
                try {
                    var vocabularies = it.body()?.data

                    binding.apply {
                        val linearLayoutManager = StaggeredGridLayoutManager(
                            1,
                            StaggeredGridLayoutManager.VERTICAL
                        )
                        rcvReject.layoutManager = linearLayoutManager
                        rcvReject.adapter = collectDataAdapter
                        collectDataAdapterReject.submitList(vocabularies!!)
                    }
                } catch (e: Exception) {

                }
            }
        }
    }

    private fun onApprove() {
        initRcView()
        binding.rcvApproved.visibility = View.VISIBLE
        viewModel.getCollectDataApprovedMe().observe(viewLifecycleOwner) {
            if (it.isSuccessful) {
                try {
                    var vocabularies = it.body()?.data

                    binding.apply {
                        val linearLayoutManager = StaggeredGridLayoutManager(
                            1,
                            StaggeredGridLayoutManager.VERTICAL
                        )
                        rcvApproved.layoutManager = linearLayoutManager
                        rcvApproved.adapter = collectDataAdapter
                        collectDataAdapterApproved.submitList(vocabularies!!)
                    }
                } catch (e: Exception) {

                }
            }
        }
    }

    private fun onPending() {
        initRcView()
        binding.rcvView.visibility = View.VISIBLE
        viewModel.getCollectDataRejectMe().observe(viewLifecycleOwner) {
            if (it.isSuccessful) {
                try {
                    var vocabularies = it.body()?.data

                    binding.apply {
                        val linearLayoutManager = StaggeredGridLayoutManager(
                            1,
                            StaggeredGridLayoutManager.VERTICAL
                        )
                        rcvView.layoutManager = linearLayoutManager
                        rcvView.adapter = collectDataAdapter
                        collectDataAdapter.submitList(vocabularies!!)
                    }
                } catch (e: Exception) {

                }
            }
        }
    }

    private fun initRcView() {
        binding.apply {
            rcvView.visibility = View.GONE
            rcvApproved.visibility = View.GONE
            rcvReject.visibility = View.GONE
        }
    }

    private fun init() {
        lifecycleScope.launchWhenStarted {
            viewModel.getCollectDataMe()
            viewModel.collectDatas.observe(viewLifecycleOwner) {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        try {
                            var vocabularies = it.data!!.data

                            binding.apply {
                                val linearLayoutManager = StaggeredGridLayoutManager(
                                    1,
                                    StaggeredGridLayoutManager.VERTICAL
                                )
                                rcvView.layoutManager = linearLayoutManager
                                rcvView.adapter = collectDataAdapter
                                collectDataAdapter.submitList(vocabularies)
                            }
                        } catch (e: Exception) {

                        }
                    }

                    is Resource.Error -> {
                        requireContext().showToast()
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CollectDataMeFragment().apply {
                arguments = Bundle().apply {

                }
            }

        const val STATUS_PENDING = "Danh sách chờ"
        const val STATUS_APPROVED = "Danh sách chấp nhận"
        const val STATUS_REJECT = "Danh sách từ chối"
    }

}