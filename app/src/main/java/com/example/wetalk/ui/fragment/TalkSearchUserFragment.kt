package com.example.wetalk.ui.fragment


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wetalk.R
import com.example.wetalk.data.model.objectmodel.UserInforRequest
import com.example.wetalk.data.model.objectmodel.UserQueryRequest
import com.example.wetalk.databinding.FragmentTalkProfileHomeBinding
import com.example.wetalk.databinding.FragmentTalkSearchUserBinding
import com.example.wetalk.ui.activity.MainActivity
import com.example.wetalk.ui.adapter.UserSearchAdapter
import com.example.wetalk.ui.viewmodels.SearchUserViewModel
import com.example.wetalk.util.Resource
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 * Use the [TalkSearchUserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class TalkSearchUserFragment : Fragment() {
    private var _binding: FragmentTalkSearchUserBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchUserViewModel by viewModels()
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
                            is Resource.Loading -> { Log.d("TextWatcher", "Loading") }
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
                            is Resource.Error -> { Log.d("UserRegisterDTO", it.message.toString()) }
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
             user.id?.let {
                 //Call fun add friend by viewModel
                 viewModel.addFriend(it)
                 lifecycleScope.launchWhenResumed {
                     viewModel.getAddFriend.collect {
                         when(it) {
                             //Success to notify
                             is Resource.Success -> {Toast.makeText(requireContext(), "Thêm bạn bè thành công", Toast.LENGTH_LONG).show()}
                             //Error to notify
                             is  Resource.Error -> {Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_LONG).show()}
                         }
                     }
                 }
             }
         }

         //View Infor User
         override fun onUser(position: Int, user: UserInforRequest) {
             user.id?.let {
                   viewModel.searchUserByID(it)
                 lifecycleScope.launchWhenResumed {
                     viewModel.getUserID.collect {
                         when(it) {
                             is Resource.Loading -> {}
                             //Success to notify
                             is Resource.Success -> {initDialogInformUser(it.data!!.data)}
                             //Error to notify
                             is  Resource.Error -> {Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_LONG).show()}
                         }
                     }
                 }

             }
         }
     })
    }

    private fun initDialogInformUser(data: ArrayList<UserInforRequest>) {
        val binding: FragmentTalkProfileHomeBinding = FragmentTalkProfileHomeBinding.inflate(layoutInflater)

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setView(binding.root)
        builder.setTitle("Custom Dialog")
        builder.setView(binding.root)

        builder.setPositiveButton("OK") { dialog, which ->
            // Xử lý sự kiện khi nhấn nút OK trong dialog
        }

        builder.setNegativeButton("Cancel") { dialog, which ->
            // Xử lý sự kiện khi nhấn nút Cancel trong dialog
            dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
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