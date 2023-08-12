package com.example.goodgun.main_function

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.goodgun.databinding.ItemFoodRecommendBinding

class FoodRVAdapter(
    val context: Context,
    private val dataList: ArrayList<String>,
    private val maxItemCount: Int
) : RecyclerView.Adapter<FoodRVAdapter.ItemViewHolder>() {

    /*interface OnItemClickListener{
        fun OnItemClick(position:Int)
        fun OnSwitchClick(position: Int, isChecked: Boolean)
    }

    var itemClickListener:OnItemClickListener?= null*/

    inner class ItemViewHolder(val binding: ItemFoodRecommendBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            /*binding.tvGroupName.setOnClickListener{
                itemClickListener!!.OnItemClick(bindingAdapterPosition)
            }*/
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ItemViewHolder {
        val binding = ItemFoodRecommendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        if (position < maxItemCount) {
            val listposition = dataList[position].split(':')

            Log.d("Checking OPENAI", "tokenized result at index $position: ${listposition[0].trim()}, ${listposition[1].trim()}")
            holder.binding.apply {
                tvFoodName.text = listposition[0].trim()
                tvFoodInfo.text = listposition[1].trim()
                /*Log.d("ColorCode", "color = ${listposition.label}, ${R.color.color_gr4}")
                tagColor.backgroundTintList = context.resources.getColorStateList(listposition.label)*/
            }
        }
    }

    override fun getItemCount(): Int {
        return if (dataList.size <= maxItemCount) {
            dataList.size
        } else {
            maxItemCount
        }
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }
}
