package com.fatihaltuntas.tabirbaz.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.fatihaltuntas.tabirbaz.databinding.FragmentOnboardingBinding

class OnboardingFragment : Fragment() {
    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

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
        setupViewPager()
        setupClickListeners()
    }

    private fun setupViewPager() {
        // ViewPager2 adapter'ı burada ayarlanacak
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateNextButtonText(position)
            }
        })
    }

    private fun setupClickListeners() {
        binding.btnNext.setOnClickListener {
            val currentItem = binding.viewPager.currentItem
            if (currentItem < 2) { // Son sayfada değilse
                binding.viewPager.currentItem = currentItem + 1
            } else { // Son sayfadaysa
                // TODO: Kayıt ekranına geçiş yapılacak
            }
        }
    }

    private fun updateNextButtonText(position: Int) {
        binding.btnNext.text = if (position == 2) {
            getString(com.fatihaltuntas.tabirbaz.R.string.get_started)
        } else {
            getString(com.fatihaltuntas.tabirbaz.R.string.next)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}