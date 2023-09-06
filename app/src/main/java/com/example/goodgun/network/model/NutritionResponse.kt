package com.example.goodgun.network.model

import com.example.goodgun.roomDB.FoodEntity

data class NutritionResponse(
    val nutrition: Nutrition,
    val food_list: ArrayList<FoodEntity> = arrayListOf(),
) {
    constructor() : this(
        Nutrition(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
    )
}
