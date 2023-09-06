package com.example.goodgun.add_food

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.goodgun.databinding.AddFoodRowBinding
import com.example.goodgun.roomDB.FoodEntity
import kotlinx.coroutines.NonDisposableHandle.parent

class FoodAddAdapter(var items: List<FoodEntity>, var context: Context) : RecyclerView.Adapter<FoodAddAdapter.ViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(data: FoodEntity, position: Int, amount: Double)
    }
    interface OnEditClickListener {
        fun onEditClick(data: FoodEntity, position: Int)
    }
    interface OnTextChangeListener {
        fun onTextChanged(data: FoodEntity, position: Int, amount: Double)
    }

    // 하나의 data 에 대해 서로다른 이벤트리스너 등록가능
    var itemedit: OnEditClickListener? = null
    var itemdelete: OnItemClickListener? = null
    var itemchange: OnTextChangeListener? = null

    fun isStringConvertibleToDouble(input: String): Boolean {
        return try {
            input.toDouble()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }

    inner class ViewHolder(val binding: AddFoodRowBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.foodEdit.setOnClickListener {
                itemedit?.onEditClick(items[adapterPosition], adapterPosition)
            }
            binding.foodDelete.setOnClickListener {
                itemdelete?.onItemClick(items[adapterPosition], adapterPosition, 0.0)
            }
            binding.amount.addTextChangedListener {
                if(isStringConvertibleToDouble(binding.amount.text.toString())){
                    var amt =  binding.amount.text.toString().toDouble()
                    itemchange?.onTextChanged(items[adapterPosition], adapterPosition, amt)
                }
//                if(isStringConvertibleToDouble(binding.amount.text.toString())){
//                    amt =  binding.amount.text.toString().toDouble()
//                    //amt를 db의 모든 영양소에 곱하기
//                    //ex) 칼로리 * amt
//                    // 탄수화물 * amt   ...
//                }
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
        holder.binding.amount.setText(items[position].amount.toString())
    }
}
