package com.example.goodgun.network.model

import android.util.Log
import com.example.goodgun.ApplicationClass

data class Nutrition(
    var calorie: Double,
    var carbohydrates: Double,
    var sugar: Double,
    var fat: Double,
    var trans_fat: Double,
    var saturated_fat: Double,
    var protein: Double,
    var sodium: Double,
    var cholesterol: Double,
) {
    constructor() : this(
        0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
    )

    private val arr1: List<String> = listOf("칼로리", "탄수화물", "당류", "지방", "트랜스지방", "포화지방", "단백질", "나트륨", "콜레스테롤")
    private val arr2: List<String> = listOf("calories", "carbohydrates", "sugar", "fats", "trans_fat", "saturated_fat", "proteins", "sodium", "cholesterol")

    /*case 1: 음식 추천, 2: 솔루션 */
    fun getQuestion(case: Int): String? {
        var question: String? = ""

        for (i in arr1.indices) {
            if (calculateNutrientIntake(arr2[i]) == 2) {
                question += "과한 " + arr1[i] + ","
            } else if (calculateNutrientIntake(arr2[i]) == -2) question += "부족한 " + arr1[i] + ","
        }
        if (question != "") {
            question = question?.removeSuffix(",")
            if (case == 1) {
                question += " 섭취를 하는 사람에게 건강해지기 위한 식단을 추천해줘. 앞뒤 설명은 일절 보여주지 말고, 숫자와 설명을 붙여서 5개 보여줘."
            } else {
                question += " 섭취를 하는 사람에게 건강해지기 위한 생활 패턴을 추천해줘. 앞뒤 설명은 일절 보여주지 말고, 숫자와 설명을 붙여서 5개 보여줘"
            }
            // question += " 섭취를 하는 사람에게 생활 패턴을 추천해줘. 앞뒤 설명 생략하고 숫자 붙여서 부제와 내용 형식으로 보여줘"
        } else {
            question = null
        }

        Log.d("Checking OPENAI", "$question")

        return question
    }

    fun calculateNutrientIntake(nutrient: String): Int {
        var res = 0
        var max = ApplicationClass.maxNutrition

        when (nutrient) {
            "calories" -> res = calculateIntake2(this.calorie, max.calorie)
            "carbohydrates" -> res = calculateIntake2(this.carbohydrates, max.carbohydrates)
            "proteins" -> res = calculateIntake2(this.protein,  max.protein)
            "fats" -> res = calculateIntake2(this.fat, max.fat)
            "sugar" -> res = calculateIntake1(this.sugar, max.sugar)
            "trans_fat" -> res = calculateIntake1(this.trans_fat, max.trans_fat)
            "saturated_fat" -> res = calculateIntake1(saturated_fat, max.saturated_fat)
            "sodium" -> res = calculateIntake1(this.sodium, max.sodium)
            "cholesterol" -> res = calculateIntake1(this.cholesterol, max.cholesterol)
        }
        return res
    }

    // 적용가능: 당류, 콜레스테롤, 나트륨, 포화지방, 트랜스지방
    fun calculateIntake1(nutrient: Double, amount: Double): Int {
        var res = 0

        if (nutrient >= amount * 1.1) {
            res = 2
        } else if (nutrient < amount * 1.1 && nutrient > amount) {
            res = 1
        }
        return res
    }

    // 적용가능: 탄수화물, 단백질, 지방
    private fun calculateIntake2(nutrient: Double, max: Double): Int {
        var res = 0

        if (nutrient >= max * 1.1) {
            res = 2
        } else if (nutrient < max * 1.1 && nutrient > max) {
            res = 1
        } else if (nutrient < max && nutrient > max * 0.9) {
            res = -1
        } else if (nutrient <= max * 0.9) {
            res = -2
        }

        return res
    }
}
