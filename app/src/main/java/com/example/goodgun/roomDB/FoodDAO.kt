package com.example.goodgun.roomDB

import androidx.room.*

@Dao
interface FoodDAO {

    @Query("SELECT * FROM table_food WHERE name != 'is_sum_entity'")
    fun getAll(): List<FoodEntity>

    @Query("SELECT * FROM table_food WHERE name = 'is_sum_entity'")
    fun getSumFood(): FoodEntity

    @Query("SELECT EXISTS (SELECT 1 FROM table_food WHERE name = 'is_sum_entity' LIMIT 1)")
    fun hasSumFood(): Boolean

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
    fun foodCount(): Int

    @Query(
        "UPDATE table_food SET calory = :calory, " +
            "carbohydrates = :carbohydrates, " +
            "sugar = :sugar, " +
            "protein = :protein, " +
            "fat = :fat, " +
            "trans_fat = :trans_fat, " +
            "saturated_fat = :saturated_fat, " +
            "cholesterol = :cholesterol, " +
            "sodium = :sodium, " +
            "inroomdb = :inroomdb, " +
            "amount = :amount " +
            "WHERE name = :name AND registerDate = :registerDate AND registerTime = :registerTime",
    )
    fun updateFood(
        name: String,
        calory: Double,
        carbohydrates: Double,
        sugar: Double,
        protein: Double,
        fat: Double,
        trans_fat: Double,
        saturated_fat: Double,
        cholesterol: Double,
        sodium: Double,
        registerDate: String,
        registerTime: String,
        inroomdb: Boolean,
        amount: Double,
    )
}
