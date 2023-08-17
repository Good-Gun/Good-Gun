package com.example.goodgun.main_function

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.*
import com.example.goodgun.ApplicationClass
import com.example.goodgun.databinding.ActivityFoodBinding
import com.example.goodgun.network.NetworkManager
import com.example.goodgun.network.model.Food
import com.example.goodgun.network.model.Nutrition
import com.example.goodgun.network.model.NutritionResponse
import com.example.goodgun.util.LoadingDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/*오늘의 정보만 다루는 액티비티*/
class FoodActivity : AppCompatActivity() {
    private lateinit var client: OkHttpClient
    private lateinit var loadingDialog: Dialog
    lateinit var binding: ActivityFoodBinding
    lateinit var todayAdapter: TodayRVAdapter
    lateinit var foodAdapter: FoodRVAdapter

    private var food_list: ArrayList<Food> = arrayListOf()
    private var recommend_list: ArrayList<String> = arrayListOf()

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
        todayAdapter = TodayRVAdapter(this, food_list, 5)
        binding.rvFoodToday.adapter = todayAdapter
        binding.rvFoodToday.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        // 간격 20으로
        val spaceDecoration = this.VerticalSpaceItemDecoration(20)
        binding.rvFoodToday.addItemDecoration(spaceDecoration)

        foodAdapter = FoodRVAdapter(this, recommend_list, 5)
        binding.rvFoodRecommend.adapter = foodAdapter
        binding.rvFoodRecommend.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        // 간격 20으로
        val foodSpaceDecoration = this.VerticalSpaceItemDecoration(5)
        binding.rvFoodRecommend.addItemDecoration(foodSpaceDecoration)
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

    @OptIn(BetaOpenAI::class)
    private fun getDataFromFirebase() {
        var response: NutritionResponse?
        val today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                response = NetworkManager.getFoodByDate(today)
                food_list.apply {
                    addAll(response!!.food_list)
                }
            }
            if (response?.food_list?.size == 0) {
                Handler(Looper.getMainLooper()).post {
                    binding.tvNoFood.visibility = View.VISIBLE
                }
            } else {
                todayAdapter.notifyItemRangeInserted(0, response!!.food_list.size)
            }

            response?.nutrition?.let {
                setNutrition(it)
                val question = it.getQuestion(1)

                if (question != null) {
                    completion = NetworkManager.callAI(question)
                    Log.d("Checking OPENAI", completion!!)
                    tokenizeString(completion!!)
                }
            }
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
    }

    @SuppressLint("NotifyDataSetChanged")
    fun tokenizeString(str: String) {
        str.split("1.", "2.", "3.", "4.", "5.", "6.", "7.").toCollection(recommend_list)
        recommend_list.removeAt(0)
        Log.d("Checking OPENAI", "${recommend_list[0]}, ${recommend_list[1]}")
        Handler(Looper.getMainLooper()).post {
            foodAdapter.notifyDataSetChanged()
        }
        /*for(i in recommend_list){
            Log.d("Checking OPENAI", "tokenized result: ${i.trim()}")
        }*/
    }
}
