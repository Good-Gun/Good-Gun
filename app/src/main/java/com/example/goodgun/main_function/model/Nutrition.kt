package com.example.goodgun.main_function.model

import com.example.goodgun.ApplicationClass

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

    fun calculateNutrientIntake(nutrient: String): Int {
        var res = 0
        var max = ApplicationClass.maxNutrition
        val age = ApplicationClass.user.u_age

        when (nutrient) {
            "carbohydrates" -> res = calculateIntake(this.carbohydrates, 65, 55)
            "proteins" -> res = calculateIntake(this.protein, 20, 7)
            "fats" -> res = calculateIntake(this.fat, 30, 15)
            "sugar" -> res = calculateIntake(this.sugar, max.sugar)
            "trans_fat" -> res = calculateIntake(this.trans_fat, max.trans_fat)
            "saturated_fat" -> res = calculateIntake(saturated_fat, max.saturated_fat)
            "sodium" -> res = calculateIntake(this.sodium, max.sodium)
            "cholesterol" -> res = calculateIntake(this.cholesterol, max.cholesterol)
        }
        return res
    }

    // 적용가능: 당류, 콜레스테롤, 나트륨, 포화지방, 트랜스지방
    fun calculateIntake(nutrient: Int, amount: Int): Int {
        var res = 0

        if (nutrient >= amount * 1.1) {
            res = 2
        } else if (nutrient < amount * 1.1 && nutrient > amount) {
            res = 1
        }
        return res
    }

    // 적용가능: 탄수화물, 단백질, 지방
    private fun calculateIntake(nutrient: Int, up: Int, down: Int): Int {
        var res = 0
        val user_calorie = ApplicationClass.calorie
        val upper_bound = user_calorie * up
        val lower_bound = user_calorie * down

        if (nutrient >= upper_bound * 1.1) {
            res = 2
        } else if (nutrient < upper_bound * 1.1 && nutrient > upper_bound) {
            res = 1
        } else if (nutrient < lower_bound && nutrient > lower_bound * 0.9) {
            res = -1
        } else if (nutrient <= lower_bound * 0.9) {
            res = -2
        }

        return res
    }
}
