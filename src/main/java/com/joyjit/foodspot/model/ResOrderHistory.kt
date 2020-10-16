package com.joyjit.foodspot.model

import org.json.JSONArray

data class ResOrderHistory (
    val orderId: Int,
    val resName: String,
    val totalCost: String,
    val orderTime: String,
    val foodItem:JSONArray
)