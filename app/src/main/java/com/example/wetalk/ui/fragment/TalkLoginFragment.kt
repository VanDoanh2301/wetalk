package com.example.wetalk.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.wetalk.R
import com.example.wetalk.data.model.postmodel.LoginPost
import com.example.wetalk.databinding.FragmentTalkLoginBinding
import com.example.wetalk.ui.viewmodels.LoginViewModel
import com.example.wetalk.util.Resource
import com.example.wetalk.util.SharedPreferencesUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.observeOn

/**
 * A simple [Fragment] subclass.
 * Use the [TalkLoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class TalkLoginFragment : Fragment() {
    private val viewModel:LoginViewModel by viewModels()
    private var _binding: FragmentTalkLoginBinding? = null
    private val binding get() =  _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTalkLoginBinding.inflate(inflater, container,false)
        val view = binding.root
        return  view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }
        binding.btnSignIn.setOnClickListener {
            onLogin()
        }
        initLogin();
        innitActionView();

    }

    /** OnClick View */
    private fun innitActionView() {
        binding.btnSignup.setOnClickListener {
            findNavController().navigate(R.id.action_talkLoginFragment_to_talkRegisterFragment)
        }
        binding.button.setOnClickListener {
            val bundle = bundleOf(
                "isUser" to false
            )
            findNavController().popBackStack(R.id.talkHomeFragment, false)
            findNavController().navigate(R.id.action_talkLoginFragment_to_talkHomeFragment, bundle)

        }
    }

    /** Custom login */
    private fun initLogin() {
        lifecycleScope.launchWhenStarted {
            viewModel.loginResponseStateFlow.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.loginProgressBar.visibility = View.GONE
                    }
                    is Resource.Success -> {
                        SharedPreferencesUtils.setString("isLogin", it.data!!.accessToken);
                        binding.loginProgressBar.visibility = View.VISIBLE
                        val bundle = bundleOf(
                            "isUser" to true
                        )
                        findNavController().navigate(R.id.action_talkLoginFragment_to_talkHomeFragment, bundle)
                        findNavController().popBackStack(R.id.talkHomeFragment, false)

                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        binding.loginProgressBar.visibility = View.GONE
                    }
                }
            }
        }
    }
    private fun onLogin() {
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            val userLoginPost = LoginPost(email, password)
            viewModel.login(userLoginPost)
        } else {
            Toast.makeText(requireContext(), "Email Hoặc Mật Khẩu Không Đúng", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onDestroy() {
        super.onDestroy()

    }
}