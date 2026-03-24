package com.healthdietapp.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.healthdietapp.R
import com.healthdietapp.data.model.HistoryItem
import com.healthdietapp.databinding.ItemHistoryCardBinding

class HistoryAdapter : ListAdapter<HistoryItem, HistoryAdapter.HistoryViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HistoryViewHolder(private val binding: ItemHistoryCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HistoryItem) {
            binding.tvDate.text = formatDate(item.created_at)
            binding.tvBmi.text = "BMI ${String.format("%.1f", item.bmi)}"
            binding.tvBmiCategory.text = item.bmi_category
            binding.tvObesityLevel.text = item.predicted_obesity_level.replace("_", " ")
            binding.tvBmr.text = "${item.bmr.toInt()} kcal"
            binding.tvCalorieTarget.text = "${item.daily_calorie_target} kcal"
            binding.tvConfidence.text = "${(item.confidence_score * 100).toInt()}%"

            val context = binding.root.context
            val bmiColor = when {
                item.bmi_category.contains("Insufficient", ignoreCase = true) ||
                item.bmi_category.contains("Underweight", ignoreCase = true) ->
                    context.getColor(R.color.bmi_underweight)
                item.bmi_category.contains("Normal", ignoreCase = true) ->
                    context.getColor(R.color.bmi_normal)
                item.bmi_category.contains("Overweight", ignoreCase = true) ->
                    context.getColor(R.color.bmi_overweight)
                else -> context.getColor(R.color.bmi_obese)
            }
            (binding.tvBmiCategory.background as? android.graphics.drawable.GradientDrawable)
                ?.setColor(bmiColor)
                ?: binding.tvBmiCategory.setBackgroundColor(bmiColor)
        }

        private fun formatDate(isoDate: String): String {
            return try {
                val input = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", java.util.Locale.getDefault())
                val output = java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault())
                val date = input.parse(isoDate)
                if (date != null) output.format(date) else isoDate
            } catch (e: Exception) {
                isoDate.take(16).replace("T", " ")
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<HistoryItem>() {
        override fun areItemsTheSame(oldItem: HistoryItem, newItem: HistoryItem) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: HistoryItem, newItem: HistoryItem) = oldItem == newItem
    }
}
