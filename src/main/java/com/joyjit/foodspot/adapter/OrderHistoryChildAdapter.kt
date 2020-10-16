package com.joyjit.foodspot.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.joyjit.foodspot.R
import com.joyjit.foodspot.model.ResFoodItems

class OrderHistoryChildAdapter(val context: Context, val foodItemLists: ArrayList<ResFoodItems>) :
    RecyclerView.Adapter<OrderHistoryChildAdapter.OrderHistoryViewHolder>() {

    class OrderHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val foodName: TextView = view.findViewById(R.id.txtFoodName)
        val foodPrice:TextView = view.findViewById(R.id.txtFoodPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
       return OrderHistoryViewHolder(
           LayoutInflater.from(context).inflate(R.layout.order_histroy_child_recyler_single_row,parent,false)
       )
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        val resFoodItems = foodItemLists[position]
        holder.foodName.text= resFoodItems.name
        holder.foodPrice.text = resFoodItems.cost
    }

    override fun getItemCount(): Int {
        return foodItemLists.size
    }
}