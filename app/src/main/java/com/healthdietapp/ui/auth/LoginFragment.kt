package com.healthdietapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.healthdietapp.R
import com.healthdietapp.databinding.FragmentLoginBinding
import com.healthdietapp.utils.NetworkResult
import com.healthdietapp.utils.TokenManager
import com.healthdietapp.utils.hide
import com.healthdietapp.utils.show
import com.healthdietapp.utils.showErrorSnackbar
import com.healthdietapp.utils.showSnackbar
import com.healthdietapp.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty()) {
                binding.tilEmail.error = "Email is required"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.tilPassword.error = "Password is required"
                return@setOnClickListener
            }

            binding.tilEmail.error = null
            binding.tilPassword.error = null
            authViewModel.login(email, password)
        }

        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }

        observeLoginState()
    }

    private fun observeLoginState() {
        viewLifecycleOwner.lifecycleScope.launch {
            authViewModel.loginState.collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        binding.progressIndicator.show()
                        binding.btnLogin.isEnabled = false
                    }
                    is NetworkResult.Success -> {
                        binding.progressIndicator.hide()
                        binding.btnLogin.isEnabled = true
                        tokenManager.saveToken(result.data.access_token)
                        val navOptions = NavOptions.Builder()
                            .setPopUpTo(R.id.loginFragment, true)
                            .build()
                        findNavController().navigate(R.id.action_login_to_profile, null, navOptions)
                    }
                    is NetworkResult.Error -> {
                        binding.progressIndicator.hide()
                        binding.btnLogin.isEnabled = true
                        if (result.code == 401) {
                            tokenManager.clearToken()
                        }
                        binding.root.showErrorSnackbar(result.message)
                    }
                    null -> {
                        binding.progressIndicator.hide()
                        binding.btnLogin.isEnabled = true
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
