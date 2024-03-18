package com.example.wetalk.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.wetalk.R
import com.example.wetalk.data.model.postmodel.UserRegisterPost
import com.example.wetalk.databinding.FragmentTalkRegisterBinding
import com.example.wetalk.ui.viewmodels.RegisterViewModel
import com.example.wetalk.util.Resource
import dagger.hilt.android.AndroidEntryPoint


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
                            if (it.data!!.code != 404) {
                                val bundle = bundleOf("email" to binding.edtEmail.text.toString())
                                findNavController().navigate(R.id.action_talkRegisterFragment_to_talkOtpFragment, bundle)
                            } else {
                                Toast.makeText(requireContext(), "Email đã tồn tại", Toast.LENGTH_LONG).show()
                                binding.rlProgress.visibility = View.GONE
                            }
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
                    var userRegisterPost = UserRegisterPost(
                        edtName.text.toString(),
                        edtEmail.text.toString(),
                        edtPassword.text.toString(),
                        "USER"
                    );
                    viewModel.generateOtp(userRegisterPost)
                    binding.rlProgress.visibility = View.VISIBLE
                }
            }

        }

    }
}