package com.fatihaltuntas.tabirbaz.view.fragments.dream

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.fatihaltuntas.tabirbaz.R
import com.fatihaltuntas.tabirbaz.databinding.FragmentDreamDetailBinding
import com.fatihaltuntas.tabirbaz.view.adapters.DreamAdapter
import com.fatihaltuntas.tabirbaz.viewmodel.DreamDetailViewModel

class DreamDetailFragment : Fragment() {

    private var _binding: FragmentDreamDetailBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: DreamDetailViewModel by viewModels()
    private val args: DreamDetailFragmentArgs by navArgs()
    
    private lateinit var similarDreamsAdapter: DreamAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDreamDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupToolbar()
        setupAdapter()
        setupClickListeners()
        
        // Rüya detayını yükle
        viewModel.loadDreamDetails(args.dreamId)
        
        // ViewModel'daki verileri izle
        setupObservers()
    }
    
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun setupAdapter() {
        similarDreamsAdapter = DreamAdapter { dream ->
            // Benzer rüyaya tıklandığında o rüyanın detayına git
            val action = DreamDetailFragmentDirections.actionDreamDetailFragmentSelf(dreamId = dream.id)
            findNavController().navigate(action)
        }
        
        binding.rvSimilarDreams.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = similarDreamsAdapter
        }
    }
    
    private fun setupClickListeners() {
        // Paylaş butonuna tıklandığında içeriği paylaş
        binding.fabShare.setOnClickListener {
            shareCurrentDream()
        }
    }
    
    private fun setupObservers() {
        // Rüya detayını izle
        viewModel.dream.observe(viewLifecycleOwner) { dream ->
            dream?.let {
                binding.tvDreamTitle.text = it.title
                binding.tvCategory.text = it.categoryName
                binding.tvViewCount.text = it.viewCount.toString()
                binding.tvDreamContent.text = it.content
                binding.tvInterpretation.text = it.interpretation
            }
        }
        
        // Benzer rüyaları izle
        viewModel.similarDreams.observe(viewLifecycleOwner) { dreams ->
            similarDreamsAdapter.submitList(dreams)
            binding.tvSimilarDreamsLabel.visibility = if (dreams.isEmpty()) View.GONE else View.VISIBLE
            binding.rvSimilarDreams.visibility = if (dreams.isEmpty()) View.GONE else View.VISIBLE
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
    
    private fun shareCurrentDream() {
        viewModel.dream.value?.let { dream ->
            val shareText = getString(
                R.string.share_dream_text,
                dream.title,
                dream.content,
                dream.interpretation
            )
            
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }
            
            val shareIntent = Intent.createChooser(sendIntent, getString(R.string.share_dream))
            startActivity(shareIntent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}