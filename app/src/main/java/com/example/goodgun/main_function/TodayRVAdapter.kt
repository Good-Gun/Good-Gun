package com.example.goodgun.main_function

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.goodgun.databinding.ItemFoodListTodayBinding

class TodayRVAdapter (
    val context: Context,
    private val dataList: ArrayList<Pair<String, String>>,
) : RecyclerView.Adapter<TodayRVAdapter.ItemViewHolder>() {

    /*interface OnItemClickListener{
        fun OnItemClick(position:Int)
        fun OnSwitchClick(position: Int, isChecked: Boolean)
    }

    var itemClickListener:OnItemClickListener?= null*/

    inner class ItemViewHolder(val binding:ItemFoodListTodayBinding):RecyclerView.ViewHolder(binding.root){
        init{
            /*binding.tvGroupName.setOnClickListener{
                itemClickListener!!.OnItemClick(bindingAdapterPosition)
            }*/
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ItemViewHolder {
        val binding = ItemFoodListTodayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val listposition = dataList[position]
        holder.binding.apply {
            tvDate.text = listposition.first
            tvName.text = listposition.second
            /*Log.d("ColorCode", "color = ${listposition.label}, ${R.color.color_gr4}")
            tagColor.backgroundTintList = context.resources.getColorStateList(listposition.label)*/

        }
    }

    override fun getItemCount(): Int = dataList.size

    override fun getItemViewType(position: Int): Int {
        return position
    }
}