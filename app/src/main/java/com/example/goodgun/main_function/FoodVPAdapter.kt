package com.example.goodgun.main_function

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class FoodVPAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private val fragmentTexts = mutableListOf<String>()

    fun setFragmentTexts(texts: List<String>) {
        fragmentTexts.clear()
        fragmentTexts.addAll(texts)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = fragmentTexts.size

    override fun createFragment(position: Int): Fragment {
        val fragment = FoodFragment()
        fragment.setText(fragmentTexts[position])
        return fragment
    }
}
