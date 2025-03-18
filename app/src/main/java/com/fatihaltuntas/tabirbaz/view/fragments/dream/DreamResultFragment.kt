package com.fatihaltuntas.tabirbaz.view.fragments.dream

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fatihaltuntas.tabirbaz.R
import com.fatihaltuntas.tabirbaz.databinding.FragmentDreamResultBinding
import com.fatihaltuntas.tabirbaz.util.Resource
import com.fatihaltuntas.tabirbaz.viewmodel.DreamViewModel
import com.fatihaltuntas.tabirbaz.viewmodel.ViewModelFactory

class DreamResultFragment : Fragment() {

    private var _binding: FragmentDreamResultBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: DreamViewModel
    private val args: DreamResultFragmentArgs by navArgs()
    private var dreamId: String? = null
    private var dreamContent: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDreamResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // ViewModel'i başlat
        viewModel = ViewModelProvider(this, ViewModelFactory(requireActivity().application))[DreamViewModel::class.java]
        
        // Fragment'a gönderilen rüya ID'sini al
        dreamId = args.dreamId ?: arguments?.getString("dreamId")
        
        setupToolbar()
        setupShareButton()
        setupInterpretButton()
        loadDreamDetails()
        observeViewModel()
    }
    
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun setupShareButton() {
        binding.btnShare.setOnClickListener {
            shareDream()
        }
    }
    
    private fun setupInterpretButton() {
        binding.btnInterpret.setOnClickListener {
            interpretDream()
        }
    }
    
    private fun interpretDream() {
        if (dreamId != null && dreamContent.isNotEmpty()) {
            binding.interpretationSection.visibility = View.GONE
            binding.interpretLoadingLayout.visibility = View.VISIBLE
            
            viewModel.interpretDream(dreamId!!, dreamContent)
        } else {
            Toast.makeText(requireContext(), "Rüya yorumlanamadı", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun loadDreamDetails() {
        binding.loadingLayout.visibility = View.VISIBLE
        binding.contentScrollView.visibility = View.GONE
        
        dreamId?.let {
            viewModel.getDreamById(it)
        } ?: run {
            Toast.makeText(requireContext(), "Rüya ID'si bulunamadı", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }
    
    private fun observeViewModel() {
        // Rüya detaylarını izle
        viewModel.dreamDetails.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    binding.loadingLayout.visibility = View.GONE
                    binding.contentScrollView.visibility = View.VISIBLE
                    
                    resource.data?.let { dream ->
                        dreamContent = dream.content
                        
                        binding.tvDreamTitle.text = dream.title
                        binding.tvCategory.text = dream.categoryName
                        binding.tvDreamContent.text = dream.content
                        
                        // Yorum varsa göster, yoksa yorumlama butonunu göster
                        if (dream.interpretation.isNotEmpty()) {
                            binding.tvInterpretation.text = dream.interpretation
                            binding.interpretationSection.visibility = View.VISIBLE
                            binding.btnInterpret.visibility = View.GONE
                        } else {
                            binding.interpretationSection.visibility = View.GONE
                            binding.btnInterpret.visibility = View.VISIBLE
                        }
                    }
                }
                is Resource.Error -> {
                    binding.loadingLayout.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        resource.message ?: getString(R.string.unknown_error),
                        Toast.LENGTH_LONG
                    ).show()
                }
                is Resource.Loading -> {
                    binding.loadingLayout.visibility = View.VISIBLE
                    binding.contentScrollView.visibility = View.GONE
                }
                else -> {}
            }
        }
        
        // Yorumlama durumunu izle
        viewModel.interpretationStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    binding.interpretLoadingLayout.visibility = View.GONE
                    binding.interpretationSection.visibility = View.VISIBLE
                    binding.btnInterpret.visibility = View.GONE
                    viewModel.resetInterpretationStatus()
                }
                is Resource.Error -> {
                    binding.interpretLoadingLayout.visibility = View.GONE
                    binding.btnInterpret.visibility = View.VISIBLE
                    Toast.makeText(
                        requireContext(),
                        resource.message ?: getString(R.string.unknown_error),
                        Toast.LENGTH_LONG
                    ).show()
                    viewModel.resetInterpretationStatus()
                }
                is Resource.Loading -> {
                    binding.interpretLoadingLayout.visibility = View.VISIBLE
                    binding.btnInterpret.visibility = View.GONE
                }
                else -> {}
            }
        }
    }
    
    private fun shareDream() {
        viewModel.dreamDetails.value?.data?.let { dream ->
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