package com.example.goodgun.main_function

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.aallam.openai.api.chat.*
import com.example.goodgun.ApplicationClass
import com.example.goodgun.databinding.ActivityFoodBinding
import com.example.goodgun.main_function.adapter.RecommendVPAdapter
import com.example.goodgun.main_function.adapter.TodayVPAdapter
import com.example.goodgun.network.NetworkManager
import com.example.goodgun.network.model.Nutrition
import com.example.goodgun.network.model.NutritionResponse
import com.example.goodgun.roomDB.FoodEntity
import com.example.goodgun.util.LoadingDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/*오늘의 정보만 다루는 액티비티*/
class FoodActivity : AppCompatActivity() {
    private lateinit var question: String
    private lateinit var loadingDialog: Dialog
    lateinit var binding: ActivityFoodBinding

    private lateinit var viewPager: ViewPager2
    private lateinit var recommendAdapter: RecommendVPAdapter
    private var recommend_list: ArrayList<String> = arrayListOf()

    private lateinit var todayVP: ViewPager2
    private lateinit var todayAdapter: TodayVPAdapter
    private var fragmentToday = mutableListOf<FoodEntity>()

    var completion: String ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadingDialog = LoadingDialog(this)
        loadingDialog.show()

        initLayout()
        initTodayRV()
        getDataFromFirebase()
    }

    private fun initLayout() {
        binding.apply {
            backBtn.setOnClickListener {
                finish()
            }
        }
    }

    private fun initTodayRV() {
        /*오늘 먹은 음식 리스트*/
        todayVP = binding.vpFoodToday
        todayAdapter = TodayVPAdapter(supportFragmentManager, lifecycle)
        todayVP.adapter = todayAdapter
        binding.indicatorFood.attachTo(todayVP)

        /*식사 추천 리스트*/
        viewPager = binding.vpFoodRecommend
        recommendAdapter = RecommendVPAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = recommendAdapter
        binding.indicator.attachTo(viewPager)
    }

    private fun getDataFromFirebase() {
        var response: NutritionResponse?
        val today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        CoroutineScope(Dispatchers.Main).launch {
            response = NetworkManager.getFoodByDate(today)
            fragmentToday.apply {
                clear()
                addAll(response!!.food_list)
            }
            todayAdapter.setFragmentFood(fragmentToday)
            if (fragmentToday.size == 0) {
                Handler(Looper.getMainLooper()).post {
                    binding.tvNoFood.visibility = View.VISIBLE
                }
            }

            response?.nutrition?.let {
                setNutrition(it)
                question = it.getQuestion(1)!!
                getSolution()
            }
        }
    }

    suspend fun getSolution() {
        completion = NetworkManager.callAI(question)
        Log.d("Checking OPENAI", completion!!)
        tokenizeString(completion!!)
    }

    private fun setNutrition(nutrition: Nutrition) {
        val max = ApplicationClass.maxNutrition
//        binding.apply {
//            tvFoodCalorie.text = nutrition.calorie.roundToInt().toString() + "/" + max.calorie.roundToInt()
//            tvFoodCarbo.text = nutrition.carbohydrates.roundToInt().toString() + "/" + max.carbohydrates.roundToInt()
//            tvFoodSugar.text = nutrition.sugar.roundToInt().toString() + "/" + max.sugar.roundToInt()
//            tvFoodFat.text = nutrition.fat.roundToInt().toString() + "/" + max.fat.roundToInt()
//            tvFoodTrans.text = nutrition.trans_fat.roundToInt().toString() + "/" + max.trans_fat.roundToInt()
//            tvFoodSaturated.text = nutrition.saturated_fat.roundToInt().toString() + "/" + max.saturated_fat.roundToInt()
//            tvFoodProtein.text = nutrition.protein.roundToInt().toString() + "/" + max.protein.roundToInt()
//            tvFoodCholesterol.text = nutrition.cholesterol.roundToInt().toString() + "/" + max.cholesterol.roundToInt()
//            tvFoodProtein.text = nutrition.protein.roundToInt().toString() + "/" + max.protein.roundToInt()
//            tvFoodSodium.text = nutrition.sodium.roundToInt().toString() + "/" + max.sodium.roundToInt()
//        }
        loadingDialog.dismiss()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun tokenizeString(str: String) {
        var flag = true
        while (flag) {
            try {
                str.split("1.", "2.", "3.", "4.", "5.").toCollection(recommend_list)
                recommend_list.removeAt(0)
                recommendAdapter.setFragmentTexts(recommend_list)
                Handler(Looper.getMainLooper()).post {
                    binding.tvWait.visibility = View.GONE
                }
                flag = false
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch {
                    getSolution()
                }
            }
        }
        /*for(i in recommend_list){
            Log.d("Checking OPENAI", "tokenized result: ${i.trim()}")
        }*/
    }
}
