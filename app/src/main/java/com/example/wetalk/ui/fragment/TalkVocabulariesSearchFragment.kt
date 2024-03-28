package com.example.wetalk.ui.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager

import com.example.wetalk.data.model.objectmodel.VocabularyRequest
import com.example.wetalk.data.model.postmodel.VocabulariesDTO
import com.example.wetalk.databinding.FragmentTalkSearchVocabulariesBinding
import com.example.wetalk.ui.activity.MainActivity
import com.example.wetalk.ui.adapter.VocabulariesAdapter
import com.example.wetalk.ui.viewmodels.VocabulariesSearchViewModel
import com.example.wetalk.util.Resource
import dagger.hilt.android.AndroidEntryPoint

/**
 * A simple [Fragment] subclass.
 * Use the [TalkVocabulariesSearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class TalkVocabulariesSearchFragment : Fragment() {
    private var _binding: FragmentTalkSearchVocabulariesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: VocabulariesSearchViewModel by viewModels()
    private var resultlist: ArrayList<VocabularyRequest> = ArrayList()
    private var isFocus = false
    private val TAG = "TalkVocabulariesSearchFragment"
    private lateinit var vocabulariesAdapter: VocabulariesAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTalkSearchVocabulariesBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun setFocus(isFocus: Boolean): TalkVocabulariesSearchFragment {
        this.isFocus = isFocus
        return this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vocabulariesAdapter = VocabulariesAdapter(requireContext())
        binding.edtSearch.requestFocus()
        showKeyboard(binding.edtSearch)
        binding.apply {
            val linearLayoutManager = StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL
            )
            rcvView.layoutManager = linearLayoutManager
        }
        if (isFocus) {
            showKeyboard(binding.edtSearch)
        } else {
            hideKeyboard()
        }
        init()
        onClickView()
    }

    private fun onClickView() {
        vocabulariesAdapter.setOnItemClick(object  : VocabulariesAdapter.OnItemClick{
            override fun onItem(position: Int, topicRequest: VocabularyRequest) {
                BaseDialogFragment.add(
                    (activity as MainActivity), PlayVideoFragment.newInstance()
                        .setVideoPath(topicRequest.videoLocation!!,topicRequest.imageLocation!!, if (topicRequest.videoLocation.equals("")) 1 else 2)
                )
            }

        })
    }

    private fun init() {
        binding.btBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.apply {
            edtSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    val dataStr = p0.toString()
                    val vocabulariesDTO = VocabulariesDTO(1, 10, dataStr, true, "", 0)
                    lifecycleScope.launchWhenStarted {
                        viewModel.searchVocabulariesData(vocabulariesDTO)
                        viewModel.searchVocabularies.observe(viewLifecycleOwner) {
                            when (it) {
                                is Resource.Loading -> {}
                                is Resource.Success -> {
                                    try {
                                        var vocabularies = it.data!!.data
                                        resultlist.addAll(vocabularies)
                                        rcvView.adapter = vocabulariesAdapter
                                        vocabulariesAdapter.submitList(vocabularies)
                                    } catch (e: NullPointerException) {
                                        throw NullPointerException("Users is null")
                                    }
                                }

                                is Resource.Error -> {
                                    Log.d(TAG, it.message.toString())
                                }
                            }
                        }
                    }


                }

                override fun afterTextChanged(p0: Editable?) {

                }

            })
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    private fun showKeyboard(editText: AppCompatEditText) {
        val inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    companion object {
        @JvmStatic
        fun newInstance() = TalkVocabulariesSearchFragment().apply {
            arguments = Bundle().apply {

            }
        }
    }
}