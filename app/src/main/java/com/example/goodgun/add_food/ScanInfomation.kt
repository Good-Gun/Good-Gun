package com.example.goodgun.add_food

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.goodgun.Food
import com.example.goodgun.R
import com.example.goodgun.databinding.ActivityScanInfomationBinding

class ScanInfomation : AppCompatActivity() {

    lateinit var binding: ActivityScanInfomationBinding
    var tmpdata:ArrayList<Food> = ArrayList()
    var foodAddData:ArrayList<Food> = ArrayList()
    lateinit var adapter: FoodAddAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanInfomationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        tmpdata.apply {
            add(Food("data1"))
            add(Food("data2"))
            add(Food("data3"))
            add(Food("data4"))
            add(Food("data5"))
        }

        adapter = FoodAddAdapter(tmpdata)
        adapter.itemadd = object :FoodAddAdapter.OnItemClickListener{
            override fun onItemClick(data: Food, position: Int) {
                foodAddData.add(data)
                binding.recyclerView.findViewHolderForAdapterPosition(position)?.itemView?.findViewById<ImageButton>(R.id.food_add)?.visibility = View.GONE
            }
        }
        adapter.itemdelete=object :FoodAddAdapter.OnItemClickListener{
            // 2번째 onclick 이벤트리스너
            override fun onItemClick(data: Food, position: Int) {
                tmpdata.remove(data)
                adapter.notifyItemRemoved(position)
            }
        }
        binding.recyclerView.adapter = adapter
    }
}