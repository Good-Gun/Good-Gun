package com.example.goodgun.add_food

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.doinglab.foodlens.sdk.FoodLens
import com.doinglab.foodlens.sdk.NutritionRetrieveMode
import com.doinglab.foodlens.sdk.RecognizeResultHandler
import com.doinglab.foodlens.sdk.errors.BaseError
import com.doinglab.foodlens.sdk.network.model.Food
import com.doinglab.foodlens.sdk.network.model.RecognitionResult
import com.example.goodgun.R
import com.example.goodgun.databinding.ActivityFoodPhotoCheckBinding
import com.example.goodgun.roomDB.DatabaseManager
import com.example.goodgun.roomDB.FoodDatabase
import com.example.goodgun.roomDB.FoodEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FoodPhotoCheck : AppCompatActivity() {
    private val binding: ActivityFoodPhotoCheckBinding by lazy {
        ActivityFoodPhotoCheckBinding.inflate(layoutInflater)
    }
    private lateinit var adapter: FoodListAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var roomdb: FoodDatabase

    private val data = ArrayList<FoodEntity>()
    private val isChecked = ArrayList<Boolean>()
    private var currentUser: FirebaseUser? = null
    private var imageUriString: String? = null
    private var userid = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initRoomDB()
        requestFoodLens()
    }

    private fun initRoomDB() {
        auth = Firebase.auth
        currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this@FoodPhotoCheck, "유효하지 않은 유저입니다.", Toast.LENGTH_SHORT).show()
        } else {
            userid = currentUser!!.uid
        }
        roomdb = DatabaseManager.getDatabaseInstance(userid, applicationContext)
    }

    private fun initLayout() {
        binding.apply {
            recyclerView.layoutManager = LinearLayoutManager(this@FoodPhotoCheck, LinearLayoutManager.VERTICAL, false)
            Log.i("FoodLens", data.size.toString())
            adapter = FoodListAdapter(data, isChecked)
            adapter.itemClickListener = object : FoodListAdapter.OnItemClickListener {
                override fun onItemClick(holder: FoodListAdapter.ViewHolder, position: Int) {
                    isChecked[position] = !isChecked[position]
                }
            }
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()

            okButton.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    for (i in 0 until data.size) if (isChecked[i]) roomdb.foodDao().saveFood(data[i])
                }
                finish()
                startActivity(Intent(this@FoodPhotoCheck, ScanInfomation::class.java))
            }
            cancelButton.setOnClickListener {
                finish()
            }

            textButton.setOnClickListener {
                startActivity(Intent(this@FoodPhotoCheck, ScanInfomation::class.java))
                finish()
            }
        }
    }

    private fun requestFoodLens() {
        imageUriString = intent.getStringExtra("food")
        val bitmap = loadBitmapFromUri(Uri.parse(imageUriString))
        val dialog = Dialog(this, R.style.CustomDialogTheme)
        dialog.setContentView(R.layout.waiting_dialog)
        dialog.show()

        val ns = FoodLens.createNetworkService(this)
        ns.nutritionRetrieveMode = NutritionRetrieveMode.ALL_NUTRITION
        ns.predictMultipleFood(
            bitmap,
            object : RecognizeResultHandler {
                override fun onSuccess(result: RecognitionResult) {
                    val foodPosList = result.foodPositions
                    var total_count = 0
                    for (fp in foodPosList) {
                        val foods: List<Food> = fp.foods
                        total_count += foods.size
                        for (food in foods) {
                            Log.i("FoodLens", food.foodName)
                            Log.i("FoodLens", food.nutrition.calories.toString())
                            val nutrition = food.nutrition
                            nutrition.apply {
                                val temp = FoodEntity(food.foodName, calories.toDouble(), carbonHydrate.toDouble(), sugar.toDouble(), protein.toDouble(), fat.toDouble(), transFat.toDouble(), saturatedFat.toDouble(), cholesterol.toDouble(), sodium.toDouble(), true)
                                data.add(temp)
                            }
                            isChecked.add(false)
                        }
                    }
                    dialog.dismiss()
                    initLayout()
                }
                override fun onError(errorReason: BaseError) {
                    Log.e("FoodLens", errorReason.message)
                }
            },
        )
    }

    private fun loadBitmapFromUri(uri: Uri): Bitmap? {
        val inputStream = contentResolver.openInputStream(uri)
        return BitmapFactory.decodeStream(inputStream)
    }
}
