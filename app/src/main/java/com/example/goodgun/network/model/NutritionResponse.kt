package com.example.goodgun.network.model
import com.example.goodgun.Food

data class NutritionResponse(
    val nutrition: Nutrition,
    val food_list:ArrayList<Food> = arrayListOf()
) {
    constructor() : this(
        Nutrition(0, 0, 0, 0, 0, 0, 0, 0, 0)
    )
}