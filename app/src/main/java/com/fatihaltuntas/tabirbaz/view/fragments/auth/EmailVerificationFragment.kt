package com.fatihaltuntas.tabirbaz.view.fragments.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fatihaltuntas.tabirbaz.R
import com.fatihaltuntas.tabirbaz.databinding.FragmentEmailVerificationBinding
import com.fatihaltuntas.tabirbaz.viewmodel.AuthViewModel

class EmailVerificationFragment : Fragment() {
    private var _binding: FragmentEmailVerificationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

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
        setupObservers()
        setupClickListeners()
        checkEmailVerification()
    }

    private fun setupObservers() {
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                if (it.isEmailVerified) {
                    findNavController().navigate(R.id.action_emailVerification_to_profileCompletion)
                }
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingAnimation.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnResendEmail.isEnabled = !isLoading
            binding.btnContinue.isEnabled = !isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnResendEmail.setOnClickListener {
            viewModel.currentUser.value?.let { user ->
                viewModel.sendEmailVerification(user)
                Toast.makeText(requireContext(), getString(R.string.email_verification_sent), Toast.LENGTH_LONG).show()
            }
        }

        binding.btnContinue.setOnClickListener {
            checkEmailVerification()
        }
    }

    private fun checkEmailVerification() {
        viewModel.currentUser.value?.let { user ->
            user.reload().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (user.isEmailVerified) {
                        findNavController().navigate(R.id.action_emailVerification_to_profileCompletion)
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.email_verification_required), Toast.LENGTH_LONG).show()
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