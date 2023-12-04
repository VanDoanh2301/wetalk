package com.example.wetalk.ui.fragment

import android.os.Bundle
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

        binding.loginProgressBar.visibility = View.GONE
        binding.loginCountrycode.registerCarrierNumberEditText(binding.loginMobileNumber)
        initViewModel()
        initAge()
        initRegister();
    }

    private fun initViewModel() {
        lifecycleScope.launchWhenStarted {
            viewModel.registerResponseStateFlow.collect{

            }
        }
    }

    private fun initRegister() {
        binding.sendOtpBtn.setOnClickListener {
            binding.apply {
                if(!loginCountrycode.isValidFullNumber){
                    loginMobileNumber.error = "Phone number not valid";
                    return@apply;
                }
                if (edtPassword.text.toString() != edtConfirm.text.toString()) {
                    edtConfirm.error = "Passwords are not the same";
                } else {
                    var user = User(edtName.text.toString(),
                        edtEmail.text.toString(),
                        edtPassword.text.toString(),
                        loginCountrycode.fullNumberWithPlus,
                        edtAddress.text.toString(),
                        "USER",
                        age!!,
                        edtGender.getText().toString()
                    );
                    viewModel.generateOtp(user)
                    val bundle = bundleOf("email" to edtEmail.text.toString())
                    findNavController().navigate(R.id.action_talkRegisterFragment_to_talkOtpFragment, bundle)
                }
            }

        }

    }
    /** Parse Age User */
    private fun initAge() {
        binding.edtDate.setOnClickListener(View.OnClickListener {

            WeTalkApp.showDatePicker(activity as MainActivity, object  : Task<Long> {
                override fun callback(result: Long) {
                    val format =
                        SimpleDateFormat("EE, dd MMM yyyy", Locale.getDefault())
                    val dateString: String = format.format(result)
                    binding.edtDate.text = dateString
                    try {
                        val date: Date = format.parse(dateString)
                        val dob: Calendar = Calendar.getInstance()
                        dob.time = date
                        val today: Calendar = Calendar.getInstance()
                        age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
                        if (today.get(Calendar.MONTH) < dob.get(Calendar.MONTH)) {
                            age = age!! - 1
                        } else if (today.get(Calendar.MONTH) === dob.get(Calendar.MONTH)
                            && today.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH)
                        ) {
                            age = age!! - 1
                        }
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                }

            })
        })
    }
}