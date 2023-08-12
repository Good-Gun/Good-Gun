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
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.example.goodgun.ApplicationClass
import com.example.goodgun.BuildConfig
import com.example.goodgun.Food
import com.example.goodgun.LoadingDialog
import com.example.goodgun.databinding.ActivityFoodBinding
import com.example.goodgun.firebase.FirebaseManager
import com.example.goodgun.main_function.model.Nutrition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds


/*오늘의 정보만 다루는 액티비티*/
class FoodActivity : AppCompatActivity() {
    private lateinit var client:OkHttpClient
    private lateinit var loadingDialog: Dialog
    lateinit var binding: ActivityFoodBinding
    lateinit var todayAdapter: TodayRVAdapter
    lateinit var foodAdapter: FoodRVAdapter

    private var food_list: ArrayList<Food> = arrayListOf()
    private var recommend_list:ArrayList<String> = arrayListOf()

    @OptIn(BetaOpenAI::class)
    var completion:ChatCompletion ?= null
    @OptIn(BetaOpenAI::class)
    var completions: Flow<ChatCompletionChunk> ?= null

    @OptIn(BetaOpenAI::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadingDialog = LoadingDialog(this)
        loadingDialog.show()
        val todayDate =
            LocalDateTime.now().format(DateTimeFormatter.ofPattern(" yyyy-MM-dd"))

        client = OkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

//        temporaryFillArr()
        initLayout()
        initTodayRV()
        getDataFromFirebase(todayDate)
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
            nutrition?.let {
                setNutrition(it)

                val question = it.getQuestionForFood()
                if (question != null) {
                    callAI(question)
                }
            }
        }
    }

    private fun setNutrition(nutrition: Nutrition) {
        val max = ApplicationClass.maxNutrition
        binding.apply {
            tvFoodCalorie.text = nutrition.calorie.toString() +"/"+ max.calorie
            tvFoodCarbo.text = nutrition.carbohydrates.toString() +"/"+ max.carbohydrates
            tvFoodSugar.text = nutrition.sugar.toString() +"/"+max.sugar
            tvFoodFat.text = nutrition.fat.toString() +"/"+max.fat
            tvFoodTrans.text = nutrition.trans_fat.toString() +"/"+max.trans_fat
            tvFoodSaturated.text = nutrition.saturated_fat.toString() +"/"+max.saturated_fat
            tvFoodProtein.text = nutrition.protein.toString() +"/"+max.protein
            tvFoodCholesterol.text = nutrition.cholesterol.toString() +"/"+max.cholesterol
            tvFoodProtein.text = nutrition.protein.toString() +"/"+max.protein
            tvFoodSodium.text = nutrition.sodium.toString() +"/"+max.sodium

        }
        loadingDialog.dismiss()
    }

    @OptIn(BetaOpenAI::class)
    private suspend fun callAI(question:String) = withContext(Dispatchers.IO){
        val openAI = OpenAI(
            token = BuildConfig.SAMPLE_API_KEY,
            timeout = Timeout(socket = 120.seconds),
            // additional configurations...
        )

        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.User,
//                    content = "평소에 탄수화물 섭취가 과다한 사람에게 추천할 생활 패턴을 알려줘.
                    content = question
                )
            )
        )
        completion = openAI.chatCompletion(chatCompletionRequest)
// or, as flow
        completions = openAI.chatCompletions(chatCompletionRequest)


        if(completion != null) {
            val str = completion!!.choices[0].message?.content.toString()
            Log.d("Checking OPENAI", str)
            tokenizeString(str)
        }
        else
            Log.d("Checking OPENAI", "completion null")
    }

    @SuppressLint("NotifyDataSetChanged")
    fun tokenizeString(str:String){
        str.split("1.","2.","3.","4.","5.","6.","7.").toCollection(recommend_list)
        recommend_list.removeAt(0)
        Log.d("Checking OPENAI", "${recommend_list[0]}, ${recommend_list[1]}")
        Handler(Looper.getMainLooper()).post{
            foodAdapter.notifyDataSetChanged()
        }
        /*for(i in recommend_list){
            Log.d("Checking OPENAI", "tokenized result: ${i.trim()}")
        }*/
    }
}
