package com.example.goodgun.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.goodgun.databinding.FragmentItemFoodBinding
import com.example.goodgun.network.model.Food

class ItemFoodFragment : Fragment() {

    var binding: FragmentItemFoodBinding? = null
    private var title: String? = null
    private var body: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentItemFoodBinding.inflate(layoutInflater, container, false)
        binding?.apply {
            tvDate.text = title
            tvName.text = body
        }
        return binding!!.root
    }
    fun setFood(food: Food) {
        title = food.registerDate
        body = food.name
    }
}
