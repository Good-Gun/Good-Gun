package com.example.goodgun.firebase

import android.util.Log
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.doinglab.foodlens.sdk.ui.util.UnitTokenizer.tokenizeString
import com.example.goodgun.ApplicationClass
import com.example.goodgun.BuildConfig
import com.example.goodgun.Food
import com.example.goodgun.User
import com.example.goodgun.main_function.model.Nutrition
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import kotlin.time.Duration.Companion.seconds

object NetworkManager {
    private val userId = "MhAk3L1JdrcV2bnoCkvKq2vCnD02"
    private val database = FirebaseDatabase.getInstance()

    /*firebase에서 date부터 오늘까지의 음식 정보들 가져오기*/
    suspend fun getFoodData(date: String): List<Food> = withContext(Dispatchers.IO) {
        val foodList: MutableList<Food> = mutableListOf<Food>()
        val datesRef: DatabaseReference =
            database.getReference("user_list").child(userId).child("food")

        val outerSnapshot: DataSnapshot = datesRef.get().await()
        for (dateSnapshot in outerSnapshot.children) {
            if (dateSnapshot.value != null) {
                Log.d(
                    "Firebase Communication",

                    "key ${dateSnapshot.key!!}"
                )

                val date1 = LocalDate.parse(dateSnapshot.key.toString().trim())
                val date2 = LocalDate.parse(date.trim())

                if (date1 >= date2) {
                    val innerSnapshot = datesRef.child(dateSnapshot.key!!).get().await()
                    for (foodSnapshot in innerSnapshot.children) {
                        val food = foodSnapshot.getValue(Food::class.java)!!
                        Log.d(
                            "Firebase Communication",
                            "Adding food: ${food.name}, regDate: ${food.registerDate}"
                        )
                        foodList.add(food)
                    }
                }
            }
        }
        foodList
    }

    /*firebase에서 date부터 오늘까지의 음식들의 영양정보 총합 가져오기*/

    suspend fun getNutritionData(date: String): Nutrition = withContext(Dispatchers.IO) {
        val nutrition = Nutrition()
        var days = 0

        val datesRef: DatabaseReference =
            database.getReference("user_list").child(userId).child("food")

        val outerSnapshot: DataSnapshot = datesRef.get().await()
        for (dateSnapshot in outerSnapshot.children) {
            if (dateSnapshot.value != null) {
                Log.d(
                    "Firebase Communication",
                    "key ${dateSnapshot.key!!}"

                )

                val date1 = LocalDate.parse(dateSnapshot.key.toString().trim())
                val date2 = LocalDate.parse(date.trim())

                if (date1 >= date2) {
                    days++
                    val innerSnapshot = datesRef.child(dateSnapshot.key!!).get().await()
                    for (foodSnapshot in innerSnapshot.children) {
                        val food = foodSnapshot.getValue(Food::class.java)!!
                        Log.d(
                            "Firebase Communication",

                            "Adding food: ${food.name}, regDate: ${food.registerDate}"
                        )
                        nutrition.apply {
                            calorie += food.calorie
                            carbohydrates += food.carbohydrates
                            fat += food.fat
                            saturated_fat += food.saturated_fat
                            trans_fat += food.trans_fat
                            cholesterol += food.cholesterol
                            protein += food.protein
                            sodium += food.sodium
                            sugar += food.sugar
                        }
                    }
                }
            }
        }
        if (days != 0) {
            nutrition.apply {
                calorie /= days
                carbohydrates /= days
                fat /= days
                saturated_fat /= days
                trans_fat /= days
                cholesterol /= days
                protein /= days
                sodium /= days
                sugar /= days
            }
        }
        nutrition
    }

    /*firebase에서 date에 등록된 음식들의 영양정보 총합 가져오기*/
    suspend fun getDayNutrition(date: String): Nutrition = withContext(Dispatchers.IO) {
        val nutrition = Nutrition()

        val datesRef: DatabaseReference =
            database.getReference("user_list").child(userId).child("food").child(date.trim())

        val dataSnapshot: DataSnapshot = datesRef.get().await()
        for (snapshot in dataSnapshot.children) {
            Log.d("Firebase Communication", "in day Nutrition, ${snapshot.key}")
            val food = snapshot.getValue(Food::class.java)!!
            nutrition.apply {
                calorie += food.calorie
                carbohydrates += food.carbohydrates
                fat += food.fat
                saturated_fat += food.saturated_fat
                trans_fat += food.trans_fat
                cholesterol += food.cholesterol
                protein += food.protein
                sodium += food.sodium
                sugar += food.sugar
            }
        }
        nutrition
    }

    suspend fun getUserData(): User = withContext(Dispatchers.IO) {
        val dataRef: DatabaseReference =
            database.getReference("user_list").child(ApplicationClass.uid)
        val dataSnapshot: DataSnapshot = dataRef.get().await()
        val user = dataSnapshot.getValue(User::class.java)!!

        user
    }

    /*임시로 만들어둔 음식 등록용 함수*/
    fun postFoodData(date: String, food: Food) {
        val foodRef =
            FirebaseDatabase.getInstance().getReference("user_list").child(userId).child("food").child(date.trim()).push()
        foodRef.setValue(food)
            .addOnSuccessListener {
                // 성공적으로 데이터가 저장된 경우 실행될 코드
                Log.d("Firebase Communication", "Data Added Successfully: $date, ${food.name}")
            }
            .addOnFailureListener {
                Log.d("Firebase Communication", "Data Add Failed: $date, ${food.name}")
            }
    }

    @OptIn(BetaOpenAI::class)
    suspend fun callAI(question: String): ChatCompletion = withContext(Dispatchers.IO) {
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
                    content = question
                )
            )
        )
        val completion = openAI.chatCompletion(chatCompletionRequest)

        val str = completion.choices[0].message?.content.toString()
        Log.d("Checking OPENAI", str)
        tokenizeString(str)

        completion
    }
}
