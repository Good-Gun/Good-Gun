package com.example.goodgun.main_function

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.goodgun.ApplicationClass
import com.example.goodgun.R
import com.example.goodgun.databinding.ActivitySolutionBinding
import com.example.goodgun.network.NetworkManager
import com.example.goodgun.network.model.Nutrition
import com.example.goodgun.roomDB.DatabaseManager
import com.example.goodgun.roomDB.FoodDatabase
import com.example.goodgun.util.LoadingDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SolutionActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: SolutionVPAdapter
    private lateinit var binding: ActivitySolutionBinding
    private lateinit var loadingDialog: Dialog
    private val fragmentTexts = mutableListOf<String>()
    private var response: String = ""
    lateinit var nutrition: Nutrition

    lateinit var roomdb: FoodDatabase
    private lateinit var auth: FirebaseAuth
    var currentUser: FirebaseUser? = null
    var userid: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySolutionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this@SolutionActivity, "유효하지 않은 유저입니다.", Toast.LENGTH_SHORT).show()
        } else {
            userid = currentUser!!.uid
        }
        roomdb = DatabaseManager.getDatabaseInstance(userid, applicationContext)
        loadingDialog = LoadingDialog(this)
        loadingDialog.show()
        val statusBarColor = getColor(R.color.red) // 원하는 색상 리소스로 변경
        window.statusBarColor = statusBarColor

        initLayout()
        // initFr()
        init()
    }
    fun initLayout() {
        viewPager = binding.vpSolution
        adapter = SolutionVPAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = adapter
        binding.indicator.attachTo(viewPager)
        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    fun init() {
        CoroutineScope(Dispatchers.Main).launch {
            /*val solution = roomdb.foodDao().getSolution()
            if(solution.str.isBlank()){
                fragmentTexts.add("솔루션이 준비되지 않았습니다:음식을 등록하고 드신 음식을 기반으로 건강 솔루션을 받아보세요")
                adapter.setFragmentTexts(fragmentTexts)
                loadingDialog.dismiss()
            } else{
                tokenizeString(solution.str)
                adapter.setFragmentTexts(fragmentTexts)
                loadingDialog.dismiss()
            }*/
            nutrition = NetworkManager.getNutritionData(LocalDateTime.now().minusDays(3).format(
                DateTimeFormatter.ofPattern(" yyyy-MM-dd")))
            setViewColor()

            val solution = ApplicationClass.sharedPreferences.getString("solution", null)
            if (solution.isNullOrBlank()) {
                fragmentTexts.add("솔루션이 준비되지 않았습니다:음식을 등록하고 드신 음식을 기반으로 건강 솔루션을 받아보세요")
                adapter.setFragmentTexts(fragmentTexts)
                loadingDialog.dismiss()
            } else {
                tokenizeString(solution)
                adapter.setFragmentTexts(fragmentTexts)



                loadingDialog.dismiss()
            }
        }
    }



    @SuppressLint("NotifyDataSetChanged")
    fun tokenizeString(str: String) {
        str.split("1.", "2.", "3.", "4.", "5.").toCollection(fragmentTexts)
        fragmentTexts.removeAt(0)
        Log.d("Checking OPENAI", "Hi from SolutionActivity: $str")
    }

    private fun setViewColor() {
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
            when (nutrition.calculateNutrientIntake(arr2[i])) {
                2 -> {
                    arr4[i].text = "과다"
                    arr4[i].backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.red, null))
                }
                1 -> {
                    arr4[i].text = "주의"
                    arr4[i].backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.yellow, null))
                }
                0 -> {
                    arr4[i].text = "정상"
                    arr4[i].backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.green, null))
                }
                -1 -> {
                    arr4[i].text = "주의"
                    arr4[i].backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.yellow, null))
                }
                -2 -> {
                    arr4[i].text = "부족"
                    arr4[i].backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.red, null))
                }
            }
        }
    }

}
