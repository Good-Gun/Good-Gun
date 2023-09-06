package com.example.goodgun.network

import android.util.Log
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.example.goodgun.ApplicationClass
import com.example.goodgun.BuildConfig
import com.example.goodgun.network.model.Nutrition
import com.example.goodgun.network.model.NutritionResponse
import com.example.goodgun.network.model.User
import com.example.goodgun.roomDB.FoodEntity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import kotlin.time.Duration.Companion.seconds

object NetworkManager : NetworkInterface {
    private val userId = ApplicationClass.uid
    private val database = FirebaseDatabase.getInstance()
    private val path_user = "user_list"
    private val path_food = "food_list"

    /*firebase에서 date부터 오늘까지의 음식 정보들 가져오기*/
    override suspend fun getFoodByDate(date: String): NutritionResponse = withContext(Dispatchers.IO) {
        val response = NutritionResponse()
        val datesRef: DatabaseReference =
            database.getReference(path_user).child(userId).child(path_food)
        val outerSnapshot: DataSnapshot = datesRef.get().await()
        for (dateSnapshot in outerSnapshot.children) {
            if (dateSnapshot.value != null) {
                val date1 = LocalDate.parse(dateSnapshot.key.toString().trim()) // 파이어베이스에 저장된 날짜
                val date2 = LocalDate.parse(date.trim()) // 찾는 날짜
                if (date1 == date2) {
                    val innerSnapshot = datesRef.child(dateSnapshot.key!!).get().await()
                    for (foodSnapshot in innerSnapshot.children) {
                        val food = foodSnapshot.getValue(FoodEntity::class.java)!!
                        Log.d(
                            "Firebase Communication",
                            "Adding food: ${food.name}, regDate: ${food.registerDate}",
                        )
                        response.food_list.add(food)
                        response.nutrition.apply {
                            calorie += food.calory!! * food.amount!!
                            carbohydrates += food.carbohydrates!! * food.amount!!
                            fat += food.fat!! * food.amount!!
                            saturated_fat += food.saturated_fat!! * food.amount!!
                            trans_fat += food.trans_fat!! * food.amount!!
                            cholesterol += food.cholesterol!! * food.amount!!
                            protein += food.protein!! * food.amount!!
                            sodium += food.sodium!! * food.amount!!
                            sugar += food.sugar!! * food.amount!!
                        }
                    }
                }
            }
        }
        response
    }

    /*firebase에서 date부터 오늘까지의 음식들의 영양정보 총합 가져오기*/
    /*GraphActivity*/
    override suspend fun getNutritionData(date: String): Nutrition = withContext(Dispatchers.IO) {
        val nutrition = Nutrition()
        var days = 0
        val datesRef: DatabaseReference =
            database.getReference(path_user).child(userId).child(path_food)
        val outerSnapshot: DataSnapshot = datesRef.get().await()
        for (dateSnapshot in outerSnapshot.children) {
            if (dateSnapshot.value != null) {
                Log.d(
                    "Firebase Communication",
                    "key ${dateSnapshot.key!!}",
                )

                val date1 = LocalDate.parse(dateSnapshot.key.toString().trim())
                val date2 = LocalDate.parse(date.trim())

                if (date1 >= date2) {
                    days++
                    val innerSnapshot = datesRef.child(dateSnapshot.key!!).get().await()
                    for (foodSnapshot in innerSnapshot.children) {
                        val food = foodSnapshot.getValue(FoodEntity::class.java)!!
                        nutrition.apply {
                            calorie += food.calory!! * food.amount!!
                            carbohydrates += food.carbohydrates!! * food.amount!!
                            fat += food.fat!! * food.amount!!
                            saturated_fat += food.saturated_fat!! * food.amount!!
                            trans_fat += food.trans_fat!! * food.amount!!
                            cholesterol += food.cholesterol!!* food.amount!!
                            protein += food.protein!! * food.amount!!
                            sodium += food.sodium!! * food.amount!!
                            sugar += food.sugar!! * food.amount!!
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
    override suspend fun getDayNutrition(date: String): Nutrition = withContext(Dispatchers.IO) {
        val nutrition = Nutrition()

        val datesRef: DatabaseReference =
            database.getReference(path_user).child(userId).child(path_food).child(date.trim())

        val dataSnapshot: DataSnapshot = datesRef.get().await()
        for (snapshot in dataSnapshot.children) {
            Log.d("Firebase Communication", "in day Nutrition, ${snapshot.key}")
            val food = snapshot.getValue(FoodEntity::class.java)!!
            nutrition.apply {
                calorie += food.calory!! * food.amount!!
                carbohydrates += food.carbohydrates!! * food.amount!!
                fat += food.fat!! * food.amount!!
                saturated_fat += food.saturated_fat!! * food.amount!!
                trans_fat += food.trans_fat!! * food.amount!!
                cholesterol += food.cholesterol!! * food.amount!!
                protein += food.protein!! * food.amount!!
                sodium += food.sodium!! * food.amount!!
                sugar += food.sugar!! * food.amount!!
            }
        }
        nutrition
    }

    override suspend fun getUserData(): User = withContext(Dispatchers.IO) {
        val dataRef: DatabaseReference =
            database.getReference(path_user).child(ApplicationClass.uid)
        val dataSnapshot: DataSnapshot = dataRef.get().await()
        val user = dataSnapshot.getValue(User::class.java)!!

        user
    }

    /*임시로 만들어둔 음식 등록용 함수*/
    override fun postFoodData(date: String, food: FoodEntity) {
        val foodRef =
            FirebaseDatabase.getInstance().getReference(path_user).child(userId).child(path_food).child(date.trim()).push()
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
    override suspend fun callAI(question: String): String = withContext(Dispatchers.IO) {
        var str = ""
        val openAI = OpenAI(
            token = BuildConfig.SAMPLE_API_KEY,
            timeout = Timeout(socket = 200.seconds),
            // additional configurations...
        )

        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.User,
                    content = question,
                ),
            ),
        )

        var flag = true
        while (flag) {
            try {
                val completion = openAI.chatCompletion(chatCompletionRequest)

                str = completion.choices[0].message?.content.toString()
                Log.d("Check OpenAI from NetworkManager", str)

                val check = arrayListOf<String>()
                str.split("1.", "2.", "3.", "4.", "5.").toCollection(check)
                flag = false
            } catch (_: Exception) {}
        }
        str
    }
}
