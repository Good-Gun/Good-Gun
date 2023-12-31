package com.example.goodgun.add_food

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.aallam.openai.api.BetaOpenAI
import com.doinglab.foodlens.sdk.ui.network.models.Nutrition
import com.example.goodgun.ApplicationClass
import com.example.goodgun.R
import com.example.goodgun.add_food.direct_add.DirectInputFragment
import com.example.goodgun.databinding.ActivityScanInfomationBinding
import com.example.goodgun.main.MainActivity
import com.example.goodgun.main_function.FoodModifyDialog
import com.example.goodgun.network.NetworkManager
import com.example.goodgun.roomDB.DatabaseManager
import com.example.goodgun.roomDB.FoodDatabase
import com.example.goodgun.roomDB.FoodEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

class ScanInfomation : AppCompatActivity() {

    lateinit var binding: ActivityScanInfomationBinding
    var tmpdata: MutableList<FoodEntity> = mutableListOf()
    lateinit var adapter: FoodAddAdapter
    lateinit var roomdb: FoodDatabase
    lateinit var database: DatabaseReference

    private lateinit var auth: FirebaseAuth
    var currentUser: FirebaseUser? = null
    var userid: String = ""

    val model: FoodViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = getColor(R.color.theme1)
        binding = ActivityScanInfomationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this@ScanInfomation, "유효하지 않은 유저입니다.", Toast.LENGTH_SHORT).show()
        } else {
            userid = currentUser!!.uid
        }

        // db 연결
        roomdb = DatabaseManager.getDatabaseInstance(userid, applicationContext)

        database = Firebase.database("https://goodgun-4740f-default-rtdb.firebaseio.com/").reference

        initBtn()
        initRecyclerView()
        initDirectAdd()
        init()
    }

    private fun initDirectAdd() {
        // 임시 룸DB 확인
//        binding.dbCheck.setOnClickListener {
//            GlobalScope.launch(Dispatchers.IO) {
//                val tmp: List<FoodEntity> = roomdb.foodDao().getAll()
//                val message =
//                    tmp.joinToString("\n") { "FoodEntity(id=${it.id}, name=${it.name}), amount=${it.amount})" }
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(this@ScanInfomation, message, Toast.LENGTH_SHORT).show()
//                    Log.i("check",message)
//                }
//            }
//        }
        binding.directAdd.setOnClickListener {
            model.reset()
            model.setuserid(userid)
            val dialog = DirectInputFragment()
            dialog.show(supportFragmentManager, "DirectInputFragment")
        }
    }

    private fun init() {
    }

    private fun updateSum() {
        GlobalScope.launch(Dispatchers.IO) {
            val sumfood = roomdb.foodDao().getSumFood()
            withContext(Dispatchers.Main) {
                binding.apply {
                    calory.text = ((sumfood.calory!! * 10.0).roundToInt() / 10.0).toString()
                    carbohydrates.text = ((sumfood.carbohydrates!! * 10.0).roundToInt() / 10.0).toString()
                    sugar.text = ((sumfood.sugar!! * 10.0).roundToInt() / 10.0).toString()
                    protein.text = ((sumfood.protein!! * 10.0).roundToInt() / 10.0).toString()
                    fat.text = ((sumfood.fat!! * 10.0).roundToInt() / 10.0).toString()
                    transFat.text = ((sumfood.trans_fat!! * 10.0).roundToInt() / 10.0).toString()
                    saturatedFat.text = ((sumfood.saturated_fat!! * 10.0).roundToInt() / 10.0).toString()
                    cholesterol.text = ((sumfood.cholesterol!! * 10.0).roundToInt() / 10.0).toString()
                    sodium.text = ((sumfood.sodium!! * 10.0).roundToInt() / 10.0).toString()
                }
            }
        }
    }

    private fun updateSumFoodEntity() {
        GlobalScope.launch(Dispatchers.IO) {
            val sumfood = roomdb.foodDao().getSumFood()
            val allfood = roomdb.foodDao().getAll()

            var tmpcalory = 0.0
            var tmpcarbohydrates = 0.0
            var tmpsugar = 0.0
            var tmpprotein = 0.0
            var tmpfat = 0.0
            var tmptrans_fat = 0.0
            var tmpsaturated_fat = 0.0
            var tmpcholesterol = 0.0
            var tmpsodium = 0.0
            for (food in allfood) {
                val amt = food.amount
                tmpcalory += food.calory?.times(amt!!) ?: 0.0
                tmpcarbohydrates += food.carbohydrates?.times(amt!!) ?: 0.0
                tmpsugar += food.sugar?.times(amt!!) ?: 0.0
                tmpprotein += food.protein?.times(amt!!) ?: 0.0
                tmpfat += food.fat?.times(amt!!) ?: 0.0
                tmptrans_fat += food.trans_fat?.times(amt!!) ?: 0.0
                tmpsaturated_fat += food.saturated_fat?.times(amt!!) ?: 0.0
                tmpcholesterol += food.cholesterol?.times(amt!!) ?: 0.0
                tmpsodium += food.sodium?.times(amt!!) ?: 0.0
            }

            withContext(Dispatchers.Main) {
                binding.apply {
                    sumfood.calory = tmpcalory
                    sumfood.carbohydrates = tmpcarbohydrates
                    sumfood.sugar = tmpsugar
                    sumfood.protein = tmpprotein
                    sumfood.fat = tmpfat
                    sumfood.trans_fat = tmptrans_fat
                    sumfood.saturated_fat = tmpsaturated_fat
                    sumfood.cholesterol = tmpcholesterol
                    sumfood.sodium = tmpsodium
                }
            }
            roomdb.foodDao().updateFood(
                sumfood.name,
                sumfood.calory!!,
                sumfood.carbohydrates!!,
                sumfood.sugar!!,
                sumfood.protein!!,
                sumfood.fat!!,
                sumfood.trans_fat!!,
                sumfood.saturated_fat!!,
                sumfood.cholesterol!!,
                sumfood.sodium!!,
                sumfood.registerDate,
                sumfood.registerTime,
                sumfood.inroomdb,
                sumfood.amount!!,
            )
            updateSum()
        }
    }

    @OptIn(BetaOpenAI::class)
    private fun initBtn() {
        binding.registerFoods.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                roomdb.foodDao().deleteSumFood()
                val foods: List<FoodEntity> = roomdb.foodDao().getAll()
                for (food in foods) {
                    database.child("user_list").child(userid).child("food_list").child(food.registerDate)
                        .child(food.name)
                        .setValue(food)
                }
                roomdb.foodDao().deleteAll()
                roomdb.foodDao().saveFood(FoodEntity("is_sum_entity"))

                val nutrition = NetworkManager.getNutritionData(
                    LocalDateTime.now().minusWeeks(1)
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                )

                Log.d("Managing Network from ScanInfo", "${nutrition.calorie}, ${Nutrition.ColumnInfo.sugar}")

                val question = nutrition.getQuestion(2)
                val answer = if (question != null) {
                    NetworkManager.callAI(question)
                } else {
                    null
                }

                Log.d("Managing Network from ScanInfo", answer!!)
                ApplicationClass.pref_edit.putString("solution", answer).apply()
                // roomdb.foodDao().saveSolution(Solution(answer))
                // 영양소 합계 저장할 foodentity 생성
            }
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        binding.backBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun initRecyclerView() {
        GlobalScope.launch(Dispatchers.IO) {
            val tmpentity = roomdb.foodDao().hasSumFood()
            Log.d("SumFood", "name: $tmpentity")
            if (roomdb.foodDao().hasSumFood() == false) {
                roomdb.foodDao().saveFood(FoodEntity("is_sum_entity"))
            }
            tmpdata = roomdb.foodDao().getAll().toMutableList()
            withContext(Dispatchers.Main) {
                adapter = FoodAddAdapter(tmpdata, this@ScanInfomation)
                binding.recyclerView.layoutManager =
                    LinearLayoutManager(this@ScanInfomation, LinearLayoutManager.VERTICAL, false)

//                adapter.itemadd = object : FoodAddAdapter.OnItemClickListener {
//                    override fun onItemClick(data: FoodEntity, position: Int, amount: Double) {
//                        GlobalScope.launch(Dispatchers.IO) {
//                            data.inroomdb = true
//
//                            data.calory = data.calory?.times(amount)
//                            data.carbohydrates = data.carbohydrates?.times(amount)
//                            data.sugar = data.sugar?.times(amount)
//                            data.protein = data.protein?.times(amount)
//                            data.fat = data.fat?.times(amount)
//                            data.trans_fat = data.trans_fat?.times(amount)
//                            data.saturated_fat = data.saturated_fat?.times(amount)
//                            data.cholesterol = data.cholesterol?.times(amount)
//
//                            roomdb.foodDao().saveFood(data)
//                            updateSumFoodEntity()
//                        }
//                    }
//                }
                adapter.itemedit = object : FoodAddAdapter.OnEditClickListener {
                    override fun onEditClick(data: FoodEntity, position: Int) {
                        val dialog = FoodModifyDialog(data)
                        dialog.show(supportFragmentManager, "FoodModifyDialog")
                    }
                }
                adapter.itemdelete = object : FoodAddAdapter.OnItemClickListener {
                    // 2번째 onclick 이벤트리스너
                    override fun onItemClick(data: FoodEntity, position: Int, amount: Double) {
                        tmpdata.removeAt(position)
                        GlobalScope.launch(Dispatchers.IO) {
                            roomdb.foodDao().deleteFood(data.name, data.registerDate)
                            withContext(Dispatchers.Main) {
                                updateSumFoodEntity()
                            }
                        }
                        adapter.notifyItemRemoved(position)
                    }
                }
                adapter.itemchange = object : FoodAddAdapter.OnTextChangeListener {
                    override fun onTextChanged(data: FoodEntity, position: Int, amount: Double) {
                        val formattedValue = String.format("%.2f", amount).toDouble()
                        if (data.amount != formattedValue) {
                            data.amount = formattedValue
                            GlobalScope.launch(Dispatchers.IO) {
                                roomdb.foodDao().updateFood(
                                    data.name,
                                    data.calory!!,
                                    data.carbohydrates!!,
                                    data.sugar!!,
                                    data.protein!!,
                                    data.fat!!,
                                    data.trans_fat!!,
                                    data.saturated_fat!!,
                                    data.cholesterol!!,
                                    data.sodium!!,
                                    data.registerDate,
                                    data.registerTime,
                                    data.inroomdb,
                                    data.amount!!,
                                )
                                withContext(Dispatchers.Main) {
                                    updateSumFoodEntity()
                                }
                            }
                        }
                    }
                }
                binding.recyclerView.adapter = adapter
                updateSumFoodEntity()
            }
        }
    }

    // 다이얼로그 닫힐 때 실행되는 함수
    fun onDialogDissmissed() {
        if (!model.is_blank()) {
            tmpdata.add(model.getfood())
            updateSumFoodEntity()
            adapter.notifyDataSetChanged()
        }
    }

    fun onUpdateFood() {
        updateSumFoodEntity()
    }
}
