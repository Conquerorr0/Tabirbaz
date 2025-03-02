package com.fatihaltuntas.tabirbaz.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.fatihaltuntas.tabirbaz.R
import com.fatihaltuntas.tabirbaz.view.adapters.OnboardingPagerAdapter
import com.fatihaltuntas.tabirbaz.databinding.FragmentOnboardingBinding
import com.fatihaltuntas.tabirbaz.model.OnboardingPage

class OnboardingFragment : Fragment() {
    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!
    private lateinit var pagerAdapter: OnboardingPagerAdapter

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
        val pages = listOf(
            OnboardingPage(
                R.drawable.ic_onboarding_1,
                R.string.onboarding_title_1,
                R.string.onboarding_desc_1
            ),
            OnboardingPage(
                R.drawable.ic_onboarding_2,
                R.string.onboarding_title_2,
                R.string.onboarding_desc_2
            ),
            OnboardingPage(
                R.drawable.ic_onboarding_3,
                R.string.onboarding_title_3,
                R.string.onboarding_desc_3
            )
        )

        pagerAdapter = OnboardingPagerAdapter(pages)
        binding.viewPager.adapter = pagerAdapter
        binding.dotsIndicator.attachTo(binding.viewPager)

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
            getString(R.string.get_started)
        } else {
            getString(R.string.next)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}