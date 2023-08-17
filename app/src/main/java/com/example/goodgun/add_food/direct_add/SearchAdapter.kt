package com.example.goodgun.add_food.direct_add

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.goodgun.R
import com.example.goodgun.databinding.SearchListBinding
import com.example.goodgun.openAPI.FoodItem
import com.example.goodgun.openAPI.FoodList

class SearchAdapter(private var foodList: List<FoodItem>) : RecyclerView.Adapter<SearchAdapter.FoodViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(data: FoodItem, position: Int)
    }

    // 하나의 data 에 대해 서로다른 이벤트리스너 등록가능
    var click: OnItemClickListener? = null
    var selectedItemPosition: Int = -1

    fun setData(newFoodList: List<FoodItem>){
        foodList = newFoodList
        notifyDataSetChanged()
    }


    inner class FoodViewHolder(val binding: SearchListBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.searchLayout.setOnClickListener {
                click?.onItemClick(foodList[adapterPosition], adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = SearchListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(view)
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        if (position == selectedItemPosition) {
            // 선택된 아이템의 테두리 설정
            holder.binding.searchLayout.background= ContextCompat.getDrawable(
                holder.binding.searchLayout.context,
                R.drawable.item_selected
            )
        } else {
            // 선택되지 않은 아이템의 테두리 설정 제거
            holder.binding.searchLayout.background=null
        }
        holder.binding.num.text = foodList[position].number
        holder.binding.searchName.text = foodList[position].foodName
    }


}