package com.example.goodgun.roomDB

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "table_food")
data class FoodEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long=0,
    val name: String ="none",
    val calory: Int = 0,
    val carbohydrates: Int = 0,
    val sugar: Int = 0,
    val protein: Int = 0,
    val fat: Int = 0,
    val trans_fat: Int = 0,
    val saturated_fat: Int = 0,
    val cholesterol: Int = 0,
    val registerDate: String = LocalDate.now().toString(),
) {
    // LocalDate 타입은 sdk 버전 26 이상부터 작동하므로 minSdk를 26 이상으로 설정해야함
    // 기본 생성자
    // 이름, 등록일자, 열량, 탄수화물, 당류, 단백질, 지방, 트랜스지방, 포화지방, 콜레스테롤
    constructor() : this(0,  "none", 0, 0, 0, 0,0,0,0,0,LocalDate.now().toString())
    constructor(name: String) : this(0, name,  0, 0, 0, 0,0,0,0,0,LocalDate.now().toString())
}