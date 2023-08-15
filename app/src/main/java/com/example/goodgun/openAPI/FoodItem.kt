package com.example.goodgun.openAPI

import com.google.gson.annotations.SerializedName

data class FoodItem(
    @SerializedName("DESC_KOR") val foodName: String,
    @SerializedName("NUTR_CONT1") val kcal: String,
)
