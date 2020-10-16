package com.joyjit.foodspot.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.joyjit.foodspot.R
import com.joyjit.foodspot.activity.CartActivity
import com.joyjit.foodspot.adapter.MenuRecyclerAdapter
import com.joyjit.foodspot.database.CartEntity
import com.joyjit.foodspot.database.FoodDatabase
import com.joyjit.foodspot.model.RestaurantMenu
import com.joyjit.foodspot.utility.ConnectionManager
import kotlinx.android.synthetic.main.activity_home_page.*
import kotlinx.android.synthetic.main.fragment_menu.*
import kotlinx.android.synthetic.main.fragment_menu.view.*
import kotlin.collections.HashMap

class MenuFragment : Fragment(){
    lateinit var recyclerMenu: RecyclerView
    lateinit var recyclerAdapter: MenuRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var btnAddToCart : Button

    var resId = ""
    var resName = ""


    val resInfoList = kotlin.collections.arrayListOf<RestaurantMenu>()
    val orderList = kotlin.collections.arrayListOf<RestaurantMenu>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_menu, container, false)
        recyclerMenu = view.findViewById(R.id.recyclerMenu)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressLayout.visibility = View.VISIBLE
        btnAddToCart = view.findViewById(R.id.butAddToCart)

        resId = arguments?.getString("res_id").toString()
        resName = arguments?.getString("res_name").toString()

        (activity as AppCompatActivity).supportActionBar?.title = "Restaurants Name"



        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/$resId"

        if (ConnectionManager().checkConnectivity(activity as Context)){
            val jsonObjectRequest =
                object : JsonObjectRequest(Method.GET, url, null, Response.Listener {
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if (success) {
                        progressLayout.visibility = View.GONE
                        val jsonArray = data.getJSONArray("data")
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val restaurantMenu = RestaurantMenu(
                                jsonObject.getString("id").toInt(),
                                jsonObject.getString("name"),
                                jsonObject.getString("cost_for_one"),
                                jsonObject.getString("restaurant_id")
                            )
                            resInfoList.add(restaurantMenu)
                            val layoutManager = LinearLayoutManager(activity as Context)
                            recyclerAdapter =
                                MenuRecyclerAdapter(
                                    activity as Context,
                                    resInfoList,
                                    object : MenuRecyclerAdapter.OnItemClickListener {
                                        override fun addFood(resMenu: RestaurantMenu){
                                            orderList.add(resMenu)
                                            btnAddToCart.visibility = View.VISIBLE

                                        }
                                        override fun removeFood(resMenu: RestaurantMenu){
                                            orderList.remove(resMenu)
                                            if(orderList.size>0){
                                                btnAddToCart.visibility= View.VISIBLE
                                            }else{
                                                btnAddToCart.visibility = View.GONE
                                            }
                                        }
                                    })
                            recyclerMenu.adapter = recyclerAdapter
                            recyclerMenu.layoutManager = layoutManager
                        }
                    }else {
                        Toast.makeText(activity as Context, "Data fetching failed", Toast.LENGTH_SHORT).show()
                    }

                }, Response.ErrorListener {
                    progressLayout.visibility = View.GONE
                    if (activity != null) {
                        Toast.makeText(
                            activity,
                            "Some Unexpected error occurred!!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "9bf534118365f1"
                        return headers
                    }
                }
            queue.add(jsonObjectRequest)
        } else {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Connection Error")
            dialog.setMessage("Internet Connection Not Found.")

            dialog.setPositiveButton("OpenSetting") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }
        proceedToCart(view)
        return view
    }

    fun proceedToCart(view: View){
        view.butAddToCart.setOnClickListener {
            val gson = Gson()
            val foodItems = gson.toJson(orderList)
            val addFoodToCart = CartAsyncTask(activity as Context, resId,foodItems,1).execute()
            val foodCart = addFoodToCart.get()
            if(foodCart){
                val intent = Intent(context, CartActivity::class.java)
                intent.putExtra("resName",resName)
                intent.putExtra("resId",resId)
                startActivity(intent)
            }else{
                Toast.makeText( activity as Context, "Some error Occurred!!", Toast.LENGTH_SHORT).show()
            }
        }
    }


    class CartAsyncTask(
        val context: Context,
        val resId: String,
        val foodItem: String,
        private val mode: Int
    ) :
        AsyncTask<Void, Void, Boolean>() {

        val db = Room.databaseBuilder(context, FoodDatabase::class.java, "foods_db").build()

        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {
                1 -> {
                    db.cartDao().insertCartItems(CartEntity(resId, foodItem))
                    db.close()
                    return true
                }
                2 -> {
                    db.cartDao().deleteCartItem(resId)
                    db.cartDao()
                    return true
                }
            }
            return false
        }
    }
}



