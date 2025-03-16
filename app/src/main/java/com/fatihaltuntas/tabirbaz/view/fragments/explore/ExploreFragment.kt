package com.fatihaltuntas.tabirbaz.view.fragments.explore

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.fatihaltuntas.tabirbaz.databinding.FragmentExploreBinding
import com.fatihaltuntas.tabirbaz.view.adapters.DreamAdapter
import com.fatihaltuntas.tabirbaz.view.adapters.ExploreCategoryAdapter
import com.fatihaltuntas.tabirbaz.viewmodel.ExploreViewModel

class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: ExploreViewModel by viewModels()
    private lateinit var categoryAdapter: ExploreCategoryAdapter
    private lateinit var popularDreamsAdapter: DreamAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupAdapters()
        setupClickListeners()
        setupObservers()
        
        // Kategori ve popüler rüyaları yükle
        viewModel.loadCategories()
        viewModel.loadPopularDreams()
    }
    
    private fun setupAdapters() {
        // Kategori adaptörü
        categoryAdapter = ExploreCategoryAdapter { category ->
            // Kategori tıklandığında kategori detay sayfasına git
            val action = ExploreFragmentDirections.actionExploreFragmentToCategoryDreamsFragment(
                categoryId = category.id,
                categoryName = category.name
            )
            findNavController().navigate(action)
        }
        
        binding.rvCategories.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = categoryAdapter
        }
        
        // Popüler rüyalar adaptörü
        popularDreamsAdapter = DreamAdapter { dream ->
            // Rüya tıklandığında rüya detay sayfasına git
            val action = ExploreFragmentDirections.actionExploreFragmentToDreamDetailFragment(dreamId = dream.id)
            findNavController().navigate(action)
        }
        
        binding.rvPopularDreams.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = popularDreamsAdapter
        }
    }
    
    private fun setupClickListeners() {
        // Arama kutusuna tıklandığında arama sayfasına git
        binding.cardSearch.setOnClickListener {
            val action = ExploreFragmentDirections.actionExploreFragmentToSearchResultsFragment(query = "")
            findNavController().navigate(action)
        }
    }
    
    private fun setupObservers() {
        // Kategorileri izle
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            categoryAdapter.submitList(categories)
        }
        
        // Popüler rüyaları izle
        viewModel.popularDreams.observe(viewLifecycleOwner) { dreams ->
            popularDreamsAdapter.submitList(dreams)
            binding.tvEmptyState.visibility = if (dreams.isEmpty()) View.VISIBLE else View.GONE
        }
        
        // Yükleme durumunu izle
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        // Hata durumunu izle
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}