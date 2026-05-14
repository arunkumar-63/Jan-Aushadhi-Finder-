package com.janaushadhi.finder.ui.reminder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.janaushadhi.finder.data.ReminderEntity
import com.janaushadhi.finder.databinding.ItemReminderBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReminderAdapter : ListAdapter<ReminderEntity, ReminderAdapter.ReminderViewHolder>(Diff) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val binding = ItemReminderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReminderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ReminderViewHolder(
        private val binding: ItemReminderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(reminder: ReminderEntity) {
            val formatted = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                .format(Date(reminder.reminderTime))
            binding.reminderText.text = "${reminder.medicineName}\nMonthly refill from $formatted"
        }
    }

    private object Diff : DiffUtil.ItemCallback<ReminderEntity>() {
        override fun areItemsTheSame(oldItem: ReminderEntity, newItem: ReminderEntity) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ReminderEntity, newItem: ReminderEntity) = oldItem == newItem
    }
}
