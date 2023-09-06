package com.example.goodgun.roomDB

import android.content.Context
import androidx.room.Room

object DatabaseManager {
    private val databaseMap: MutableMap<String, FoodDatabase> = mutableMapOf()

    fun getDatabaseInstance(userId: String, context: Context): FoodDatabase {
        return if (databaseMap.containsKey(userId)) {
            databaseMap[userId]!!
        } else {
            val newDatabase = Room.databaseBuilder(
                context.applicationContext,
                FoodDatabase::class.java,
                "food.db",
            )
                .addMigrations(FoodDatabase.migration_5_to_6)
                .build()
            databaseMap[userId] = newDatabase
            newDatabase
        }
    }

    fun removeDatabaseInstance(userId: String) {
        databaseMap.remove(userId)
    }
}
