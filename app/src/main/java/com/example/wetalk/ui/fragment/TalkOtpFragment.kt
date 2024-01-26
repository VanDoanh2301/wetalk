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
import java.util.Timer
import java.util.TimerTask


/**
 * A simple [Fragment] subclass.
 * Use the [TalkOtpFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class TalkOtpFragment : Fragment() {

    private var _binding: FragmentTalkOtpBinding? =null
    private val binding get() =  _binding!!
    private val viewModel: OtpViewModel by viewModels()
    private lateinit var email:String
    private var timeoutSeconds = 60L
    private  lateinit var  timer:Timer

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
        startResendTimer()
        initViewModel();
        initOtp();
    }

    /** Input OTP */
    private fun initOtp() {
        binding.loginNextBtn.setOnClickListener {
            if (binding.loginOtp.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), "Kiểm tra email của bạn để lấy mã OTP", Toast.LENGTH_SHORT).show()
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
            viewModel.otpResponseStateFlow.collect{
                when(it) {
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


    /** Create Timer */
    private fun startResendTimer() {
        binding.resendOtpTextview.isEnabled = false
        try{
             timer = Timer()
            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    timeoutSeconds--
                    if (binding != null) {
                        activity!!.runOnUiThread {
                            binding.resendOtpTextview.text = "Resend OTP in $timeoutSeconds seconds"
                        }
                    }
                    if (timeoutSeconds <= 0) {
                        timeoutSeconds = 60L
                        binding.loginProgressBar.visibility = View.GONE
                        timer.cancel()
                    }
                }
            }, 0, 1000)
        } catch (e : Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
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