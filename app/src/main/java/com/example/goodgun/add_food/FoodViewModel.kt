package com.example.goodgun.add_food

import androidx.lifecycle.ViewModel
import com.example.goodgun.roomDB.FoodEntity

class FoodViewModel:ViewModel() {
    var food:FoodEntity = FoodEntity()

    fun reset(){
        food=FoodEntity()
    }

    fun is_blank():Boolean{
        return food.name=="기본 생성자"
    }

    fun getfood():FoodEntity{
        return food
    }
    fun setfood(new:FoodEntity){
        food=new
    }
}