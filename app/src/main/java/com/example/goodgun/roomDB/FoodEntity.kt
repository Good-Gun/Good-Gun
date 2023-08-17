package com.example.goodgun.roomDB

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "table_food")
data class FoodEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String = "none",
    var calory: Double = 0.0,
    var carbohydrates: Double = 0.0,
    var sugar: Double = 0.0,
    var protein: Double = 0.0,
    var fat: Double = 0.0,
    var trans_fat: Double = 0.0,
    var saturated_fat: Double = 0.0,
    var cholesterol: Double = 0.0,
    val registerDate: String = LocalDate.now().toString(),
    val registerTime: String = LocalTime.now().toString(),
) {
    // LocalDate 타입은 sdk 버전 26 이상부터 작동하므로 minSdk를 26 이상으로 설정해야함
    // 기본 생성자
    // 이름, 열량, 탄수화물, 당류, 단백질, 지방, 트랜스지방, 포화지방, 콜레스테롤, 등록일자, 등록시간
    constructor() : this(0, "기본 생성자", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, LocalDate.now().toString(), LocalTime.now().toString())
    constructor(name: String) : this(0, name, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, LocalDate.now().toString(), LocalTime.now().toString())


    constructor(name: String, calory: Double, carbohydrates: Double, sugar: Double, protein: Double, fat: Double, trans_fat: Double, saturated_fat: Double, cholesterol: Double) : this(0, name, calory, carbohydrates, sugar, protein, fat, trans_fat, saturated_fat, cholesterol, LocalDate.now().toString(), LocalTime.now().toString())
}
