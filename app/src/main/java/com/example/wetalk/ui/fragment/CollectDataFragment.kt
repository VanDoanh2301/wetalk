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
import com.example.wetalk.databinding.FragmentCollectDataBinding
import com.example.wetalk.ui.adapter.CollectDataAdapter
import com.example.wetalk.ui.viewmodels.AdminViewModel
import com.example.wetalk.util.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CollectDataFragment : Fragment() {
    private var _binding: FragmentCollectDataBinding ?= null
    private val binding get() = _binding!!
    private val adminViewModel : AdminViewModel by viewModels()
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
        _binding = FragmentCollectDataBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = CollectDataAdapter(requireContext())
        init()
        initData()
        onView()
    }
    private fun onView() {
        binding.btnMenu.setOnClickListener {
            requireActivity().onBackPressed()
        }
        adapter.setOnClickItem(object : CollectDataAdapter.OnClickItem {
            override fun onClickItem(position: Int, collectData: CollectData) {
               onItem(position, collectData)
            }

        })
    }

    private fun onItem(position: Int, collectData: CollectData) {
        val popupMenu = PopupMenu(requireContext(), binding.btnMenu)
        popupMenu.menuInflater.inflate(R.menu.menu_collect_data, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_item_1 -> {
                    onApproved(collectData.dataCollectionId)
                    true
                }
                R.id.menu_item_2 -> {

                    true
                }
                R.id.menu_item_3 -> {
                    onDelete(position,collectData.dataCollectionId)
                    true
                }
                else -> false
            }
        }

        // Hiển thị Popup Menu
        popupMenu.show()
    }

    private fun onDelete(position: Int,dataCollectionId: Int) {
           adminViewModel.deleteCollectData(dataCollectionId).observe(viewLifecycleOwner) {
               if (it.isSuccessful) {
                   requireContext().showToast("Xóa dữ liệu thành công")
                   adapter.removeItem(position)
               }
           }
    }

    private fun onApproved(dataCollectionId: Int) {
        adminViewModel.approvedCollectData(dataCollectionId).observe(viewLifecycleOwner) {
            if (it.isSuccessful) {
                requireContext().showToast("Chấp nhận video thành công")
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
    }
}