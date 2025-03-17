package com.fatihaltuntas.tabirbaz.view.fragments.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fatihaltuntas.tabirbaz.R
import com.fatihaltuntas.tabirbaz.databinding.FragmentEmailVerificationBinding
import com.fatihaltuntas.tabirbaz.view.activities.MainActivity
import com.fatihaltuntas.tabirbaz.viewmodel.AuthViewModel
import com.fatihaltuntas.tabirbaz.viewmodel.ViewModelFactory

class EmailVerificationFragment : Fragment() {
    private var _binding: FragmentEmailVerificationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels {
        ViewModelFactory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmailVerificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        setupObservers()
    }

    private fun setupClickListeners() {
        binding.btnResendEmail.setOnClickListener {
            viewModel.resendVerificationEmail()
        }
        
        binding.btnContinue.setOnClickListener {
            // Kullanıcının doğrulamasını yeniden kontrol et
            viewModel.currentUser.value?.reload()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = viewModel.currentUser.value
                    if (user != null && user.isEmailVerified) {
                        startMainActivity()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.email_verification_required),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun setupObservers() {
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                if (user.isEmailVerified) {
                    startMainActivity()
                }
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingAnimation.isVisible = isLoading
            binding.btnResendEmail.isEnabled = !isLoading
            binding.btnContinue.isEnabled = !isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }
    }

    private fun startMainActivity() {
        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 