package com.example.goodgun.main_function

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.aallam.openai.api.chat.*
import com.example.goodgun.Food
import com.example.goodgun.R
import com.example.goodgun.User
import com.example.goodgun.databinding.ActivityGraphBinding
import com.example.goodgun.firebase.FirebaseManager
import com.example.goodgun.main_function.model.Nutrition
import com.google.firebase.database.*
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.days

class GraphActivity : AppCompatActivity() {
    lateinit var binding: ActivityGraphBinding
    lateinit var foodList: List<Food>
    var days = 0

    val api_key = "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" // not valid
    val userId = "CCZLvkLE6GfhwlhL4XTyk1vnKrp2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGraphBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLayout()
        // initAI()
    }

    private fun initLayout() {
        val items = resources.getStringArray(R.array.my_array)
        binding.apply {
            val myAdapter = ArrayAdapter(
                this@GraphActivity,
                android.R.layout.simple_spinner_dropdown_item,
                items,
            )
            spinner1.adapter = myAdapter
            spinner1.setSelection(0)
            spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long,
                ) {
                    // dateSelect(position)
                    var time = LocalDateTime.now()
                    when (position) {
                        0 -> {
                            days = 1
                        }
                        1 -> {
                            days = 7
                            time = time.minusWeeks(1)
                        }
                        2 -> {
                            time = time.minusMonths(1)
                            days = YearMonth.now().minusMonths(1).lengthOfMonth()
                        }
                        3 -> {
                            for (i in 1..3) {
                                days += YearMonth.now().minusMonths(i.toLong()).lengthOfMonth()
                            }
                            time = time.minusMonths(3)
                        }
                        4 -> {
                            for (i in 1..6) {
                                days += YearMonth.now().minusMonths(i.toLong()).lengthOfMonth()
                                time = time.minusMonths(6)
                            }
                        }
                    }
                    val formatter = DateTimeFormatter.ofPattern(" yyyy-MM-dd")
                    val formatted = time.format(formatter)

                    CoroutineScope(Dispatchers.Main).launch {
                        withContext(Dispatchers.IO) {
                            foodList = FirebaseManager.getFoodData(formatted)
                        }
                        // addData(formatted)
                        delay(1000)
                        Log.e("Firebase Communication", "Check After foodList initialization")
                        initChart()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
        }
    }

    private fun initChart() {
        binding.apply {
            var nutrition = Nutrition()
            for (item in foodList) {
                Log.d("Firebase Communication", "${item.name}, ${item.registerDate}")
                nutrition.apply {
                    calorie += item.calorie
                    carbohydrates += item.carbohydrates
                    fat += item.fat
                    saturated_fat += item.saturated_fat
                    trans_fat += item.trans_fat
                    cholesterol += item.cholesterol
                    protein += item.protein
                    sodium += item.sodium
                    sugar += item.sugar
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

            pvCalorie.progress = nutrition.calorie.toFloat()
            pvCarbohydrates.progress = nutrition.carbohydrates.toFloat()
            pvProteins.progress = nutrition.protein.toFloat()
            pvFat.progress = nutrition.fat.toFloat()
            pvTransFat.progress = nutrition.trans_fat.toFloat()
            pvSaturatedFat.progress = nutrition.saturated_fat.toFloat()
            pvCholesterol.progress = nutrition.cholesterol.toFloat()
            pvSugar.progress = nutrition.sugar.toFloat()
            pvSodium.progress = nutrition.sodium.toFloat()
        }
    }

    fun readUserData(usersRef: DatabaseReference, userId: String) {
        // 단일 사용자에 대한 ValueEventListener를 추가합니다.
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // 사용자 데이터 가져오기
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    Log.d("Firebase Communication", "User Name: ${user.u_name}")
                    Log.d("Firebase Communication", "User Name: ${user.u_email}")
                } else {
                    println("User not found.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Database error occurred: ${error.message}")
            }
        }

        // ValueEventListener를 등록하고 해당 사용자 데이터를 읽습니다.
        val userRef = usersRef.child(userId)
        userRef.addListenerForSingleValueEvent(valueEventListener)
    }

    /*temporary Addition of Data*/
    private fun addData(date: String) {
        for (i in 1..10) {
            val food = Food(
                0 + i,
                "food" + (i + 1).toString(),
                100 + i,
                101 + i,
                102 + i,
                103 + i,
                104 + i,
                105 + i,
                106 + i,
                107 + i,
                108 + i,
                date,
            )
            FirebaseManager.postFoodData(date, food)
        }
    }
}
