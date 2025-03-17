package com.fatihaltuntas.tabirbaz.view.fragments.dream

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fatihaltuntas.tabirbaz.R
import com.fatihaltuntas.tabirbaz.databinding.FragmentAddDreamBinding
import com.fatihaltuntas.tabirbaz.util.Resource
import com.fatihaltuntas.tabirbaz.viewmodel.DreamViewModel
import com.fatihaltuntas.tabirbaz.viewmodel.ViewModelFactory
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddDreamFragment : Fragment() {

    private var _binding: FragmentAddDreamBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DreamViewModel
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("tr"))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddDreamBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel'i başlat
        viewModel = ViewModelProvider(this, ViewModelFactory())[DreamViewModel::class.java]

        setupToolbar()
        setupDatePicker()
        observeViewModel()
        setupSubmitButton()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupDatePicker() {
        binding.etDreamDate.setOnClickListener {
            showDatePicker()
        }

        binding.tilDreamDate.setEndIconOnClickListener {
            showDatePicker()
        }

        // Bugünün tarihini göster
        viewModel.setSelectedDate(Date())
        binding.etDreamDate.setText(dateFormat.format(Date()))
    }

    private fun showDatePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)
            
            val date = calendar.time
            binding.etDreamDate.setText(dateFormat.format(date))
            viewModel.setSelectedDate(date)
        }
        
        DatePickerDialog(
            requireContext(),
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun observeViewModel() {
        // Kategorileri gözlemle
        viewModel.categories.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let { categories ->
                        setupCategoryChips(categories)
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(
                        requireContext(),
                        resource.message ?: getString(R.string.unknown_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Resource.Loading -> {
                    // Yükleniyor durumunu gösterebiliriz
                }
                else -> {}
            }
        }

        // Rüya ekleme durumunu gözlemle
        viewModel.addDreamStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSubmitDream.isEnabled = true
                    
                    // Rüya detay sayfasına yönlendir
                    resource.data?.id?.let { dreamId ->
                        val action = findNavController().navigate(R.id.action_addDreamFragment_to_dreamResultFragment, 
                            Bundle().apply {
                                putString("dreamId", dreamId)
                            }
                        )
                    }
                    
                    // ViewModel'deki durumu sıfırla
                    viewModel.resetAddDreamStatus()
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSubmitDream.isEnabled = true
                    Toast.makeText(
                        requireContext(),
                        resource.message ?: getString(R.string.unknown_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnSubmitDream.isEnabled = false
                }
                else -> {}
            }
        }
    }

    private fun setupCategoryChips(categories: List<String>) {
        binding.chipGroupCategories.removeAllViews()
        
        categories.forEach { category ->
            val chip = layoutInflater.inflate(
                R.layout.item_category_chip,
                binding.chipGroupCategories,
                false
            ) as Chip
            
            chip.text = category
            chip.isCheckable = true
            
            chip.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    viewModel.setSelectedCategory(category)
                }
            }
            
            binding.chipGroupCategories.addView(chip)
        }
        
        // İlk kategoriyi varsayılan olarak seç
        if (categories.isNotEmpty()) {
            (binding.chipGroupCategories.getChildAt(0) as? Chip)?.isChecked = true
        }
    }

    private fun setupSubmitButton() {
        binding.btnSubmitDream.setOnClickListener {
            val title = binding.etDreamTitle.text.toString().trim()
            val content = binding.etDreamContent.text.toString().trim()
            
            when {
                title.isEmpty() -> {
                    binding.tilDreamTitle.error = "Lütfen rüya başlığını girin"
                }
                content.isEmpty() -> {
                    binding.tilDreamContent.error = "Lütfen rüyanızı anlatın"
                }
                else -> {
                    binding.tilDreamTitle.error = null
                    binding.tilDreamContent.error = null
                    viewModel.addDream(title, content)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}