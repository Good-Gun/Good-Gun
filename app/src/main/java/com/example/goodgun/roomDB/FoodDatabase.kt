package com.example.goodgun.roomDB

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [FoodEntity::class], version = 2)
abstract class FoodDatabase : RoomDatabase() {

    abstract fun foodDao(): FoodDAO

    // Migration 코드
    companion object {
        val migration_1_to_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE IF EXISTS table_food")
                // 새로운 테이블 생성 (새 스키마에 맞게)
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `table_food` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`name` TEXT NOT NULL, " +
                            "`calory` INTEGER NOT NULL, " +
                            "`carbohydrates` INTEGER NOT NULL, " +
                            "`sugar` INTEGER NOT NULL, " +
                            "`protein` INTEGER NOT NULL, " +
                            "`fat` INTEGER NOT NULL, " +
                            "`trans_fat` INTEGER NOT NULL, " +
                            "`saturated_fat` INTEGER NOT NULL, " +
                            "`cholesterol` INTEGER NOT NULL, " +
                            "`registerDate` TEXT NOT NULL, " +
                            "`registerTime` TEXT NOT NULL)"
                )
            }
        }
    }
}