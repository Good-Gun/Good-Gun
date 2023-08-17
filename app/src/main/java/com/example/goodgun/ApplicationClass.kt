package com.example.goodgun

import android.app.Application
import android.util.Log
import com.example.goodgun.network.NetworkManager
import com.example.goodgun.network.model.Nutrition
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ApplicationClass : Application() {
    companion object {
        lateinit var uid: String
        lateinit var email: String
        var uname: String = ""

        lateinit var user: User

        var BMR: Double = 0.0
        var calorie: Double = 0.0
        var maxNutrition: Nutrition = Nutrition()
        var workout_level: List<Double> = listOf(1.2, 1.375, 1.55, 1.725, 1.9)

        fun updateUserInfo() {
            val auth = Firebase.auth
            val currentUser = auth.currentUser

            if (currentUser != null) {
                uid = currentUser.uid
                email = currentUser.email!!

                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO) {
                        user = NetworkManager.getUserData()
                    }
                    uname = user.u_name
                    Log.d("Login Check", "userSnapshot key = ${user.u_name}")
                    calculateMaxNut()
                }
            }
        }

        fun calculateMaxNut() {
            BMR = 88.362 + (13.397 * user.u_weight.toInt()) + (4.799 * user.u_height.toInt()) - (5.677 * user.u_age.toInt()) // user.u_age도 등록해야함

            calorie = if (user.u_exercise_freq.toInt() < 1) {
                BMR * 1.2 // * 평소 운동 강도
            } else if (user.u_exercise_freq.toInt() > 5) {
                BMR * 1.9
            } else {
                BMR * workout_level[user.u_exercise_freq.toInt() - 1]
            }

            Log.d("Calorie Check", "$calorie")

            maxNutrition.calorie = calorie.toInt()
            maxNutrition.carbohydrates = (calorie * 0.65).toInt()
            maxNutrition.protein = (calorie * 0.2).toInt()
            maxNutrition.fat = (calorie * 0.3).toInt()
            maxNutrition.trans_fat = (calorie * 0.01).toInt()
            maxNutrition.saturated_fat = (calorie * if (user.u_age.toInt() >= 19) 0.07 else 0.08).toInt()
            maxNutrition.sugar = 25
            maxNutrition.sodium = 2000
            maxNutrition.cholesterol = 300
        }
    }

    override fun onCreate() {
        super.onCreate()
        updateUserInfo()
    }
}
