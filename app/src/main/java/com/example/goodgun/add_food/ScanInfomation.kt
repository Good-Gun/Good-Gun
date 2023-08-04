package com.example.goodgun.add_food

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.goodgun.Food
import com.example.goodgun.MainActivity
import com.example.goodgun.R
import com.example.goodgun.User
import com.example.goodgun.databinding.ActivityScanInfomationBinding
import com.example.goodgun.roomDB.DatabaseManager
import com.example.goodgun.roomDB.FoodDatabase
import com.example.goodgun.roomDB.FoodEntity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScanInfomation : AppCompatActivity() {

    lateinit var binding: ActivityScanInfomationBinding
    var tmpdata:ArrayList<FoodEntity> = ArrayList()
    lateinit var adapter: FoodAddAdapter
    lateinit var roomdb: FoodDatabase
    lateinit var database: DatabaseReference

    // 임시 유저 아이디 - 유저 토큰이나 아이디 사용
    var userid = "2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanInfomationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // db 연결
        roomdb = DatabaseManager.getDatabaseInstance(userid, applicationContext)
        // 임시 db 초기화 - 실행할 때마다 초기화되는 상태임
        GlobalScope.launch(Dispatchers.IO) {
            roomdb.foodDao().deleteAll()
            roomdb.foodDao().saveFood(FoodEntity())
            // 영양소 합계 저장할 foodentity 생성
        }

        database = Firebase.database("https://goodgun-4740f-default-rtdb.firebaseio.com/").reference

        initBtn()
        initRecyclerView()

        // 임시 룸DB 확인
        binding.directAdd.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val tmp: List<FoodEntity> = roomdb.foodDao().getAll()
                val message = tmp.joinToString("\n") { "FoodEntity(id=${it.id}, name=${it.name}), calory=${it.calory})" }
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ScanInfomation, message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        init()
    }

    private fun init(){

        updateSum()
    }

    private fun updateSum() {
        GlobalScope.launch(Dispatchers.IO) {
            val sumfood = roomdb.foodDao().getSumFood()
            withContext(Dispatchers.Main) {
                binding.apply {
                    calory.text=sumfood.calory.toString()
                    carbohydrates.text=sumfood.carbohydrates.toString()
                    sugar.text=sumfood.sugar.toString()
                    protein.text=sumfood.protein.toString()
                    fat.text=sumfood.fat.toString()
                    transFat.text=sumfood.trans_fat.toString()
                    saturatedFat.text=sumfood.saturated_fat.toString()
                    cholesterol.text=sumfood.cholesterol.toString()
                }
            }

        }
    }

    private fun updateSumFoodEntity(addFood: FoodEntity){
        GlobalScope.launch(Dispatchers.IO) {
            val sumfood = roomdb.foodDao().getSumFood()
            withContext(Dispatchers.Main) {
                binding.apply {
                    sumfood.calory += addFood.calory
                    sumfood.carbohydrates += addFood.carbohydrates
                    sumfood.sugar += addFood.sugar
                    sumfood.protein += addFood.protein
                    sumfood.fat += addFood.fat
                    sumfood.trans_fat += addFood.trans_fat
                    sumfood.saturated_fat += addFood.saturated_fat
                    sumfood.cholesterol += addFood.cholesterol
                }
            }
            roomdb.foodDao().saveFood(sumfood)
            updateSum()
        }
    }

    private fun initBtn() {
        binding.registerFoods.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val foods: List<FoodEntity> = roomdb.foodDao().getAll()
                for (food in foods) {
                    database.child("user_list").child(userid).child("food")
                        .setValue(food)
                }
                roomdb.foodDao().deleteAll()
                roomdb.foodDao().saveFood(FoodEntity())
                // 영양소 합계 저장할 foodentity 생성
            }
            startActivity(Intent (this, MainActivity::class.java))
            finish()
        }
    }

    private fun initRecyclerView() {
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        tmpdata.apply {
            add(FoodEntity("data1"))
            add(FoodEntity("data2"))
            add(FoodEntity("data3"))
            add(FoodEntity("data4"))
            add(FoodEntity("data5"))
        }

        adapter = FoodAddAdapter(tmpdata)
        adapter.itemadd = object :FoodAddAdapter.OnItemClickListener{
            override fun onItemClick(data: FoodEntity, position: Int) {
                GlobalScope.launch(Dispatchers.IO) {
                    roomdb.foodDao().saveFood(data)
                    updateSumFoodEntity(data)
                }
                binding.recyclerView.findViewHolderForAdapterPosition(position)?.itemView?.findViewById<ImageButton>(R.id.food_add)?.visibility = View.GONE
            }
        }
        adapter.itemdelete=object :FoodAddAdapter.OnItemClickListener{
            // 2번째 onclick 이벤트리스너
            override fun onItemClick(data: FoodEntity, position: Int) {
                tmpdata.removeAt(position)
                GlobalScope.launch(Dispatchers.IO) {
                    roomdb.foodDao().deleteFood(data.name, data.registerDate)
                }
                adapter.notifyItemRemoved(position)
            }
        }
        binding.recyclerView.adapter = adapter
    }
}