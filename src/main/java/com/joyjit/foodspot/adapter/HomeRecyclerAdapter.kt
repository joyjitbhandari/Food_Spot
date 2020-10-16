package com.joyjit.foodspot.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.joyjit.foodspot.R
import com.joyjit.foodspot.activity.ResMenuActivity
import com.joyjit.foodspot.database.FoodDatabase
import com.joyjit.foodspot.database.FoodEntity
import com.joyjit.foodspot.model.RestaurantMenu
import com.joyjit.foodspot.model.Restuarant
import com.squareup.picasso.Picasso

class HomeRecyclerAdapter(
    val context: Context,
    val itemList: ArrayList<Restuarant>,

) :
    RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_home_single_row, parent, false)
        return HomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val restuarant = itemList[position]
        holder.txtFoodName.text = restuarant.resName
        holder.txtFoodPrice.text = "Rs-${restuarant.resPrice}/Person"
        holder.txtFoodRating.text = restuarant.resRating
        Picasso.get().load(restuarant.resImage).error(R.drawable.default_cover_photo)
            .into(holder.imgFoodImage)

        holder.homeContent.setOnClickListener {
            val intent = Intent(context, ResMenuActivity::class.java)
            intent.putExtra("res_id", restuarant.resId)
            intent.putExtra("res_name", restuarant.resName)
            context.startActivity(intent)
        }

        val foodEntity = FoodEntity(
            restuarant.resId.toInt(),
            holder.txtFoodName.text.toString(),
            holder.txtFoodRating.text.toString(),
            holder.txtFoodPrice.text.toString(),
            restuarant.resImage
        )

        val checkFav = DBAsyncTask(context, foodEntity, 1).execute()
        val isFav = checkFav.get()
        if (isFav) {
            holder.foodFav.setBackgroundResource(R.drawable.ic_favourite)
        } else {
            holder.foodFav.setBackgroundResource(R.drawable.ic_unfavourite)
        }

        holder.foodFav.setOnClickListener {
            if (!DBAsyncTask(context, foodEntity, 1).execute().get()) {
                val async = DBAsyncTask(context, foodEntity, 2).execute()
                val result = async.get()
                if (result) {
                    holder.foodFav.setBackgroundResource(R.drawable.ic_favourite)
                } else {
                    Toast.makeText(context, "Some Error Occurred!!!", Toast.LENGTH_SHORT).show()
                }
            } else {
                val async = DBAsyncTask(context, foodEntity, 3).execute()
                val result = async.get()
                if (result) {
                    holder.foodFav.setBackgroundResource(R.drawable.ic_unfavourite)
                } else {
                    Toast.makeText(context, "Some Error Occurred!!!", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtFoodName: TextView = view.findViewById(R.id.txtFoodName)
        val homeContent: LinearLayout = view.findViewById(R.id.homeContent)
        val imgFoodImage: ImageView = view.findViewById(R.id.imgFoodImage)
        val txtFoodPrice: TextView = view.findViewById(R.id.txtFoodPrice)
        val foodFav: ImageView = view.findViewById(R.id.imgFoodFav)
        val txtFoodRating: TextView = view.findViewById(R.id.txtFoodRating)

    }

    class DBAsyncTask(val context: Context, val foodEntity: FoodEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, FoodDatabase::class.java, "foods_db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {
                1 -> {
                    val food: FoodEntity? = db.foodDao().getFoodById(foodEntity.food_id.toString())
                    db.close()
                    return food != null
                }
                2 -> {
                    //Save the book int DB as favourite
                    db.foodDao().insertFood(foodEntity)
                    db.close()
                    return true
                }
                3 -> {
                    //Remove the favourite book
                    db.foodDao().deleteFood(foodEntity)
                    db.close()
                    return true
                }
            }
            return false
        }
    }
}