package com.joyjit.foodspot.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FoodEntity::class, CartEntity::class], version = 1)
abstract class FoodDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao
    abstract fun cartDao(): CartDao
}