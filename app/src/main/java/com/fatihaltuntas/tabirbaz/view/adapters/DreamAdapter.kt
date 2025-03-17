package com.fatihaltuntas.tabirbaz.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fatihaltuntas.tabirbaz.databinding.ItemDreamBinding
import com.fatihaltuntas.tabirbaz.model.Dream

class DreamAdapter(private val onDreamClick: (Dream) -> Unit) :
    ListAdapter<Dream, DreamAdapter.DreamViewHolder>(DreamDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DreamViewHolder {
        val binding = ItemDreamBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DreamViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DreamViewHolder, position: Int) {
        val dream = getItem(position)
        holder.bind(dream)
    }

    inner class DreamViewHolder(private val binding: ItemDreamBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(dream: Dream) {
            binding.tvDreamTitle.text = dream.title
            binding.tvDreamCategory.text = dream.categoryName
            binding.tvViewCount.text = dream.viewCount.toString()
            
            // Eğer kısa bir özet göstermek istersek
            val contentPreview = if (dream.content.length > 100) {
                dream.content.substring(0, 97) + "..."
            } else {
                dream.content
            }
            binding.tvDreamExcerpt.text = contentPreview
            
            binding.root.setOnClickListener {
                onDreamClick(dream)
            }
        }
    }

    private class DreamDiffCallback : DiffUtil.ItemCallback<Dream>() {
        override fun areItemsTheSame(oldItem: Dream, newItem: Dream): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Dream, newItem: Dream): Boolean {
            return oldItem == newItem
        }
    }
} 