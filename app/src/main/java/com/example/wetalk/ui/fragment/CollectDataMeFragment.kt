package com.example.wetalk.ui.fragment

import android.R
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.wetalk.WeTalkApp
import com.example.wetalk.data.model.objectmodel.CollectData
import com.example.wetalk.data.model.postmodel.DataPostSearch
import com.example.wetalk.databinding.FragmentTalkHistoryVocabulariesBinding
import com.example.wetalk.ui.activity.MainActivity
import com.example.wetalk.ui.adapter.CollectDataAdapter
import com.example.wetalk.ui.viewmodels.CollectDataMeViewModel
import com.example.wetalk.util.Resource
import com.example.wetalk.util.Task
import com.example.wetalk.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale


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
    private var collectDataPending : ArrayList<CollectData> = ArrayList()
    private var collectDataApproved : ArrayList<CollectData> = ArrayList()
    private var collectDataReject : ArrayList<CollectData> = ArrayList()
    private var status = 100

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
        initSearch()
    }
    private fun initSearch() {
        binding.apply {
            btCreate.setOnClickListener {
                WeTalkApp.showDatePicker(activity as MainActivity, object : Task<Long> {
                    override fun callback(result: Long) {
                        val format =
                            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val dateString: String = format.format(result)
                        txtDate.text = dateString
                    }
                })
            }
            edtSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                      val txtInput = p0.toString()
                     onSearchByVocabulary(txtInput)
                }

                override fun afterTextChanged(p0: Editable?) {

                }

            })
        }
    }

    private fun onSearchByVocabulary(txtInput:String) {
        if (txtInput.isNotEmpty()) {
            val postSearch = DataPostSearch(
                page = 1,
                size = 50,
                topic = "",
                vocabulary = txtInput,
                ascending = true,
                orderBy = "",
                createdFrom = "",
                createdTo = "",
                status = status
            )
            viewModel.searchCollectData(postSearch).observe(viewLifecycleOwner) {
                if (it.isSuccessful) {
                    val collectDatas = it.body()?.data
                    when(status) {
                        100 -> {
                            collectDataAdapter.submitListClear(collectDatas!!)
                        }
                        200 -> {
                            collectDataAdapterApproved.submitListClear(collectDatas!!)
                        }
                        300 -> {
                            collectDataAdapterReject.submitListClear(collectDatas!!)
                        }
                    }
                }
            }
        } else {
            collectDataAdapter.submitListClear(collectDataPending)
            collectDataAdapterApproved.submitListClear(collectDataApproved)
            collectDataAdapterReject.submitListClear(collectDataReject)
        }

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
                        status = 100
                        onPending()
                    }

                    STATUS_APPROVED -> {
                        status = 200
                        onApprove()
                    }

                    STATUS_REJECT -> {
                        status = 300
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
                    collectDataReject = vocabularies!!
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
                    collectDataApproved = vocabularies!!

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
                            collectDataPending = vocabularies
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