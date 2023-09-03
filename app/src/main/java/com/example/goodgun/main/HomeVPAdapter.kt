package com.example.goodgun.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.goodgun.network.model.Food

class HomeVPAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private val fragmentFood = mutableListOf<Food>()

    fun setFragmentFood(food: List<Food>) {
        fragmentFood.clear()
        fragmentFood.addAll(food)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = fragmentFood.size

    override fun createFragment(position: Int): Fragment {
        val fragment = HomeFoodFragment()
        fragment.setFood(fragmentFood[position])
        return fragment
    }
}
