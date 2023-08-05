package com.example.goodgun

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.doinglab.foodlens.sdk.FoodLens
import com.doinglab.foodlens.sdk.LanguageConfig
import com.doinglab.foodlens.sdk.RecognizeResultHandler
import com.doinglab.foodlens.sdk.errors.BaseError
import com.doinglab.foodlens.sdk.network.model.Food
import com.doinglab.foodlens.sdk.network.model.FoodPosition
import com.doinglab.foodlens.sdk.network.model.RecognitionResult
import com.example.goodgun.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFoodLens()
    }

    private fun initFoodLens() {
//        val drawable = resources.getDrawable(R.drawable.chicken)
//        val foodImage = (drawable as BitmapDrawable).bitmap
        val foodImage = BitmapFactory.decodeResource(resources, R.drawable.chicken)
        val ns = FoodLens.createNetworkService(applicationContext)

        Log.i("FoodLens", foodImage.javaClass.simpleName)

        ns.languageConfig = LanguageConfig.KO
        binding.image.setImageBitmap(foodImage)

        ns.predictMultipleFood(foodImage, object: RecognizeResultHandler{
            override fun onSuccess(result: RecognitionResult?) {
                val foodPosList: List<FoodPosition> = result!!.foodPositions // Get food positions

                for (fp in foodPosList) {
                    val foods: List<Food> = fp.foods // Get food candidates at this position
                    for (food in foods) {
                        // Print out food name at this position
                        Log.i("FoodLens", food.foodName)
                    }
                }
            }

            override fun onError(errorReason: BaseError?) {
                Log.e("FoodLens", "${errorReason!!.message}")
            }
        })
    }
}