package com.example.goodgun.add_food

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.goodgun.databinding.FoodlistItemBinding
import com.example.goodgun.roomDB.FoodEntity

class FoodListAdapter(val items: ArrayList<FoodEntity>, val isChecked: ArrayList<Boolean>) : RecyclerView.Adapter<FoodListAdapter.ViewHolder>() {
    var itemClickListener: OnItemClickListener? = null
    interface OnItemClickListener {
        fun onItemClick(holder: ViewHolder, position: Int)
    }
    inner class ViewHolder(val binding: FoodlistItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.checkBox.setOnClickListener {
                itemClickListener?.onItemClick(this, adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(FoodlistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        Log.i("FoodLens", items.size.toString())
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.foodName.text = items[position].name
        holder.binding.foodName.isSelected = true
        holder.binding.checkBox.isChecked = isChecked[position]
    }
}
