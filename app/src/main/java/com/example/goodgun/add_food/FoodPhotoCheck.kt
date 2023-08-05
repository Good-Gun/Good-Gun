package com.example.goodgun.add_food

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.goodgun.R
import com.example.goodgun.databinding.ActivityFoodPhotoCheckBinding
import com.example.goodgun.roomDB.FoodEntity

class FoodPhotoCheck : AppCompatActivity() {
    val binding: ActivityFoodPhotoCheckBinding by lazy {
        ActivityFoodPhotoCheckBinding.inflate(layoutInflater)
    }
    lateinit var data: ArrayList<FoodEntity>
    lateinit var adapter: FoodListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initLayout()
    }

    private fun initLayout() {
        binding.apply {
            adapter = FoodListAdapter(data)
            recyclerView.layoutManager = LinearLayoutManager(this@FoodPhotoCheck, LinearLayoutManager.VERTICAL, false)
        }
    }
}