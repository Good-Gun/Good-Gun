package com.example.goodgun.firebase

import android.util.Log
import com.example.goodgun.Food
import com.example.goodgun.main_function.model.Nutrition
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object FirebaseManager {
    private val userId = "MhAk3L1JdrcV2bnoCkvKq2vCnD02"
    private val database = FirebaseDatabase.getInstance()

    /*파이어베이스로부터 데이터 가져오기*/
    suspend fun getFoodData(date: String): List<Food> = withContext(Dispatchers.IO) {
        val foodList: MutableList<Food> = mutableListOf<Food>()
        val datesRef: DatabaseReference =
            database.getReference("user_list").child(userId).child("food")

        val outerSnapshot: DataSnapshot = datesRef.get().await()
        for (dateSnapshot in outerSnapshot.children) {
            if (dateSnapshot.value != null) {
                Log.d(
                    "Firebase Communication",

                    "key ${dateSnapshot.key!!}"
                )

                val date1 = LocalDate.parse(dateSnapshot.key.toString().trim())
                val date2 = LocalDate.parse(date.trim())

                if(date1 >= date2){
                    val innerSnapshot = datesRef.child(dateSnapshot.key!!).get().await()
                    for(foodSnapshot in innerSnapshot.children){
                        val food = foodSnapshot.getValue(Food::class.java)!!
                        Log.d(
                            "Firebase Communication",
                            "Adding food: ${food.name}, regDate: ${food.registerDate}"
                        )
                        foodList.add(food)
                    }

                }
            }
        }
        foodList
    }


    suspend fun getNutritionData(date: String): Nutrition = withContext(Dispatchers.IO) {
        val nutrition = Nutrition()
        var days = 0

        val datesRef: DatabaseReference =
            database.getReference("user_list").child(userId).child("food")

        val outerSnapshot: DataSnapshot = datesRef.get().await()
        for (dateSnapshot in outerSnapshot.children) {
            if (dateSnapshot.value != null) {
                Log.d(
                    "Firebase Communication",
                    "key ${dateSnapshot.key!!}"

                )

                val date1 = LocalDate.parse(dateSnapshot.key.toString().trim())
                val date2 = LocalDate.parse(date.trim())

                if(date1 >= date2){
                    days++
                    val innerSnapshot = datesRef.child(dateSnapshot.key!!).get().await()
                    for(foodSnapshot in innerSnapshot.children){
                        val food = foodSnapshot.getValue(Food::class.java)!!
                        Log.d(
                            "Firebase Communication",

                            "Adding food: ${food.name}, regDate: ${food.registerDate}"
                        )
                        nutrition.apply {
                            calorie += food.calorie
                            carbohydrates += food.carbohydrates
                            fat += food.fat
                            saturated_fat += food.saturated_fat
                            trans_fat += food.trans_fat
                            cholesterol += food.cholesterol
                            protein += food.protein
                            sodium += food.sodium
                            sugar += food.sugar
                        }
                    }
                }
            }

        }
        nutrition.apply {
            calorie /= days
            carbohydrates /= days
            fat /= days
            saturated_fat /= days
            trans_fat /= days
            cholesterol /= days
            protein /= days
            sodium /= days
            sugar /= days
        }
        nutrition
    }

    suspend fun getDayNutrition(date: String): Nutrition = withContext(Dispatchers.IO) {
        val nutrition = Nutrition()

        val datesRef: DatabaseReference =
            database.getReference("user_list").child(userId).child("food").child(date.trim())

        val dataSnapshot: DataSnapshot = datesRef.get().await()
        for (snapshot in dataSnapshot.children) {
            Log.d("Firebase Communication", "in day Nutrition, ${snapshot.key}")
            val food = snapshot.getValue(Food::class.java)!!
            nutrition.apply {
                calorie += food.calorie
                carbohydrates += food.carbohydrates
                fat += food.fat
                saturated_fat += food.saturated_fat
                trans_fat += food.trans_fat
                cholesterol += food.cholesterol
                protein += food.protein
                sodium += food.sodium
                sugar += food.sugar
            }
        }
        nutrition
    }

    fun postFoodData(date: String, food: Food) {
        val foodRef =
            FirebaseDatabase.getInstance().getReference("user_list").child(userId).child("food").child(date.trim()).push()
        foodRef.setValue(food)
            .addOnSuccessListener {
                // 성공적으로 데이터가 저장된 경우 실행될 코드
                Log.d("Firebase Communication", "Data Added Successfully: $date, ${food.name}")
            }
            .addOnFailureListener {
                Log.d("Firebase Communication", "Data Add Failed: $date, ${food.name}")
            }
    }
}
