package com.example.goodgun.main_function

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.goodgun.databinding.FragmentFoodTodayBinding
import com.example.goodgun.roomDB.FoodEntity
import kotlin.math.roundToInt

class FoodTodayFragment : Fragment() {

    var binding: FragmentFoodTodayBinding? = null
    private var food: FoodEntity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFoodTodayBinding.inflate(layoutInflater, container, false)
        Log.d("Check OpenAI", "from FoodTodayFragment${food?.name}")
        binding?.apply {
            tvName.text = food?.name
            tvCal.text = food?.calory!!.roundToInt().toString()
            tvC.text = food?.carbohydrates!!.roundToInt().toString()
            tvF.text = food?.fat!!.roundToInt().toString()
            tvP.text = food?.protein!!.roundToInt().toString()
        }
        return binding!!.root
    }

    fun setFood(food: FoodEntity) {
        this.food = food
    }
}
