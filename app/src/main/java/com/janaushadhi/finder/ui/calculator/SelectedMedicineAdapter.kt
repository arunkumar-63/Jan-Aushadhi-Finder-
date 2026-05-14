package com.janaushadhi.finder.ui.calculator

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.janaushadhi.finder.data.MedicineEntity
import com.janaushadhi.finder.databinding.ItemSelectedMedicineBinding

class SelectedMedicineAdapter :
    ListAdapter<MedicineEntity, SelectedMedicineAdapter.SelectedMedicineViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedMedicineViewHolder {
        val binding = ItemSelectedMedicineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SelectedMedicineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SelectedMedicineViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SelectedMedicineViewHolder(
        private val binding: ItemSelectedMedicineBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(medicine: MedicineEntity) {
            binding.selectedMedicineText.text =
                "${medicine.brandName} to ${medicine.genericName}\nSave ₹${String.format("%.0f", medicine.savings)} monthly"
        }
    }

    private object Diff : DiffUtil.ItemCallback<MedicineEntity>() {
        override fun areItemsTheSame(oldItem: MedicineEntity, newItem: MedicineEntity) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: MedicineEntity, newItem: MedicineEntity) = oldItem == newItem
    }
}
