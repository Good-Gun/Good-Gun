package com.example.goodgun.add_food

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.goodgun.databinding.AddFoodRowBinding
import com.example.goodgun.roomDB.FoodEntity

class FoodAddAdapter(var items: List<FoodEntity>) : RecyclerView.Adapter<FoodAddAdapter.ViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(data: FoodEntity, position: Int)
    }

    // 하나의 data 에 대해 서로다른 이벤트리스너 등록가능
    var itemadd: OnItemClickListener? = null
    var itemdelete: OnItemClickListener? = null

    inner class ViewHolder(val binding: AddFoodRowBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.foodAdd.setOnClickListener {
                itemadd?.onItemClick(items[adapterPosition], adapterPosition)

            }
            binding.foodDelete.setOnClickListener {
                itemdelete?.onItemClick(items[adapterPosition], adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = AddFoodRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.foodName.text = items[position].name
        if (items[position].inroomdb) {
            holder.binding.foodAdd.visibility = View.GONE
        }
    }
}
