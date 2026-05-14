package com.janaushadhi.finder.ui.map

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.janaushadhi.finder.databinding.ItemKendraStoreBinding

class KendraStoreAdapter(
    private val onDirections: (KendraStore) -> Unit
) : ListAdapter<KendraStore, KendraStoreAdapter.StoreViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val binding = ItemKendraStoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class StoreViewHolder(
        private val binding: ItemKendraStoreBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(store: KendraStore) {
            binding.storeNameText.text = store.name
            binding.storeAddressText.text = store.address.ifBlank { "Jan Aushadhi Kendra" }
            binding.storeDistanceText.text = "${String.format("%.1f", store.distanceKm)} km away"
            binding.directionsButton.setOnClickListener { onDirections(store) }
        }
    }

    private object Diff : DiffUtil.ItemCallback<KendraStore>() {
        override fun areItemsTheSame(oldItem: KendraStore, newItem: KendraStore): Boolean {
            return oldItem.name == newItem.name && oldItem.location == newItem.location
        }

        override fun areContentsTheSame(oldItem: KendraStore, newItem: KendraStore): Boolean {
            return oldItem == newItem
        }
    }
}
