package com.example.goodgun.main_function

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.goodgun.ApplicationClass
import com.example.goodgun.R
import com.example.goodgun.databinding.ActivityGraphBinding
import com.example.goodgun.network.NetworkManager
import com.example.goodgun.network.model.Food
import com.example.goodgun.network.model.Nutrition
import com.example.goodgun.util.LoadingDialog
import com.skydoves.progressview.ProgressView
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class GraphActivity : AppCompatActivity() {
    private lateinit var loadingDialog: Dialog
    private lateinit var binding: ActivityGraphBinding
    private lateinit var nutrition: Nutrition
    private var days = 0

    val today = LocalDateTime.now().format(DateTimeFormatter.ofPattern(" yyyy-MM-dd"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGraphBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingDialog = LoadingDialog(this)

        // addData(today)

        initLayout()
        // initAI()
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

            val strFormatter = DateTimeFormatter.ofPattern(" yyyy.MM.dd")
            binding.tvDate.text = (time.format(strFormatter) + "~" + LocalDateTime.now().format(strFormatter)).trim()

            CoroutineScope(Dispatchers.Main).launch {
                nutrition = NetworkManager.getNutritionData(formatted)
                initChart()
                setNutrition(nutrition)
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>) {
        }
    }

    private fun setNutrition(nutrition: Nutrition) {
        val max = ApplicationClass.maxNutrition
        binding.apply {
            tvFoodCalorie.text = nutrition.calorie.toString() + "/" + max.calorie
            tvFoodCarbo.text = nutrition.carbohydrates.toString() + "/" + max.carbohydrates
            tvFoodSugar.text = nutrition.sugar.toString() + "/" + max.sugar
            tvFoodFat.text = nutrition.fat.toString() + "/" + max.fat
            tvFoodTrans.text = nutrition.trans_fat.toString() + "/" + max.trans_fat
            tvFoodSaturated.text = nutrition.saturated_fat.toString() + "/" + max.saturated_fat
            tvFoodProtein.text = nutrition.protein.toString() + "/" + max.protein
            tvFoodCholesterol.text = nutrition.cholesterol.toString() + "/" + max.cholesterol
            tvFoodProtein.text = nutrition.protein.toString() + "/" + max.protein
            tvFoodSodium.text = nutrition.sodium.toString() + "/" + max.sodium
        }
        loadingDialog.dismiss()
        setViewColor()
    }

    private fun setViewColor() {
        var str: String = ""
        val arr1: List<String> = listOf("탄수화물", "당류", "지방", "트랜스지방", "포화지방", "단백질", "나트륨", "콜레스테롤")
        val arr2: List<String> = listOf(
            "carbohydrates",
            "sugar",
            "fats",
            "trans_fat",
            "saturated_fat",
            "proteins",
            "sodium",
            "cholesterol",
        )
        val arr3: List<ProgressView> = listOf(
            binding.pvCarbohydrates,
            binding.pvSugar,
            binding.pvFat,
            binding.pvTransFat,
            binding.pvSaturatedFat,
            binding.pvProteins,
            binding.pvSodium,
            binding.pvCholesterol,
        )

        val arr4: List<TextView> = listOf(
            binding.tvFoodCarbo,
            binding.tvFoodSugar,
            binding.tvFoodFat,
            binding.tvFoodTrans,
            binding.tvFoodSaturated,
            binding.tvFoodProtein,
            binding.tvFoodSodium,
            binding.tvFoodCholesterol,
        )
        for (i in arr1.indices) {
            val text = arr4[i].text.toString()
            val spannableString = SpannableString(text)
            when (nutrition.calculateNutrientIntake(arr2[i])) {
                2 -> {
                    arr3[i].highlightView.color = ResourcesCompat.getColor(resources, R.color.red, null)
                    str += arr1[i] + " 섭취량이 적정량보다 매우 높습니다!!\n"
                    spannableString.setSpan(
                        ForegroundColorSpan(Color.RED),
                        0,
                        text.indexOf("/"),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
                    )
                    arr4[i].text = spannableString
                }
                1 -> {
                    arr3[i].highlightView.color = ResourcesCompat.getColor(resources, R.color.yellow, null)
                    str += arr1[i] + " 섭취량이 적정량보다 약간 높습니다!\n"
                    spannableString.setSpan(
                        ForegroundColorSpan(Color.YELLOW),
                        0,
                        text.indexOf("/"),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
                    )
                    arr4[i].text = spannableString
                }
                -1 -> {
                    arr3[i].highlightView.color = ResourcesCompat.getColor(resources, R.color.yellow, null)
                    str += arr1[i] + " 섭취량이 적정량보다 약간 부족합니다!\n"
                    spannableString.setSpan(
                        ForegroundColorSpan(Color.YELLOW),
                        0,
                        text.indexOf("/"),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
                    )
                    arr4[i].text = spannableString
                }
                -2 -> {
                    arr3[i].highlightView.color = ResourcesCompat.getColor(resources, R.color.red, null)
                    str += arr1[i] + " 섭취량이 적정량보다 매우 부족합니다!!\n"
                    spannableString.setSpan(
                        ForegroundColorSpan(Color.RED),
                        0,
                        text.indexOf("/"),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
                    )
                    arr4[i].text = spannableString
                }
            }
        }
        if (str == "") {
            str = "아무 이상 없습니다 :)"
        } else {
            str = str.removeSuffix("\n")
        }

        val keyword = listOf<String>("약간 부족합니다!", "약간 높습니다!", "매우 부족합니다!", "매우 높습니다!!")
        val spannableString = SpannableString(str)

        for (i in 0..keyword.size - 1) {
            var startIndex = str.indexOf(keyword[i])
            while (startIndex != -1) {
                val endIndex = startIndex + keyword[i].length
                val colorSpan =
                    if (i < 2) { ForegroundColorSpan(Color.YELLOW) } else {
                        ForegroundColorSpan(Color.RED)
                    }

                spannableString.setSpan(colorSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                startIndex = str.indexOf(keyword[i], endIndex)
            }
        }
        binding.tvWarning.text = spannableString
    }

    /*temporary Addition of Data*/
    private fun addData(date: String) {
        for (i in 1..5) {
            val food = Food(
                0 + i,
                "food" + (i + 1).toString(),
                150 + i,
                50 + i,
                i,
                20 + i,
                30 + i,
                1 + i,
                3 + i,
                3 + i,
                100 + i,
                date,
            )
            NetworkManager.postFoodData(date, food)
        }
    }
}
