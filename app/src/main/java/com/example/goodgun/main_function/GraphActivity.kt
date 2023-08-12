package com.example.goodgun.main_function

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.goodgun.ApplicationClass
import com.example.goodgun.Food
import com.example.goodgun.LoadingDialog
import com.example.goodgun.R
import com.example.goodgun.databinding.ActivityGraphBinding
import com.example.goodgun.firebase.FirebaseManager
import com.example.goodgun.main_function.model.Nutrition
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class GraphActivity : AppCompatActivity() {
    private lateinit var loadingDialog: Dialog
    private lateinit var binding: ActivityGraphBinding
    private lateinit var nutrition: Nutrition
    private var days = 0

    val api_key = "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" // not valid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGraphBinding.inflate(layoutInflater)
        setContentView(binding.root)



        loadingDialog = LoadingDialog(this)

        initLayout()
        //initAI()

    }

    private fun initLayout() {
        loadingDialog.show()
        val items = resources.getStringArray(R.array.my_array)
        binding.apply {
            val myAdapter = ArrayAdapter(
                this@GraphActivity,
                android.R.layout.simple_spinner_dropdown_item,
                items,
            )
            spinner1.adapter = myAdapter
            spinner1.setSelection(0)

            spinner1.onItemSelectedListener = SpinnerItemSelectListener()
        }
    }

    private fun initChart() {
        val max = ApplicationClass.maxNutrition
        binding.apply {
            pvCalorie.progress = nutrition.calorie.toFloat() / max.calorie * 100
            pvCarbohydrates.progress = nutrition.carbohydrates.toFloat() / max.carbohydrates * 100
            pvProteins.progress = nutrition.protein.toFloat() / max.protein * 100
            pvFat.progress = nutrition.fat.toFloat() / max.fat * 100
            pvTransFat.progress = nutrition.trans_fat.toFloat() / max.trans_fat * 100
            pvSaturatedFat.progress = nutrition.saturated_fat.toFloat() / max.saturated_fat * 100
            pvCholesterol.progress = nutrition.cholesterol.toFloat() / max.cholesterol * 100
            pvSugar.progress = nutrition.sugar.toFloat() / max.sugar * 100
            pvSodium.progress = nutrition.sodium.toFloat() / max.sodium * 100
        }
        loadingDialog.dismiss()
    }

    inner class SpinnerItemSelectListener : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>,
            view: View,
            position: Int,
            id: Long
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
                date,
            )
            FirebaseManager.postFoodData(date, food)
        }
    }
}
