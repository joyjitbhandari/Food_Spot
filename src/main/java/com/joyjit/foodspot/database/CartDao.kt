package com.joyjit.foodspot.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCartItems(cartEntity: CartEntity)

    @Query("DELETE FROM cart WHERE resId = :resId")
    fun deleteCartItem(resId: String)

    @Query("SELECT * FROM cart ")
    fun getAllCart(): List<CartEntity>

}