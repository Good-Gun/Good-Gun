package com.example.goodgun.roomDB

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [FoodEntity::class], version = 3)
abstract class FoodDatabase : RoomDatabase() {

    abstract fun foodDao(): FoodDAO

    // Migration 코드
    companion object {
        val migration_2_to_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE IF EXISTS table_food")
                // 새로운 테이블 생성 (새 스키마에 맞게)
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `table_food` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`name` TEXT NOT NULL, " +
                        "`calory` DOUBLE NOT NULL, " +
                        "`carbohydrates` DOUBLE NOT NULL, " +
                        "`sugar` DOUBLE NOT NULL, " +
                        "`protein` DOUBLE NOT NULL, " +
                        "`fat` DOUBLE NOT NULL, " +
                        "`trans_fat` DOUBLE NOT NULL, " +
                        "`saturated_fat` DOUBLE NOT NULL, " +
                        "`cholesterol` DOUBLE NOT NULL, " +
                        "`registerDate` TEXT NOT NULL, " +
                        "`registerTime` TEXT NOT NULL)",
                )
            }
        }
    }
}
