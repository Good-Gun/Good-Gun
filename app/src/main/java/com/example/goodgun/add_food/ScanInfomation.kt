package com.example.goodgun.add_food

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.aallam.openai.api.BetaOpenAI
import com.doinglab.foodlens.sdk.ui.network.models.Nutrition
import com.example.goodgun.MainActivity
import com.example.goodgun.R
import com.example.goodgun.add_food.direct_add.DirectInputFragment
import com.example.goodgun.databinding.ActivityScanInfomationBinding
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
        binding.dbCheck.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val tmp: List<FoodEntity> = roomdb.foodDao().getAll()
                val message =
                    tmp.joinToString("\n") { "FoodEntity(id=${it.id}, name=${it.name}), calory=${it.calory})" }
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ScanInfomation, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.directAdd.setOnClickListener {
            model.reset()
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
                    calory.text = sumfood.calory.toString()
                    carbohydrates.text = sumfood.carbohydrates.toString()
                    sugar.text = sumfood.sugar.toString()
                    protein.text = sumfood.protein.toString()
                    fat.text = sumfood.fat.toString()
                    transFat.text = sumfood.trans_fat.toString()
                    saturatedFat.text = sumfood.saturated_fat.toString()
                    cholesterol.text = sumfood.cholesterol.toString()
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
            for (food in allfood) {
                tmpcalory += food.calory
                tmpcarbohydrates += food.carbohydrates
                tmpsugar += food.sugar
                tmpprotein += food.protein
                tmpfat += food.fat
                tmptrans_fat += food.trans_fat
                tmpsaturated_fat += food.saturated_fat
                tmpcholesterol += food.cholesterol
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
                }
            }
            roomdb.foodDao().saveFood(sumfood)
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
                    database.child("user_list").child(userid).child(food.registerDate)
                        .child(food.name)
                        .setValue(food)
                }
                roomdb.foodDao().deleteAll()
                roomdb.foodDao().saveFood(FoodEntity())

                val nutrition = withContext(Dispatchers.IO) {
                    val nutrition = NetworkManager.getNutritionData(
                        LocalDateTime.now().minusWeeks(1)
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    )
                    nutrition
                }

                Log.d("Managing Network from ScanInfo", "${nutrition.calorie}, ${Nutrition.ColumnInfo.sugar}")

                val question = nutrition.getQuestion(2)
                val answer = if (question != null) {
                    NetworkManager.callAI(question)
                } else {
                    null
                }

                Log.d("Managing Network from ScanInfo", answer!!)
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
                adapter = FoodAddAdapter(tmpdata)

                binding.recyclerView.layoutManager =
                    LinearLayoutManager(this@ScanInfomation, LinearLayoutManager.VERTICAL, false)

                adapter.itemadd = object : FoodAddAdapter.OnItemClickListener {
                    override fun onItemClick(data: FoodEntity, position: Int) {
                        GlobalScope.launch(Dispatchers.IO) {
                            roomdb.foodDao().saveFood(data)
                            updateSumFoodEntity()
                        }
                        binding.recyclerView.findViewHolderForAdapterPosition(position)?.itemView?.findViewById<ImageButton>(
                            R.id.food_add,
                        )?.visibility = View.GONE
                    }
                }
                adapter.itemdelete = object : FoodAddAdapter.OnItemClickListener {
                    // 2번째 onclick 이벤트리스너
                    override fun onItemClick(data: FoodEntity, position: Int) {
                        tmpdata.removeAt(position)
                        GlobalScope.launch(Dispatchers.IO) {
                            roomdb.foodDao().deleteFood(data.name, data.registerDate)
                            updateSumFoodEntity()
                        }
                        val itemView = binding.recyclerView.findViewHolderForAdapterPosition(position)?.itemView
                        itemView?.findViewById<ImageButton>(R.id.food_add)?.visibility = View.VISIBLE
                        adapter.notifyItemRemoved(position)
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
            adapter.notifyDataSetChanged()
        }
    }
}
