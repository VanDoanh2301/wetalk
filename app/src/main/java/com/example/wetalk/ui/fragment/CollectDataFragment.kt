package com.example.wetalk.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wetalk.R
import com.example.wetalk.data.model.objectmodel.CollectData
import com.example.wetalk.data.model.postmodel.DataApprovedPost
import com.example.wetalk.databinding.FragmentCollectDataBinding
import com.example.wetalk.ui.adapter.CollectDataAdapter
import com.example.wetalk.ui.viewmodels.AdminViewModel
import com.example.wetalk.util.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CollectDataFragment : Fragment() {
    private var _binding: FragmentCollectDataBinding? = null
    private val binding get() = _binding!!
    private val adminViewModel: AdminViewModel by viewModels()
    private val resultList: ArrayList<CollectData> = ArrayList()
    private lateinit var adapter: CollectDataAdapter
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
        _binding = FragmentCollectDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = CollectDataAdapter(requireContext())
        init()
        initData()
//        initSpinner()
        onView()

    }

//    private fun initSpinner() {
//        val statusList = listOf(STATUS_PENDING, STATUS_APPROVED, STATUS_REJECT)
//        val adapter = ArrayAdapter(
//            requireContext(),
//            android.R.layout.simple_spinner_dropdown_item,
//            statusList
//        )
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.btStatus.adapter = adapter
//        binding.btStatus.onItemSelectedListener = object : OnItemSelectedListener {
//            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                val selectedStatus = p0?.getItemAtPosition(p2).toString()
//                when (selectedStatus) {
//                    STATUS_PENDING -> {
//                        onPending()
//                    }
//                    STATUS_APPROVED -> {
//                        onApprove()
//                    }
//                    STATUS_REJECT -> {
//                        onReject()
//                    }
//                }
//            }
//
//            override fun onNothingSelected(p0: AdapterView<*>?) {
//
//            }
//
//        }
//    }

    private fun initRcView() {
        binding.apply {
            rcvView.visibility = View.GONE
            rcvApproved.visibility = View.GONE
            rcvReject.visibility = View.GONE
        }
    }

    private fun onView() {
        binding.btnMenu.setOnClickListener {
            requireActivity().onBackPressed()
        }
        adapter.setOnClickItem(object : CollectDataAdapter.OnClickItem {
            override fun onClickItem(position: Int, collectData: CollectData, view: View) {
                onItem(position, collectData, view)
            }

        })
    }

    private fun onItem(position: Int, collectData: CollectData, view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.menu_collect_data, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_item_1 -> {
                    onApproved(position,collectData.dataCollectionId)
                    true
                }

                R.id.menu_item_2 -> {
                    onReject(position, collectData)
                    true
                }

                R.id.menu_item_3 -> {
                    onDelete(position, collectData.dataCollectionId)
                    true
                }

                else -> false
            }
        }

        // Hiển thị Popup Menu
        popupMenu.show()
    }

    private fun onReject(position: Int, collectData: CollectData) {
        var dataApprovedPost = DataApprovedPost(collectData.dataCollectionId, "")
        adminViewModel.rejectCollectData(dataApprovedPost).observe(viewLifecycleOwner) {
            if (it.isSuccessful) {
                requireContext().showToast("Từ chối dữ liệu thành công")
                adapter.removeItem(position)
            }
        }
    }


    private fun onDelete(position: Int, dataCollectionId: Int) {
        adminViewModel.deleteCollectData(dataCollectionId).observe(viewLifecycleOwner) {
            if (it.isSuccessful) {
                requireContext().showToast("Xóa dữ liệu thành công")
                adapter.removeItem(position)
            }
        }
    }

    private fun onApproved(position: Int,dataCollectionId: Int) {
        adminViewModel.approvedCollectData(dataCollectionId).observe(viewLifecycleOwner) {
            if (it.isSuccessful) {
                requireContext().showToast("Chấp nhận video thành công")
                adapter.removeItem(position)
            }
        }
    }


    private fun initData() {
        adminViewModel.getAllDataPending().observe(viewLifecycleOwner) {
            if (it.isSuccessful) {
                val collectDatas = it.body()!!.data
                adapter.submitList(collectDatas)
            }
        }
    }

    private fun init() {
        binding.apply {
            val linearLayout = LinearLayoutManager(requireContext())
            rcvView.layoutManager = linearLayout
            adapter.submitList(resultList)
            rcvView.adapter = adapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CollectDataFragment().apply {
                arguments = Bundle().apply {

                }
            }

        const val STATUS_PENDING = "Danh sách chờ"
        const val STATUS_APPROVED = "Danh sách chấp nhận"
        const val STATUS_REJECT = "Danh sách từ chối"
    }
}