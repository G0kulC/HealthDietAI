package com.healthdietapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.healthdietapp.R
import com.healthdietapp.databinding.FragmentRegisterBinding
import com.healthdietapp.utils.NetworkResult
import com.healthdietapp.utils.hide
import com.healthdietapp.utils.show
import com.healthdietapp.utils.showErrorSnackbar
import com.healthdietapp.utils.showSnackbar
import com.healthdietapp.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegister.setOnClickListener {
            val fullName = binding.etFullName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            var valid = true

            if (fullName.isEmpty()) { binding.tilFullName.error = "Full name is required"; valid = false } else binding.tilFullName.error = null
            if (email.isEmpty()) { binding.tilEmail.error = "Email is required"; valid = false } else binding.tilEmail.error = null
            if (username.isEmpty()) { binding.tilUsername.error = "Username is required"; valid = false } else binding.tilUsername.error = null
            if (password.isEmpty()) { binding.tilPassword.error = "Password is required"; valid = false } else binding.tilPassword.error = null
            if (confirmPassword.isEmpty()) { binding.tilConfirmPassword.error = "Please confirm password"; valid = false }
            else if (password != confirmPassword) { binding.tilConfirmPassword.error = "Passwords do not match"; valid = false }
            else binding.tilConfirmPassword.error = null

            if (valid) {
                authViewModel.register(fullName, email, username, password)
            }
        }

        binding.tvLogin.setOnClickListener {
            findNavController().popBackStack()
        }

        observeRegisterState()
    }

    private fun observeRegisterState() {
        viewLifecycleOwner.lifecycleScope.launch {
            authViewModel.registerState.collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        binding.progressIndicator.show()
                        binding.btnRegister.isEnabled = false
                    }
                    is NetworkResult.Success -> {
                        binding.progressIndicator.hide()
                        binding.btnRegister.isEnabled = true
                        binding.root.showSnackbar("Registration successful! Please login.")
                        findNavController().navigate(R.id.action_register_to_login)
                    }
                    is NetworkResult.Error -> {
                        binding.progressIndicator.hide()
                        binding.btnRegister.isEnabled = true
                        binding.root.showErrorSnackbar(result.message)
                    }
                    null -> {
                        binding.progressIndicator.hide()
                        binding.btnRegister.isEnabled = true
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
