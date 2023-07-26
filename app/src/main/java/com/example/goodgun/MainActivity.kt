package com.example.goodgun

import android.graphics.BitmapFactory
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.JsonReader
import android.util.Log
import com.doinglab.foodlens.sdk.FoodLens
import com.doinglab.foodlens.sdk.LanguageConfig
import com.doinglab.foodlens.sdk.NutritionRetrieveMode
import com.doinglab.foodlens.sdk.RecognizeResultHandler
import com.doinglab.foodlens.sdk.errors.BaseError
import com.doinglab.foodlens.sdk.network.model.Food
import com.doinglab.foodlens.sdk.network.model.FoodPosition
import com.doinglab.foodlens.sdk.network.model.RecognitionResult
import com.doinglab.foodlens.sdk.ui.util.FoodLensDBManager.init
import com.example.goodgun.databinding.ActivityMainBinding
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFoodLens()
    }

    private fun initFoodLens() {
        val foodImage = BitmapFactory.decodeResource(resources, R.drawable.chicken)
        val ns = FoodLens.createNetworkService(applicationContext)

        ns.languageConfig = LanguageConfig.KO
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
                Log.e("FoodLens", "${errorReason!!.message} dfdalksjdf")
            }

        })
    }
}