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
import com.fatihaltuntas.tabirbaz.databinding.FragmentProfileCompletionBinding
import com.fatihaltuntas.tabirbaz.model.UserProfile
import com.fatihaltuntas.tabirbaz.view.activities.MainActivity
import com.fatihaltuntas.tabirbaz.viewmodel.AuthViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

class ProfileCompletionFragment : Fragment() {
    private var _binding: FragmentProfileCompletionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()
    private var selectedDate: Date? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileCompletionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingAnimation.isVisible = isLoading
            binding.btnComplete.isEnabled = !isLoading
            binding.btnComplete.isVisible = !isLoading
            binding.etName.isEnabled = !isLoading
            binding.btnBirthDate.isEnabled = !isLoading
            binding.rgGender.isEnabled = !isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }

        viewModel.profileUpdateSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                startMainActivity()
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnBirthDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnComplete.setOnClickListener {
            val name = binding.etName.text.toString()
            val gender = when (binding.rgGender.checkedRadioButtonId) {
                R.id.rbMale -> "male"
                R.id.rbFemale -> "female"
                else -> "other"
            }

            if (validateInputs(name)) {
                val userProfile = UserProfile(
                    name = name,
                    birthDate = selectedDate,
                    gender = gender
                )
                viewModel.updateUserProfile(userProfile)
            }
        }
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.select_birth_date))
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            selectedDate = Date(selection)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding.btnBirthDate.text = dateFormat.format(selectedDate!!)
        }

        datePicker.show(parentFragmentManager, "DATE_PICKER")
    }

    private fun validateInputs(name: String): Boolean {
        if (name.isBlank()) {
            Toast.makeText(requireContext(), getString(R.string.name_required), Toast.LENGTH_SHORT).show()
            return false
        }
        if (selectedDate == null) {
            Toast.makeText(requireContext(), getString(R.string.birth_date_required), Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.rgGender.checkedRadioButtonId == -1) {
            Toast.makeText(requireContext(), getString(R.string.gender_required), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
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