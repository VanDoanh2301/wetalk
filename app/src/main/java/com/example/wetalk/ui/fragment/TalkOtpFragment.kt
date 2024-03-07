package com.example.wetalk.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.wetalk.R
import com.example.wetalk.data.model.postmodel.UserOtpDTO
import com.example.wetalk.databinding.FragmentTalkOtpBinding
import com.example.wetalk.ui.viewmodels.OtpViewModel
import com.example.wetalk.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Timer
import java.util.TimerTask


/**
 * A simple [Fragment] subclass.
 * Use the [TalkOtpFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class TalkOtpFragment : Fragment() {

    private var _binding: FragmentTalkOtpBinding? = null
    private val binding get() = _binding!!
    private val viewModel: OtpViewModel by viewModels()
    private lateinit var email: String
    private var timeoutSeconds = 60L
    private lateinit var timer: Timer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTalkOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        email = arguments?.getString("email").toString()
        lifecycleScope.launch {
            startResendTimer()
        }
        initViewModel();
        initOtp();
    }

    /** Input OTP */
    private fun initOtp() {
        binding.loginNextBtn.setOnClickListener {
            if (binding.loginOtp.text.toString().isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Kiểm tra email của bạn để lấy mã OTP",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                var userOtpDTO =
                    UserOtpDTO(email, Integer.parseInt(binding.loginOtp.text.toString()))
                viewModel.validateOtp(userOtpDTO)
            }
        }
    }

    /** Create viewModel */
    private fun initViewModel() {
        lifecycleScope.launchWhenStarted {
            viewModel.otpResponseStateFlow.collect {
                when (it) {
                    is Resource.Success -> {
                        Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_talkOtpFragment_to_talkDoneFragment2)
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        binding.loginProgressBar.visibility = View.GONE
                    }

                    is Resource.Loading -> {
                        binding.loginProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        }
    }


    private suspend fun startResendTimer() {
        binding?.resendOtpTextview?.isEnabled = false

        try {
            var timeoutSeconds = 60L
            val timer = Timer()
            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    timeoutSeconds--
                    CoroutineScope(Dispatchers.Default).launch {
                        withContext(Dispatchers.Main) {
                            if (binding != null && isAdded) {
                                binding.resendOtpTextview.text =
                                    "Resend OTP in $timeoutSeconds seconds"
                            }
                        }
                    }
                    if (timeoutSeconds <= 0) {
                        timer.cancel()
                        CoroutineScope(Dispatchers.Main).launch {
                            binding?.loginProgressBar?.visibility = View.GONE
                        }
                    }
                }
            }, 0, 1000)
        } catch (e: Exception) {
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onPause() {
        super.onPause()
        timer.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }
}