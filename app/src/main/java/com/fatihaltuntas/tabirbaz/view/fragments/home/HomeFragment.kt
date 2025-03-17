package com.fatihaltuntas.tabirbaz.view.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fatihaltuntas.tabirbaz.R
import com.fatihaltuntas.tabirbaz.databinding.FragmentHomeBinding
import com.fatihaltuntas.tabirbaz.view.adapters.DreamCategoryAdapter
import com.fatihaltuntas.tabirbaz.view.adapters.DreamAdapter
import com.fatihaltuntas.tabirbaz.viewmodel.HomeViewModel
import com.fatihaltuntas.tabirbaz.viewmodel.ViewModelFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: HomeViewModel by viewModels { ViewModelFactory() }
    private lateinit var categoryAdapter: DreamCategoryAdapter
    private lateinit var recentDreamsAdapter: DreamAdapter
    private lateinit var popularDreamsAdapter: DreamAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupAdapters()
        setupObservers()
        setupClickListeners()
        
        // Ana sayfa verilerini yükle
        viewModel.loadHomeData()
    }
    
    private fun setupAdapters() {
        // Kategori adaptörü
        categoryAdapter = DreamCategoryAdapter { category ->
            // Kategori tıklandığında kategori detay sayfasına git
            val bundle = Bundle().apply {
                putString("categoryId", category.id)
                putString("categoryName", category.name)
            }
            findNavController().navigate(R.id.categoryDreamsFragment, bundle)
        }
        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }
        
        // Son rüyalar adaptörü
        recentDreamsAdapter = DreamAdapter { dream ->
            // Rüya tıklandığında rüya detay sayfasına git
            val bundle = Bundle().apply {
                putString("dreamId", dream.id)
            }
            findNavController().navigate(R.id.dreamDetailFragment, bundle)
        }
        binding.rvRecentDreams.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = recentDreamsAdapter
        }
        
        // Popüler rüyalar adaptörü
        popularDreamsAdapter = DreamAdapter { dream ->
            // Rüya tıklandığında rüya detay sayfasına git
            val bundle = Bundle().apply {
                putString("dreamId", dream.id)
            }
            findNavController().navigate(R.id.dreamDetailFragment, bundle)
        }
        binding.rvPopularDreams.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = popularDreamsAdapter
        }
    }
    
    private fun setupObservers() {
        // Kategorileri izle
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            categoryAdapter.submitList(categories)
        }
        
        // Günün rüyasını izle
        viewModel.featuredDream.observe(viewLifecycleOwner) { dream ->
            dream?.let {
                binding.tvDailyDreamTitle.text = it.title
                binding.tvDailyDreamInterpretation.text = it.interpretation
                binding.cardDailyDream.setOnClickListener { _ ->
                    val bundle = Bundle().apply {
                        putString("dreamId", it.id)
                    }
                    findNavController().navigate(R.id.dreamDetailFragment, bundle)
                }
            }
        }
        
        // Son rüyaları izle
        viewModel.recentDreams.observe(viewLifecycleOwner) { dreams ->
            recentDreamsAdapter.submitList(dreams)
            // XML'de emptyViewRecentDreams olmadığı için şimdilik kaldırıldı
            // Boş durum görünümü eklendiğinde bu kısım güncellenecek
        }
        
        // Popüler rüyaları izle
        viewModel.popularDreams.observe(viewLifecycleOwner) { dreams ->
            popularDreamsAdapter.submitList(dreams)
        }
        
        // Yükleme durumunu izle
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            // XML'de progressBar olmadığı için şimdilik kaldırıldı
            // Yükleme göstergesi eklendiğinde bu kısım güncellenecek
        }
        
        // Hata durumunu izle
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }
    
    private fun setupClickListeners() {
        // Arama kutusuna tıklandığında arama sayfasına git
        binding.cardSearch.setOnClickListener {
            // TODO: Arama sayfasına yönlendir
        }
        
        // Profil resmine tıklandığında profil sayfasına git
        binding.btnProfile.setOnClickListener {
            navigateToBottomNavDestination(R.id.profileFragment)
        }
        
        // "Tümünü Gör" butonlarına tıklandığında ilgili sayfaya git
        binding.tvSeeAllRecent.setOnClickListener {
            navigateToBottomNavDestination(R.id.myDreamsFragment)
        }
        
        binding.tvSeeAllPopular.setOnClickListener {
            navigateToBottomNavDestination(R.id.exploreFragment)
        }
        
        // Rüya ekle butonuna tıklandığında rüya ekleme sayfasına git
        binding.fabAddDream.setOnClickListener {
            navigateToBottomNavDestination(R.id.addDreamFragment)
        }
    }
    
    private fun navigateToBottomNavDestination(destinationId: Int) {
        findNavController().navigate(destinationId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}