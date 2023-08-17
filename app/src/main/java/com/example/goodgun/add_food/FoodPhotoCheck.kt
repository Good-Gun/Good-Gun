package com.example.goodgun.add_food

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import com.example.goodgun.roomDB.FoodEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class FoodPhotoCheck : AppCompatActivity() {
    val binding: ActivityFoodPhotoCheckBinding by lazy {
        ActivityFoodPhotoCheckBinding.inflate(layoutInflater)
    }
    lateinit var adapter: FoodListAdapter
    val data = ArrayList<FoodEntity>()
    var scope = CoroutineScope(Dispatchers.IO)
    var imageUriString: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        requestFoodLens()
    }

    private fun initLayout() {
        binding.apply {
            recyclerView.layoutManager = LinearLayoutManager(this@FoodPhotoCheck, LinearLayoutManager.VERTICAL, false)
            Log.i("FoodLens", data.size.toString())
            adapter = FoodListAdapter(data)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
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
                    for (fp in foodPosList) {
                        val foods: List<Food> = fp.foods
                        for (food in foods) {
                            Log.i("FoodLens", food.foodName)
                            Log.i("FoodLens", food.nutrition.calories.toString())
                            val temp = FoodEntity(food.foodName)
                            val nutrition = food.nutrition
                            temp.calory = nutrition.calories.toDouble()
                            temp.carbohydrates = nutrition.carbonHydrate.toDouble()
                            temp.sugar = nutrition.sugar.toDouble()
                            temp.protein = nutrition.protein.toDouble()
                            temp.fat = nutrition.fat.toDouble()
                            temp.trans_fat = nutrition.transFat.toDouble()
                            temp.saturated_fat = nutrition.saturatedFat.toDouble()
                            temp.cholesterol = nutrition.cholesterol.toDouble()
                            data.add(temp)
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
