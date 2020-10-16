package com.joyjit.foodspot.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FoodDao {
    @Insert
    fun insertFood(foodEntities: FoodEntity)

    @Delete
    fun deleteFood(foodEntities: FoodEntity)

    @Query("SELECT * FROM foods")
    fun getALLFoods(): List<FoodEntity>

    @Query("SELECT * FROM foods WHERE food_id = :foodId")
    fun getFoodById(foodId:String): FoodEntity

}