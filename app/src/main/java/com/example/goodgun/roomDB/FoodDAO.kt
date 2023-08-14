package com.example.goodgun.roomDB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FoodDAO {

    @Query("SELECT * FROM table_food")
    fun getAll(): List<FoodEntity>

    @Query("SELECT * FROM table_food WHERE name = 'is_sum_entity'")
    fun getSumFood(): FoodEntity

    // food 저장 - 중복 값 충돌 발생 시 새로 들어온 데이터로 교체.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveFood(foodEntity: FoodEntity)

    // food 삭제
    @Query("DELETE FROM table_food WHERE name = :name AND registerDate = :registerDate")
    fun deleteFood(name: String, registerDate: String)

    @Query("DELETE FROM table_food")
    fun deleteAll()

    @Query("DELETE FROM table_food WHERE name = 'is_sum_entity'")
    fun deleteSumFood()

    @Query("SELECT count(*) FROM table_food WHERE name != 'is_sum_entity'")
    fun foodCount()
}
