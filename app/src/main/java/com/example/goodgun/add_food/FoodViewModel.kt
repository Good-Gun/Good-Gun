package com.example.goodgun.add_food

import androidx.lifecycle.ViewModel
import com.example.goodgun.roomDB.FoodEntity
class FoodViewModel : ViewModel() {
    var food: FoodEntity = FoodEntity()
    var userid: String = ""

    fun reset() {
        food = FoodEntity()
    }

    fun is_blank(): Boolean {
        return food.name == "기본 생성자"
    }

    fun getfood(): FoodEntity {
        return food
    }
    fun setfood(new: FoodEntity) {
        food = new
    }

    fun getuserid(): String {
        return userid
    }
    fun setuserid(uid: String) {
        userid = uid
    }
}
