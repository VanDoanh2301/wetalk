package com.example.wetalk.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Adapter
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.addTextChangedListener

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wetalk.R
import com.example.wetalk.data.model.objectmodel.UserInforRequest
import com.example.wetalk.data.model.objectmodel.UserQueryRequest
import com.example.wetalk.databinding.FragmentTalkSearchUserBinding
import com.example.wetalk.ui.activity.MainActivity
import com.example.wetalk.ui.adapter.UserSearchAdapter
import com.example.wetalk.ui.viewmodels.TalkSearchUserViewModel
import com.example.wetalk.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.lang.NullPointerException

/**
 * A simple [Fragment] subclass.
 * Use the [TalkSearchUserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class TalkSearchUserFragment : Fragment() {
    private var _binding: FragmentTalkSearchUserBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TalkSearchUserViewModel by viewModels()
    private lateinit var adapter: UserSearchAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTalkSearchUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSearch()


    }


    private fun initSearch() {
        binding.rcvUser.layoutManager = LinearLayoutManager(requireContext())
        // Configuring focus for the search user EditText and displaying the keyboard
        binding.seachUsernameInput.requestFocus() // Set focus on the search EditText
        showKeyboard(binding.seachUsernameInput) // Show the keyboard for the search EditText

        // Handling the back button click
        binding.btBack.setOnClickListener {
            // Calling MainActivity's onBackPressed function
            requireActivity().onBackPressed()
        }

        binding.seachUsernameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("TextWatcher", "beforeTextChanged: $p0")
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("TextWatcher", "onTextChanged: $p0")
                val userStr = p0.toString()
                val userQueryRequest = UserQueryRequest(1, 10, userStr, true, "")
                lifecycleScope.launchWhenStarted {
                    viewModel.searchUser(userQueryRequest)
                    viewModel.getInforUser.collect {
                        when (it) {
                            is Resource.Loading -> {
                                Log.d("TextWatcher", "Loading")
                            }

                            is Resource.Success -> {
                                try {
                                    val users = it.data!!.data
                                    adapter = UserSearchAdapter(requireContext(), users)
                                    binding.rcvUser.adapter = adapter
                                    adapter.notifyDataSetChanged()
                                    initAddFriend()
                                } catch (e: NullPointerException) {
                                    throw NullPointerException("Users is null")
                                }

                            }

                            is Resource.Error -> {
                                Log.d("UserRegisterDTO", it.message.toString())
                            }
                        }
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                Log.d("TextWatcher", "afterTextChanged: ${p0.toString()}")
            }
        })
    }

    private  fun initAddFriend() {
     adapter.setOnItemClickAddFriend(object :  UserSearchAdapter.OnItemClick{
         override fun onItem(position: Int, user: UserInforRequest) {
             user.id?.let { viewModel.addFriend(it) }
         }

     })
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            TalkSearchUserFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    private fun showKeyboard(editText: AppCompatEditText) {
        val inputMethodManager =
            (activity as MainActivity).getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            (activity as MainActivity).getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }


}