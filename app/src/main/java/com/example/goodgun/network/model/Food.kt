package com.example.goodgun.network.model

import java.time.LocalDate

data class Food(
    var id: Int,
    var name: String,
    var calorie: Int,
    var carbohydrates: Int,
    var sugar: Int,
    var protein: Int,
    var fat: Int,
    var trans_fat: Int,
    var saturated_fat: Int,
    var cholesterol: Int,
    var sodium: Int,
    var registerDate: String,
) {
    // LocalDate 타입은 sdk 버전 26 이상부터 작동하므로 minSdk를 26 이상으로 설정해야함
    // 기본 생성자
    // 이름, 등록일자, 열량, 탄수화물, 당류, 단백질, 지방, 트랜스지방, 포화지방, 콜레스테롤
    constructor() : this(0, "", 0, 0, 0, 0, 0, 0, 0, 0, 0, LocalDate.now().toString())
    constructor(name: String) : this(0, name, 0, 0, 0, 0, 0, 0, 0, 0, 0, LocalDate.now().toString())
}
