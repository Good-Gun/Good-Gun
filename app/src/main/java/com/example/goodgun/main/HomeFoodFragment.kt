package com.example.goodgun.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.goodgun.databinding.FragmentHomeFoodBinding
import com.example.goodgun.network.model.Food

class HomeFoodFragment : Fragment() {

    var binding: FragmentHomeFoodBinding? = null
    private var food: Food? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeFoodBinding.inflate(layoutInflater, container, false)
        binding?.apply {
            tvName.text = food?.name
            tvCal.text = food?.calory.toString()
            tvC.text = food?.carbohydrates.toString()
            tvF.text = food?.fat.toString()
            tvP.text = food?.protein.toString()
        }
        return binding!!.root
    }
    fun setFood(food: Food) {
        this.food = food
    }
}
