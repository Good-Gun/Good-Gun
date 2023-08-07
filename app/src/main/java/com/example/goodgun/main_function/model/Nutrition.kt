package com.example.goodgun.main_function.model

data class Nutrition(
    var calorie: Int,
    var carbohydrates: Int,
    var sugar: Int,
    var protein: Int,
    var fat: Int,
    var trans_fat: Int,
    var saturated_fat: Int,
    var cholesterol: Int,
    var sodium: Int,
) {
    constructor() : this(
        0, 0, 0, 0, 0, 0, 0, 0, 0,
    )
}
