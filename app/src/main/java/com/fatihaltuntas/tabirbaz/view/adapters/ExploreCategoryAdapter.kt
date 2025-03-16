package com.fatihaltuntas.tabirbaz.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fatihaltuntas.tabirbaz.databinding.ItemCategoryGridBinding
import com.fatihaltuntas.tabirbaz.model.DreamCategory

class ExploreCategoryAdapter(private val onCategoryClick: (DreamCategory) -> Unit) :
    ListAdapter<DreamCategory, ExploreCategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryGridBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = getItem(position)
        holder.bind(category)
    }

    inner class CategoryViewHolder(private val binding: ItemCategoryGridBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: DreamCategory) {
            binding.tvCategoryName.text = category.name
            
            // Eğer rüya sayısı bilgisi varsa göster
            binding.tvDreamCount.text = "${category.id.length} rüya" // Örnek olarak, gerçekte rüya sayısı başka şekilde alınabilir
            
            // Kategori ikonu ayarlanabilir, şimdilik placeholder kullanıyoruz
            // Eğer iconUrl boş değilse, Glide veya Picasso ile yüklenebilir
            
            binding.root.setOnClickListener {
                onCategoryClick(category)
            }
        }
    }

    private class CategoryDiffCallback : DiffUtil.ItemCallback<DreamCategory>() {
        override fun areItemsTheSame(oldItem: DreamCategory, newItem: DreamCategory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DreamCategory, newItem: DreamCategory): Boolean {
            return oldItem == newItem
        }
    }
} 