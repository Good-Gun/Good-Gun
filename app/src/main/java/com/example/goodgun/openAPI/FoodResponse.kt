package com.example.goodgun.openAPI

import com.google.gson.annotations.SerializedName

data class FoodList(
    @SerializedName("I2790") val list: FoodDto,
)

data class FoodDto(
    @SerializedName("row") val food: List<FoodItem>,
    @SerializedName("total_count") val count: Int,
)

data class FoodItem(
    @SerializedName("NUM") val number: String,
    @SerializedName("DESC_KOR") val foodName: String,
    @SerializedName("NUTR_CONT1") val calory: String?,
    @SerializedName("NUTR_CONT2") val carbohydrates: String?,
    @SerializedName("NUTR_CONT3") val protein: String?,
    @SerializedName("NUTR_CONT4") val fat: String?,
    @SerializedName("NUTR_CONT5") val sugar: String?,
    @SerializedName("NUTR_CONT6") val sodium: String?,
    @SerializedName("NUTR_CONT7") val cholesterol: String?,
    @SerializedName("NUTR_CONT8") val saturated_fat: String?,
    @SerializedName("NUTR_CONT9") val trans_fat: String?,
    @SerializedName("MAKER_NAME") val maker_name: String?,

    // 번호, 음식 이름, 열량, 탄수화물, 단백질, 지방, 당류, 나트륨, 콜레스테롤, 포화지방산, 트랜스지방
)
