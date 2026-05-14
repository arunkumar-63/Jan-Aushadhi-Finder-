package com.janaushadhi.finder.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.janaushadhi.finder.data.MedicineEntity
import com.janaushadhi.finder.databinding.ItemMedicineBinding
import kotlin.random.Random

class MedicineAdapter(
    private val onCheckAvailability: (MedicineEntity, Boolean) -> Unit,
    private val onAddToSavings: (MedicineEntity) -> Unit
) : ListAdapter<MedicineEntity, MedicineAdapter.MedicineViewHolder>(Diff) {
    var highlightBestMatch: Boolean = false
        set(value) {
            field = value
            if (itemCount > 0) notifyItemChanged(0)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val binding = ItemMedicineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MedicineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        holder.bind(getItem(position), highlightBestMatch && position == 0)
    }

    inner class MedicineViewHolder(
        private val binding: ItemMedicineBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(medicine: MedicineEntity, isBestMatch: Boolean) {
            binding.brandNameText.text = medicine.brandName
            binding.genericNameText.text = "Generic: ${medicine.genericName}"
            binding.categoryText.text = "${medicine.category} | ${medicine.dosageForm}"
            binding.brandPriceText.text = medicine.brandPrice.asRupees()
            binding.genericPriceText.text = medicine.genericPrice.asRupees()
            binding.savingsText.text = "You save ${medicine.savings.asRupees()} per strip"
            binding.bestMatchText.visibility = if (isBestMatch) View.VISIBLE else View.GONE
            binding.checkAvailabilityButton.setOnClickListener {
                onCheckAvailability(medicine, Random.nextBoolean())
            }
            binding.addSavingsButton.setOnClickListener { onAddToSavings(medicine) }
        }
    }

    private fun Double.asRupees(): String = "₹${String.format("%.0f", this)}"

    private object Diff : DiffUtil.ItemCallback<MedicineEntity>() {
        override fun areItemsTheSame(oldItem: MedicineEntity, newItem: MedicineEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MedicineEntity, newItem: MedicineEntity): Boolean {
            return oldItem == newItem
        }
    }
}
