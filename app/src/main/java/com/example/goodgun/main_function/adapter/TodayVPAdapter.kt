package com.example.goodgun.main_function.adapter

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.goodgun.databinding.FragmentFoodTodayBinding
import com.example.goodgun.main.HomeFoodFragment
import com.example.goodgun.main_function.FoodTodayFragment
import com.example.goodgun.network.model.Food

class TodayVPAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private val fragmentFood = mutableListOf<Food>()

    fun setFragmentFood(food: List<Food>) {
        fragmentFood.clear()
        fragmentFood.addAll(food)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = fragmentFood.size

    override fun createFragment(position: Int): Fragment {
        Log.d("Check OpenAI", "Hi from TodayVPAdapter, ${fragmentFood[position]}")
        val fragment = FoodTodayFragment()
        fragment.setFood(fragmentFood[position])
        return fragment
    }
}