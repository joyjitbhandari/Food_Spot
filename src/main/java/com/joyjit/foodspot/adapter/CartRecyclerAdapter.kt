package com.joyjit.foodspot.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.joyjit.foodspot.R
import com.joyjit.foodspot.model.RestaurantMenu

class CartRecyclerAdapter(val context: Context, val orderList: ArrayList<RestaurantMenu>): RecyclerView.Adapter<CartRecyclerAdapter.OrderViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_placed_order_single_row, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int){
        val order = orderList[position]
        holder.txtOrderedItems.text = order.name
        holder.txtItemsPrice.text = order.price
    }

    override fun getItemCount(): Int {
       return orderList.size
    }
    inner class OrderViewHolder(view: View):RecyclerView.ViewHolder(view){
        val txtOrderedItems:TextView = view.findViewById(R.id.txtOrderedItems)
        val txtItemsPrice:TextView = view.findViewById(R.id.txtItemsPrice)
        val orderContent:RelativeLayout = view.findViewById(R.id.OrderContent)
    }
}