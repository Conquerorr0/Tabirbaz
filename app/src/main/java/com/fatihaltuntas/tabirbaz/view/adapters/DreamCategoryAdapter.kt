package com.fatihaltuntas.tabirbaz.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fatihaltuntas.tabirbaz.databinding.ItemDreamCategoryBinding
import com.fatihaltuntas.tabirbaz.model.DreamCategory

class DreamCategoryAdapter(private val onCategoryClick: (DreamCategory) -> Unit) :
    ListAdapter<DreamCategory, DreamCategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemDreamCategoryBinding.inflate(
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

    inner class CategoryViewHolder(private val binding: ItemDreamCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: DreamCategory) {
            binding.tvCategoryName.text = category.name
            
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