package com.example.goodgun.roomDB

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [FoodEntity::class], version = 5)
abstract class FoodDatabase : RoomDatabase() {

    abstract fun foodDao(): FoodDAO

    // Migration 코드
    companion object {
        val migration_4_to_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE IF EXISTS table_food")
                // 새로운 테이블 생성 (새 스키마에 맞게)
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `table_food` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`name` TEXT NOT NULL, " +
                        "`calory` DOUBLE, " +
                        "`carbohydrates` DOUBLE, " +
                        "`sugar` DOUBLE, " +
                        "`protein` DOUBLE, " +
                        "`fat` DOUBLE, " +
                        "`trans_fat` DOUBLE, " +
                        "`saturated_fat` DOUBLE, " +
                        "`cholesterol` DOUBLE, " +
                        "`registerDate` TEXT NOT NULL, " +
                        "`registerTime` TEXT NOT NULL, " +
                        "`inroomdb` INTEGER NOT NULL DEFAULT 1)",
                )
            }
        }
    }
}
