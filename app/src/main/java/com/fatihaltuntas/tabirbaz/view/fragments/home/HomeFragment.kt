package com.fatihaltuntas.tabirbaz.view.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
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
import com.fatihaltuntas.tabirbaz.util.Resource

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: HomeViewModel by viewModels { 
        ViewModelFactory(requireActivity().application) 
    }
    private lateinit var categoryAdapter: DreamCategoryAdapter
    private lateinit var recentDreamsAdapter: DreamAdapter
    private lateinit var popularDreamsAdapter: DreamAdapter
    private lateinit var loadingIndicator: ProgressBar

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

        // ProgressBar'ı başlat
        loadingIndicator = ProgressBar(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            visibility = View.GONE
        }

        (binding.root as ViewGroup).addView(loadingIndicator)
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
        viewModel.featuredDream.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    binding.cardDailyDream.visibility = View.VISIBLE
                    loadingIndicator.visibility = View.GONE
                    
                    resource.data?.let { dream ->
                        binding.tvDailyDreamTitle.text = dream.title
                        binding.tvDailyDreamInterpretation.text = dream.interpretation
                        
                        binding.cardDailyDream.setOnClickListener {
                            val bundle = Bundle().apply {
                                putString("dreamId", dream.id)
                            }
                            findNavController().navigate(R.id.dreamDetailFragment, bundle)
                        }
                    }
                }
                is Resource.Error -> {
                    binding.cardDailyDream.visibility = View.GONE
                    loadingIndicator.visibility = View.GONE
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    binding.cardDailyDream.visibility = View.GONE
                    loadingIndicator.visibility = View.VISIBLE
                }
                else -> {}
            }
        }
        
        // Son rüyaları izle
        viewModel.recentDreams.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    loadingIndicator.visibility = View.GONE
                    
                    resource.data?.let { dreams ->
                        recentDreamsAdapter.submitList(dreams)
                        binding.rvRecentDreams.visibility = if (dreams.isEmpty()) View.GONE else View.VISIBLE
                    }
                }
                is Resource.Error -> {
                    loadingIndicator.visibility = View.GONE
                    binding.rvRecentDreams.visibility = View.GONE
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                    binding.rvRecentDreams.visibility = View.GONE
                }
                else -> {}
            }
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