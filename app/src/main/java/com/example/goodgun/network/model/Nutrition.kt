package com.example.goodgun.network.model

import android.util.Log
import com.example.goodgun.ApplicationClass

data class Nutrition(
    var calorie: Int,
    var carbohydrates: Int,
    var sugar: Int,
    var fat: Int,
    var trans_fat: Int,
    var saturated_fat: Int,
    var protein: Int,
    var sodium: Int,
    var cholesterol: Int,
) {
    constructor() : this(
        0, 0, 0, 0, 0, 0, 0, 0, 0,
    )

    private val arr1: List<String> = listOf("탄수화물", "당류", "지방", "트랜스지방", "포화지방", "단백질", "나트륨", "콜레스테롤")
    private val arr2: List<String> = listOf("carbohydrates", "sugar","fats", "trans_fat", "saturated_fat", "proteins", "sodium", "cholesterol")

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
                question += " 섭취를 하는 사람에게 건강해질 식단을 추천해줘. 앞뒤 설명은 일절 보여주지 말고, 출력 형식은 음식이름:간략한 설명 으로 해서 5개 보여줘"
            } else {
                question += " 섭취를 하는 사람에게 건강해질 생활 패턴을 추천해줘. 앞뒤 설명은 일절 보여주지 말고, 출력 형식은 다음과 같이 해서 5개 보여줘: 숫자.제목:설명"
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
