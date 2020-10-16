package com.joyjit.foodspot.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.joyjit.foodspot.R
import com.joyjit.foodspot.model.RestaurantMenu


class MenuRecyclerAdapter(
    val context: Context,
    val menuList: ArrayList<RestaurantMenu>,
    var listener: OnItemClickListener
) : RecyclerView.Adapter<MenuRecyclerAdapter.MenuViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_menu_single_row, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menu = menuList[position]
        holder.txtFoodItem.text = menu.name
        holder.txtFoodPrice.text = menu.price
        holder.txtSerialNam.text = "${position + 1}."

        holder.btnAdd.setOnClickListener {
            holder.btnRemove.visibility = View.VISIBLE
            holder.btnAdd.visibility = View.GONE
            listener.addFood(menu)
        }
        holder.btnRemove.setOnClickListener {
            holder.btnRemove.visibility = View.GONE
            holder.btnAdd.visibility = View.VISIBLE
            listener.removeFood(menu)
        }
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    inner class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtSerialNam: TextView = view.findViewById(R.id.txtSerialNum)
        val txtFoodItem: TextView = view.findViewById(R.id.txtFoodItem)
        val txtFoodPrice: TextView = view.findViewById(R.id.txtFoodPrice)
        val btnAdd: Button = view.findViewById(R.id.btnAdd)
        val btnRemove: Button = view.findViewById(R.id.btnRemove)
        val menuContent: RelativeLayout = view.findViewById(R.id.menuContent)

    }

    companion object{
        var isCartEmpty = true
    }

    interface OnItemClickListener {
        fun addFood(ResMenu: RestaurantMenu)
        fun removeFood(ResMenu: RestaurantMenu)
    }

}