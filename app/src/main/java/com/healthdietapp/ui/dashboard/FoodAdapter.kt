package com.healthdietapp.ui.dashboard

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.healthdietapp.data.model.FoodItemParcelable
import com.healthdietapp.databinding.ItemFoodCardBinding

class FoodAdapter : ListAdapter<FoodItemParcelable, FoodAdapter.FoodViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FoodViewHolder(private val binding: ItemFoodCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FoodItemParcelable) {
            binding.tvFoodName.text = item.food_name
            binding.tvCalories.text = "${item.calories.toInt()} kcal"
            binding.tvProtein.text = "${item.protein}g"
            binding.tvCarbs.text = "${item.carbs}g"
            binding.tvFat.text = "${item.fat}g"

            // Fade-in animation
            binding.root.alpha = 0f
            ObjectAnimator.ofFloat(binding.root, "alpha", 0f, 1f).apply {
                duration = 400
                start()
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<FoodItemParcelable>() {
        override fun areItemsTheSame(oldItem: FoodItemParcelable, newItem: FoodItemParcelable): Boolean {
            return oldItem.food_name == newItem.food_name
        }

        override fun areContentsTheSame(oldItem: FoodItemParcelable, newItem: FoodItemParcelable): Boolean {
            return oldItem == newItem
        }
    }
}
