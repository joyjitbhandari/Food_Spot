package com.joyjit.foodspot.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joyjit.foodspot.R
import com.joyjit.foodspot.model.ResFoodItems
import com.joyjit.foodspot.model.ResOrderHistory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OrderHistoryParentAdapter(
    val context: Context,
    private val orderHistList: ArrayList<ResOrderHistory>
) :
    RecyclerView.Adapter<OrderHistoryParentAdapter.OrderHistoryParentViewHolder>() {
    class OrderHistoryParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtResName: TextView = itemView.findViewById(R.id.txtResName)
        val txtOrderDate: TextView = itemView.findViewById(R.id.txtDate)
        val childRecyclerView: RecyclerView =
            itemView.findViewById(R.id.orderHistoryChildRecyclerView)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderHistoryParentViewHolder {
        return OrderHistoryParentViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.order_history_recycler_parent_single_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: OrderHistoryParentViewHolder, position: Int) {
        val resOrderHistory = orderHistList[position]
        holder.txtResName.text = resOrderHistory.resName
        val formatDateTime = formatDateTime(resOrderHistory.orderTime)
        holder.txtOrderDate.text = formatDateTime



        setUpRecyclerView(holder.childRecyclerView, resOrderHistory)
    }

    private fun setUpRecyclerView(
        childRecyclerView: RecyclerView,
        resOrderHistory: ResOrderHistory
    ) {
        childRecyclerView.layoutManager = LinearLayoutManager(context)
        val foodItemList = arrayListOf<ResFoodItems>()
        for (i in 0 until resOrderHistory.foodItem.length()) {
            val jsonObject = resOrderHistory.foodItem.getJSONObject(i)
            val resFoodItems = ResFoodItems(
                jsonObject.getString("food_item_id"),
                jsonObject.getString("name"),
                jsonObject.getString("cost")
            )
            foodItemList.add(resFoodItems)
        }
        childRecyclerView.adapter = OrderHistoryChildAdapter(context, foodItemList)
    }

    override fun getItemCount(): Int {
        return orderHistList.size
    }

    private fun formatDateTime(dateTime: String): String {
        val formater = SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.ENGLISH)
        val date: Date = formater.parse(dateTime) as Date

        val parseDate = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        return parseDate.format(date)
    }
}