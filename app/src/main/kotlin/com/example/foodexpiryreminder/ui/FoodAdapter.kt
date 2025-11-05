package com.example.foodexpiryreminder.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodexpiryreminder.R
import com.example.foodexpiryreminder.data.FoodItem
import com.example.foodexpiryreminder.databinding.ItemFoodBinding
import java.text.SimpleDateFormat
import java.util.*

class FoodAdapter(
    private val onItemDelete: (FoodItem) -> Unit,
    private val onItemSyncCalendar: (FoodItem) -> Unit
) : ListAdapter<FoodItem, FoodAdapter.FoodViewHolder>(FoodDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding, onItemDelete, onItemSyncCalendar)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class FoodViewHolder(
        private val binding: ItemFoodBinding,
        private val onItemDelete: (FoodItem) -> Unit,
        private val onItemSyncCalendar: (FoodItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(foodItem: FoodItem) {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val expiryDate = sdf.format(Date(foodItem.expiryTime))
            val isExpired = foodItem.expiryTime < System.currentTimeMillis()

            binding.apply {
                tvFoodName.text = foodItem.name
                tvExpiryTime.text = "过期时间: $expiryDate"

                if (isExpired) {
                    tvFoodName.setTextColor(binding.root.context.getColor(R.color.red))
                    tvStatus.text = "已过期"
                    tvStatus.setTextColor(binding.root.context.getColor(R.color.red))
                } else {
                    tvFoodName.setTextColor(binding.root.context.getColor(R.color.black))
                    val daysLeft = (foodItem.expiryTime - System.currentTimeMillis()) / (24 * 60 * 60 * 1000)
                    tvStatus.text = "还剩 $daysLeft 天"
                    tvStatus.setTextColor(binding.root.context.getColor(R.color.green))
                }

                btnDelete.setOnClickListener {
                    onItemDelete(foodItem)
                }

                btnSyncCalendar.setOnClickListener {
                    onItemSyncCalendar(foodItem)
                }
            }
        }
    }

    private class FoodDiffCallback : DiffUtil.ItemCallback<FoodItem>() {
        override fun areItemsTheSame(oldItem: FoodItem, newItem: FoodItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FoodItem, newItem: FoodItem): Boolean {
            return oldItem == newItem
        }
    }
}
