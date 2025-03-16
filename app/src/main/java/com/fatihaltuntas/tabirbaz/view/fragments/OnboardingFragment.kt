package com.fatihaltuntas.tabirbaz.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.fatihaltuntas.tabirbaz.R
import com.fatihaltuntas.tabirbaz.TabirbazApplication
import com.fatihaltuntas.tabirbaz.view.adapters.OnboardingPagerAdapter
import com.fatihaltuntas.tabirbaz.databinding.FragmentOnboardingBinding
import com.fatihaltuntas.tabirbaz.util.SessionManager
import com.fatihaltuntas.tabirbaz.viewmodel.OnboardingViewModel

class OnboardingFragment : Fragment() {
    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: OnboardingViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = (requireActivity().application as TabirbazApplication).sessionManager
        setupViewPager()
        setupObservers()
        setupClickListeners()
    }

    private fun setupViewPager() {
        val adapter = OnboardingPagerAdapter()
        binding.viewPager.adapter = adapter
        binding.dotsIndicator.setViewPager2(binding.viewPager)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewModel.setCurrentPage(position, adapter.itemCount)
            }
        })
    }

    private fun setupObservers() {
        viewModel.isLastPage.observe(viewLifecycleOwner) { isLastPage ->
            binding.btnNext.text = if (isLastPage) getString(R.string.finish) else getString(R.string.next)
            if (isLastPage && binding.btnNext.text == getString(R.string.finish)) {
                binding.btnNext.setOnClickListener {
                    navigateToLogin()
                }
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.btnSkip.setOnClickListener {
            navigateToLogin()
        }
        
        binding.btnNext.setOnClickListener {
            val currentPosition = binding.viewPager.currentItem
            if (currentPosition < (binding.viewPager.adapter?.itemCount ?: 0) - 1) {
                binding.viewPager.currentItem = currentPosition + 1
            } else {
                navigateToLogin()
            }
        }
    }
    
    private fun navigateToLogin() {
        // Onboarding tamamlandı olarak işaretle
        sessionManager.setOnboardingCompleted(true)
        
        // Giriş ekranına yönlendir
        findNavController().navigate(R.id.action_onboarding_to_login)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}