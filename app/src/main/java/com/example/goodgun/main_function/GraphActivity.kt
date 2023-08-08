package com.example.goodgun.main_function

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity


import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.*
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.example.goodgun.Food

import com.example.goodgun.R
import com.example.goodgun.User
import com.example.goodgun.databinding.ActivityGraphBinding
import com.example.goodgun.firebase.FirebaseManager
import com.example.goodgun.main_function.model.Nutrition
import com.google.firebase.database.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

class GraphActivity : AppCompatActivity() {
    lateinit var binding: ActivityGraphBinding
    lateinit var nutrition: Nutrition
    var days = 0

    val api_key = "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" //not valid


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGraphBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //addData(LocalDateTime.now().format(DateTimeFormatter.ofPattern(" yyyy-MM-dd")))
        initLayout()
        //initAI()



    }

    private fun initLayout() {
        val items = resources.getStringArray(R.array.my_array)
        binding.apply {
            val myAdapter = ArrayAdapter(
                this@GraphActivity,
                android.R.layout.simple_spinner_dropdown_item,
                items
            )
            spinner1.adapter = myAdapter
            spinner1.setSelection(0)
            spinner1.onItemSelectedListener = SpinnerItemSelectListener()
        }
    }

    private fun initChart() {
        binding.apply {
            pvCalorie.progress = nutrition.calorie.toFloat() / 2000 * 100
            pvCarbohydrates.progress = nutrition.carbohydrates.toFloat() / 2000 * 100
            pvProteins.progress = nutrition.protein.toFloat() / 2000 * 100
            pvFat.progress = nutrition.fat.toFloat() / 2000 * 100
            pvTransFat.progress = nutrition.trans_fat.toFloat() / 2000 * 100
            pvSaturatedFat.progress = nutrition.saturated_fat.toFloat() / 2000 * 100
            pvCholesterol.progress = nutrition.cholesterol.toFloat() / 2000 * 100
            pvSugar.progress = nutrition.sugar.toFloat() / 2000 * 100
            pvSodium.progress = nutrition.sodium.toFloat() / 2000 * 100
        }
    }

    inner class SpinnerItemSelectListener: AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>,
            view: View,
            position: Int,
            id: Long
        ) {
            //dateSelect(position)
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
                    nutrition = FirebaseManager.getNutritionData(formatted)
                }
                initChart()
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>) {
        }
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
                date
            )
            FirebaseManager.postFoodData(date, food)
        }
    }
}
