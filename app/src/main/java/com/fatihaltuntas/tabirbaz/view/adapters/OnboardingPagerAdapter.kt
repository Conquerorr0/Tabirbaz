package com.fatihaltuntas.tabirbaz.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fatihaltuntas.tabirbaz.R
import com.fatihaltuntas.tabirbaz.databinding.ItemOnboardingPageBinding
import com.fatihaltuntas.tabirbaz.model.OnboardingPage

class OnboardingPagerAdapter : RecyclerView.Adapter<OnboardingPagerAdapter.OnboardingViewHolder>() {

    private val pages = listOf(
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val binding = ItemOnboardingPageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OnboardingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(pages[position])
    }

    override fun getItemCount(): Int = pages.size

    inner class OnboardingViewHolder(
        private val binding: ItemOnboardingPageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(page: OnboardingPage) {
            binding.apply {
                ivOnboarding.setImageResource(page.imageRes)
                tvTitle.setText(page.titleRes)
                tvDescription.setText(page.descriptionRes)
            }
        }
    }
} 