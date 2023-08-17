package com.example.goodgun.add_food

import androidx.lifecycle.ViewModel

class FoodViewModel : ViewModel() {
    var name: String = ""

    fun reset() {
        name = ""
    }

    fun testset(name: String) {
        this.name = name
    }
    fun testget(): String {
        return name
    }
}
