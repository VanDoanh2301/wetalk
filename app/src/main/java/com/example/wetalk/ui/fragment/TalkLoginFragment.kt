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
import com.example.wetalk.data.model.postmodel.LoginDTO
import com.example.wetalk.databinding.FragmentTalkLoginBinding
import com.example.wetalk.ui.viewmodels.LoginViewModel
import com.example.wetalk.util.Resource
import com.example.wetalk.util.SharedPreferencesUtils
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment for user login.
 */
@AndroidEntryPoint
class TalkLoginFragment : Fragment() {
    // View model initialization using Hilt's viewModels delegate
    private val viewModel: LoginViewModel by viewModels()
    // Binding object for this fragment
    private var _binding: FragmentTalkLoginBinding? = null
    private val binding get() = _binding!!

    /**
     * Inflating the layout for this fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTalkLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Setting up views and actions when the view is created.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Handling back press to finish the activity
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }
        // Setting click listener for sign in button
        binding.btnSignIn.setOnClickListener {
            onLogin()
        }
        // Initializing views and setting up click listeners
        initViews()
        // Observing login response
        observeLoginResponse()
    }

    /**
     * Initializes views and sets up their actions.
     */
    private fun initViews() {
        binding.apply {
            // Click listener for sign up button
            btnSignup.setOnClickListener {
                findNavController().navigate(R.id.action_talkLoginFragment_to_talkRegisterFragment)
            }
            // Click listener for button
            button.setOnClickListener {
                val bundle = bundleOf("isUser" to false)
                findNavController().navigate(R.id.action_talkLoginFragment_to_talkHomeFragment, bundle)
            }
        }
    }

    /**
     * Observes login response and updates UI accordingly.
     */
    private fun observeLoginResponse() {
        lifecycleScope.launchWhenStarted {
            viewModel.loginResponseStateFlow.collect { resource ->
                binding.loginProgressBar.visibility = View.GONE
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.accessToken?.let { SharedPreferencesUtils.saveToken(it) }
                        binding.loginProgressBar.visibility = View.VISIBLE
                        val bundle = bundleOf("isUser" to true)
                        findNavController().navigate(R.id.action_talkLoginFragment_to_talkHomeFragment, bundle)
                        findNavController().popBackStack(R.id.talkHomeFragment, false)
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {
                        binding.loginProgressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    /**
     * Initiates login process.
     */
    private fun onLogin() {
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            val userLoginDTO = LoginDTO(email, password)
            viewModel.login(userLoginDTO)
        } else {
            Toast.makeText(requireContext(), "Email Hoặc Mật Khẩu Không Đúng", Toast.LENGTH_SHORT).show()
        }
        binding.loginProgressBar.visibility = View.VISIBLE
    }

    /**
     * Cleans up the binding object when the fragment's view is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
