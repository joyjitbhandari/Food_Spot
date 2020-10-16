package com.joyjit.foodspot.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class CartEntity(
    @PrimaryKey val resId: String,
    val foodItems: String
)

