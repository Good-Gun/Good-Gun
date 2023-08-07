package com.example.goodgun.firebase

import android.util.Log
import com.example.goodgun.Food
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

object FirebaseManager {
    private val userId = "lee"
    private val database = FirebaseDatabase.getInstance()

    /*파이어베이스로부터 데이터 가져오기*/
    suspend fun getFoodData(date: String): List<Food> = withContext(Dispatchers.IO) {
        val foodList: MutableList<Food> = mutableListOf<Food>()
        val foodsRef: DatabaseReference =
            database.getReference("user_list").child(userId).child("food")
        val snapshot: DataSnapshot = foodsRef.get().await()
        for (child in snapshot.children) {
            if (child.value != null) {
                val food = child.getValue(Food::class.java)!!
                val snapshotDate = LocalDate.parse(food.registerDate.trim())
                val argDate = LocalDate.parse(date.trim())
                Log.d(
                    "Firebase Communication",
                    "checking fb data ${food.registerDate.trim()} & ${date.trim()}",
                )
                if (argDate <= snapshotDate) {
                    Log.d(
                        "Firebase Communication",
                        "Adding food: ${food.name}, regDate: ${food.registerDate}",
                    )
                    foodList.add(food)
                }
            }
        }
        foodList
    }

    fun getit(date: String): List<Food> {
        val foodList: MutableList<Food> = mutableListOf<Food>()
        val foodsRef: DatabaseReference =
            database.getReference("user_list").child(userId).child("food")
        foodsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(
                    "Firebase Communication",
                    "data count: ${dataSnapshot.childrenCount}",
                )
                // 사용자 데이터 가져오기
                for (snapshot in dataSnapshot.children) {
                    val food = snapshot.getValue(Food::class.java)!!
                    if (snapshot != null) {
                        val snapshotDate = LocalDate.parse(food.registerDate.trim())
                        val argDate = LocalDate.parse(date.trim())
                        Log.d(
                            "Firebase Communication",
                            "checking fb data ${food.registerDate.trim()} & ${date.trim()}",
                        )
                        if (argDate <= snapshotDate) {
                            Log.d(
                                "Firebase Communication",
                                "Adding food: ${food.name}, regDate: ${food.registerDate}",
                            )
                            foodList.add(food)
                        }
                    } else {
                        println("User not found.")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Database error occurred: ${error.message}")
            }
        })
        return foodList
    }

    fun postFoodData(date: String, food: Food) {
        val foodRef =
            FirebaseDatabase.getInstance().getReference("user_list").child("lee").child("food")
        foodRef.child(date).setValue(food)
            .addOnSuccessListener {
                // 성공적으로 데이터가 저장된 경우 실행될 코드
                Log.d("Firebase Communication", "Data Added Successfully: $date, ${food.name}")
            }
            .addOnFailureListener {
                Log.d("Firebase Communication", "Data Add Failed: $date, ${food.name}")
            }
    }
}
