package com.example.goodgun

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import com.example.goodgun.network.NetworkManager
import com.example.goodgun.network.model.Nutrition
import com.example.goodgun.network.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ApplicationClass : Application() {
    companion object {
        lateinit var sharedPreferences: SharedPreferences
        lateinit var pref_edit: SharedPreferences.Editor

        lateinit var uid: String
        lateinit var email: String
        var uname: String = ""

        lateinit var user: User

        var BMR: Double = 0.0
        var calorie: Double = 0.0
        var maxNutrition: Nutrition = Nutrition()
        var workout_level: List<Double> = listOf(1.2, 1.375, 1.55, 1.725, 1.9)
        var goal_level: Array<Array<Double>> = arrayOf(
            arrayOf(1.0, 1.0, 1.0),
            arrayOf(0.5, 0.3, 0.2),
            arrayOf(0.5, 0.2, 0.3),
            arrayOf(0.5, 0.3, 0.2)
        )

        fun updateUserInfo() {
            val auth = Firebase.auth
            val currentUser = auth.currentUser

            if (currentUser != null) {
                uid = currentUser.uid
                email = currentUser.email!!

                CoroutineScope(Dispatchers.Main).launch {
                    user = NetworkManager.getUserData()
                    uname = user.u_name
                    Log.d("Login Check", "userSnapshot key = ${user.u_name}")
                    calculateMaxNut()
                }
            }
        }

        fun calculateMaxNut() {
            val goal = goal_level[user.u_physical_goals.toInt()]
            val cal = if(user.u_physical_goals.toInt() == 1){
                -500
            } else if(user.u_physical_goals.toInt() == 3){
                500
            } else {
                0
            }

            BMR = 88.362 + (13.397 * user.u_weight.toInt()) + (4.799 * user.u_height.toInt()) - (5.677 * user.u_age.toInt()) // user.u_age도 등록해야함

            calorie = cal + if (user.u_exercise_freq.toInt() < 1) {
                BMR * 1.2 // * 평소 운동 강도
            } else if (user.u_exercise_freq.toInt() > 5) {
                BMR * 1.9
            } else {
                BMR * workout_level[user.u_exercise_freq.toInt() - 1]
            }

            Log.d("Calorie Check", "$calorie")

            maxNutrition.calorie = calorie.toInt()
            maxNutrition.carbohydrates = (calorie * goal[0] / 4).toInt()
            maxNutrition.protein = (calorie * goal[1]/ 4).toInt()
            maxNutrition.fat = (calorie * goal[2] / 9).toInt()
            maxNutrition.trans_fat = (calorie * 0.01 / 9).toInt()
            maxNutrition.saturated_fat = (calorie * (if (user.u_age.toInt() >= 19) 0.07 else 0.08) / 9).toInt()
            maxNutrition.sugar = 25
            maxNutrition.sodium = 2000
            maxNutrition.cholesterol = 300
        }
    }

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = applicationContext.getSharedPreferences("GoodGun", MODE_PRIVATE)
        pref_edit = sharedPreferences.edit()
    }
}
