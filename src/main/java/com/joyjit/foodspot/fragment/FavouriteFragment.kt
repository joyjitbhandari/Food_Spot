package com.joyjit.foodspot.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.joyjit.foodspot.R
import com.joyjit.foodspot.adapter.HomeRecyclerAdapter
import com.joyjit.foodspot.database.FoodDatabase
import com.joyjit.foodspot.database.FoodEntity
import com.joyjit.foodspot.model.Restuarant
import java.util.ArrayList

class FavouriteFragment : Fragment() {
    lateinit var recyclerFavourite: RecyclerView
    lateinit var progressLayout: RelativeLayout
    lateinit var layoutManager : RecyclerView.LayoutManager
    lateinit var recyclerAdapter: HomeRecyclerAdapter
    var foodList = ArrayList<Restuarant>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)
        recyclerFavourite = view.findViewById(R.id.RecyclerFavourites)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressLayout.visibility = View.VISIBLE

        layoutManager = LinearLayoutManager(activity as Context)

        val dbFoodList = RetrieveFavourites(activity as Context).execute().get()

            for(i in dbFoodList){
                foodList.add(
                    Restuarant(
                        i.food_id.toString(),
                        i.foodName,
                        i.foodRating,
                        i.foodPrice,
                        i.foodImage
                    )
                )
            }
        if(activity !=null){
            progressLayout.visibility = View.GONE
            recyclerAdapter = HomeRecyclerAdapter(activity as Context, foodList)
            recyclerFavourite.adapter = recyclerAdapter
            recyclerFavourite.layoutManager = layoutManager
        }
        return view
    }
    class RetrieveFavourites(val context: Context): AsyncTask<Void, Void, List<FoodEntity>>(){
        override fun doInBackground(vararg params: Void?): List<FoodEntity> {
            val db = Room.databaseBuilder(context, FoodDatabase::class.java,"foods_db").build()
            return db.foodDao().getALLFoods()
        }
    }
}