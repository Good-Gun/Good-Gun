package com.example.goodgun.network.model

import com.example.goodgun.network.model.Food
import com.example.goodgun.network.model.Nutrition

data class NutritionResponse(
    val nutrition: Nutrition,
    val food_list:ArrayList<Food> = arrayListOf()
) {
    constructor() : this(
        Nutrition(0, 0, 0, 0, 0, 0, 0, 0, 0)
    )
}