package com.example.wetalk.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.wetalk.R
import com.example.wetalk.WeTalkApp
import com.example.wetalk.data.model.objectmodel.User
import com.example.wetalk.databinding.FragmentTalkRegisterBinding
import com.example.wetalk.ui.activity.MainActivity
import com.example.wetalk.ui.viewmodels.RegisterViewModel
import com.example.wetalk.util.Resource
import com.example.wetalk.util.Task
import dagger.hilt.android.AndroidEntryPoint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


/**
 * A simple [Fragment] subclass.
 * Use the [TalkRegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class TalkRegisterFragment : Fragment() {

    private var _binding:FragmentTalkRegisterBinding?=null
    private val binding get() =  _binding!!
    private val viewModel:RegisterViewModel by viewModels()
    private var age:Int?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTalkRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        initRegister();
    }

    private fun initViewModel() {
        lifecycleScope.launchWhenStarted {
            viewModel.registerResponseStateFlow.collect{
                    when (it) {
                        is Resource.Loading -> {
                        }
                        is Resource.Success -> {

                        }
                        is Resource.Error -> {

                        }
                    }
            }
        }
    }

    private fun initRegister() {
        binding.sendOtpBtn.setOnClickListener {
            binding.apply {
                if (edtPassword.text.toString() != edtConfirm.text.toString()) {
                    edtConfirm.error = "Passwords are not the same";
                } else {
                    var user = User(
                        edtName.text.toString(),
                        edtEmail.text.toString(),
                        edtPassword.text.toString(),
                        "USER"
                    );
                    viewModel.generateOtp(user)

                    val bundle = bundleOf("email" to edtEmail.text.toString())
                    findNavController().navigate(R.id.action_talkRegisterFragment_to_talkOtpFragment, bundle)
                }
            }

        }

    }
}