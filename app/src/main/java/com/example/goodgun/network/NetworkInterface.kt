package com.example.goodgun.network

import com.aallam.openai.api.BetaOpenAI
import com.example.goodgun.network.model.Nutrition
import com.example.goodgun.network.model.NutritionResponse
import com.example.goodgun.network.model.User
import com.example.goodgun.roomDB.FoodEntity

interface NetworkInterface {

    /*특정 날짜에 등록된 음식 정보 및 영양 정보*/
    suspend fun getFoodByDate(date: String): NutritionResponse

    /*특정 날짜로부터 오늘까지의 영양 정보*/
    suspend fun getNutritionData(date: String): Nutrition

    /*특정 날짜의 영양 정보만 가져오기*/
    suspend fun getDayNutrition(date: String): Nutrition

    /*유저 데이터 가져오기*/
    suspend fun getUserData(): User

    /*음식 정보 등록*/
    fun postFoodData(date: String, food: FoodEntity)

    /*Chat GPT 답 받기*/
    @OptIn(BetaOpenAI::class)
    suspend fun callAI(question: String): String

    /*
    1. 특정 날짜로부터 오늘까지의 영양정보 받기
    2. 영양 정보로부터 질문 만들기
    3. 질문을 callAI 함수에 넣고 답변을 받기

    위 모든 사항을 GlobalScope로 돌려서 값을 파이어베이스에 저장해야 함
    */
}
