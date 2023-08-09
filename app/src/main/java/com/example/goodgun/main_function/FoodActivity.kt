package com.example.goodgun.main_function

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.goodgun.Food
import com.example.goodgun.databinding.ActivityFoodBinding
import com.example.goodgun.firebase.FirebaseManager
import com.example.goodgun.main_function.model.Nutrition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Collections.addAll

class FoodActivity : AppCompatActivity() {
    lateinit var binding: ActivityFoodBinding
    lateinit var todayAdapter: TodayRVAdapter
    private var food_list: ArrayList<Food> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val todayDate =
            LocalDateTime.now().format(DateTimeFormatter.ofPattern(" yyyy-MM-dd"))

//        temporaryFillArr()
        initTodayRV()
        getDataFromFirebase(todayDate)
    }

    private fun initTodayRV() {
        todayAdapter = TodayRVAdapter(this, food_list, 5)
        binding.rvFoodToday.adapter = todayAdapter
        binding.rvFoodToday.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        // 간격 20으로
        val spaceDecoration = this.VerticalSpaceItemDecoration(20)
        binding.rvFoodToday.addItemDecoration(spaceDecoration)
    }

    inner class VerticalSpaceItemDecoration(private val verticalSpaceHeight: Int) :
        RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State,
        ) {
            outRect.bottom = verticalSpaceHeight
        }
    }

    private fun getDataFromFirebase(date: String) {
        var nutrition: Nutrition ? = null
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                nutrition = FirebaseManager.getNutritionData(date)
                food_list.apply {
                    addAll(FirebaseManager.getFoodData(date))
                }
            }
            todayAdapter.notifyItemRangeInserted(0, food_list.size)
            nutrition?.let { setNutrition(it) }
        }
    }

    private fun setNutrition(nutrition: Nutrition) {
        binding.apply {
            tvFoodCalorie.text = nutrition.calorie.toString() + "/" + "2000" + " kcal"
            tvFoodCarbo.text = nutrition.carbohydrates.toString() + "/" + "100" + " kcal"
            tvFoodSugar.text = nutrition.sugar.toString() + "/" + "100" + " kcal"
            tvFoodFat.text = nutrition.fat.toString() + "/" + "100" + " kcal"
            tvFoodTrans.text = nutrition.trans_fat.toString() + "/" + "100" + " kcal"
            tvFoodSaturated.text = nutrition.saturated_fat.toString() + "/" + "100" + " kcal"
            tvFoodProtein.text = nutrition.protein.toString() + "/" + "100" + " kcal"
            tvFoodCholesterol.text = nutrition.cholesterol.toString() + "/" + "100" + " kcal"
            tvFoodProtein.text = nutrition.protein.toString() + "/" + "100" + " kcal"
        }
    }

    private fun generateQuestion() {
    }

    /*테스트용 배열 생성을 위한 임시 함수*/
//    private fun temporaryFillArr() {
//        todayArray.apply {
//            for (i in 0..3) {
//                add(Pair(('A' + i).toString(), ('A' + i).toString()))
//            }
//        }
//    }
}
