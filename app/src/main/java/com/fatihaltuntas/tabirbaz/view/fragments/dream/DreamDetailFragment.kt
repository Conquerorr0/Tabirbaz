package com.fatihaltuntas.tabirbaz.view.fragments.dream

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fatihaltuntas.tabirbaz.R
import com.fatihaltuntas.tabirbaz.databinding.FragmentDreamDetailBinding
import com.fatihaltuntas.tabirbaz.model.Dream
import com.fatihaltuntas.tabirbaz.util.Resource
import com.fatihaltuntas.tabirbaz.view.adapters.DreamAdapter
import com.fatihaltuntas.tabirbaz.viewmodel.DreamDetailViewModel
import com.fatihaltuntas.tabirbaz.viewmodel.DreamViewModel
import com.fatihaltuntas.tabirbaz.viewmodel.ViewModelFactory

class DreamDetailFragment : Fragment() {

    private var _binding: FragmentDreamDetailBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: DreamDetailViewModel by viewModels { 
        ViewModelFactory(requireActivity().application) 
    }
    private lateinit var dreamViewModel: DreamViewModel
    private var dreamId: String? = null
    private var dreamContent: String = ""
    
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
        
        // Argümanları al
        dreamId = arguments?.getString("dreamId")
        
        // DreamViewModel'i başlat
        dreamViewModel = ViewModelProvider(this, ViewModelFactory(requireActivity().application))[DreamViewModel::class.java]
        
        setupToolbar()
        setupAdapter()
        setupClickListeners()
        
        // Rüya detayını yükle
        dreamId?.let { 
            viewModel.loadDreamDetails(it)
            observeInterpretation()
        } ?: run {
            Toast.makeText(requireContext(), "Rüya ID'si bulunamadı", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }
    
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun setupAdapter() {
        similarDreamsAdapter = DreamAdapter { clickedDream ->
            // Benzer rüyaya tıklandığında detay sayfasına yönlendir
            val bundle = Bundle().apply {
                putString("dreamId", clickedDream.id)
            }
            findNavController().navigate(R.id.dreamDetailFragment, bundle)
        }
        
        binding.rvSimilarDreams.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = similarDreamsAdapter
        }
    }
    
    private fun setupClickListeners() {
        binding.fabShare.setOnClickListener {
            shareDream()
        }
        
        // Fab butonuna yorumlama özelliği ekleyeceğiz
        binding.fabShare.setOnLongClickListener {
            interpretDream()
            true
        }
    }
    
    private fun interpretDream() {
        if (dreamId != null && dreamContent.isNotEmpty()) {
            // Yorum yükleniyor durumunu göster
            binding.progressBar.visibility = View.VISIBLE
            binding.tvInterpretationLabel.visibility = View.GONE
            binding.tvInterpretation.visibility = View.GONE
            
            dreamViewModel.interpretDream(dreamId!!, dreamContent)
        } else {
            Toast.makeText(requireContext(), "Rüya yorumlanamadı", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun observeInterpretation() {
        // Rüya yorumu sonucunu izle
        dreamViewModel.interpretationStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success<Dream> -> {
                    binding.progressBar.visibility = View.GONE
                    
                    resource.data?.let { dream ->
                        binding.tvInterpretation.text = dream.interpretation
                        binding.tvInterpretationLabel.visibility = View.VISIBLE
                        binding.tvInterpretation.visibility = View.VISIBLE
                    }
                    
                    dreamViewModel.resetInterpretationStatus()
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    
                    Toast.makeText(
                        requireContext(),
                        resource.message ?: getString(R.string.unknown_error),
                        Toast.LENGTH_LONG
                    ).show()
                    
                    dreamViewModel.resetInterpretationStatus()
                }
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                else -> {}
            }
        }
        
        // Rüya detaylarını izle
        viewModel.dream.observe(viewLifecycleOwner) { dream ->
            dream?.let {
                dreamContent = it.content
                
                // Yorum varsa göster, yoksa gizle
                if (it.interpretation.isNotEmpty()) {
                    binding.tvInterpretation.text = it.interpretation
                    binding.tvInterpretationLabel.visibility = View.VISIBLE
                    binding.tvInterpretation.visibility = View.VISIBLE
                } else {
                    binding.tvInterpretationLabel.visibility = View.GONE
                    binding.tvInterpretation.visibility = View.GONE
                }
            }
        }
    }
    
    private fun shareDream() {
        viewModel.dream.value?.let { dream ->
            val shareText = getString(
                R.string.share_dream_text,
                dream.title,
                dream.content,
                dream.interpretation
            )
            
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }
            
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_dream)))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}