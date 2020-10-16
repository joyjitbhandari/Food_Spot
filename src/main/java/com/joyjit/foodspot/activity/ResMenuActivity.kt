package com.joyjit.foodspot.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.joyjit.foodspot.R
import com.joyjit.foodspot.adapter.MenuRecyclerAdapter
import com.joyjit.foodspot.database.CartEntity
import com.joyjit.foodspot.database.FoodDatabase
import com.joyjit.foodspot.model.RestaurantMenu
import com.joyjit.foodspot.utility.ConnectionManager
import kotlinx.android.synthetic.main.activity_menu.*

class ResMenuActivity : AppCompatActivity() {

    lateinit var recyclerMenu: RecyclerView
    lateinit var recyclerAdapter: MenuRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var btnAddToCart: Button
    lateinit var toolBar:Toolbar

    var resId = ""
    var resName = ""

    val resInfoList = arrayListOf<RestaurantMenu>()
    val orderList = arrayListOf<RestaurantMenu>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        recyclerMenu = findViewById(R.id.recyclerMenu)
        progressLayout = findViewById(R.id.progressLayout)
        progressLayout.visibility = View.VISIBLE
        btnAddToCart = findViewById(R.id.butAddToCart)
        toolBar = findViewById(R.id.toolBar)

        resId = intent?.getStringExtra("res_id").toString()
        resName = intent?.getStringExtra("res_name").toString()

        setSupportActionBar(toolBar)
        supportActionBar?.title = resName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/$resId"

        if (ConnectionManager().checkConnectivity(this)) {
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
                            val layoutManager = LinearLayoutManager(this)
                            recyclerAdapter =
                                MenuRecyclerAdapter(
                                    this,
                                    resInfoList,
                                    object : MenuRecyclerAdapter.OnItemClickListener {
                                        override fun addFood(resMenu: RestaurantMenu) {
                                            orderList.add(resMenu)
                                            btnAddToCart.visibility = View.VISIBLE
                                            MenuRecyclerAdapter.isCartEmpty = false
                                        }

                                        override fun removeFood(resMenu: RestaurantMenu) {
                                            orderList.remove(resMenu)
                                            if (orderList.size > 0) {
                                                btnAddToCart.visibility = View.VISIBLE
                                            } else {
                                                btnAddToCart.visibility = View.GONE
                                                MenuRecyclerAdapter.isCartEmpty = true
                                            }
                                        }
                                    })
                            recyclerMenu.adapter = recyclerAdapter
                            recyclerMenu.layoutManager = layoutManager
                        }
                    }else {
                        Toast.makeText(this, "Data fetching failed", Toast.LENGTH_SHORT).show()
                    }

                }, Response.ErrorListener {
                    progressLayout.visibility = View.GONE

                    Toast.makeText(
                        this,
                        "Some Unexpected error occurred!!!!",
                        Toast.LENGTH_SHORT
                    ).show()

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
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Connection Error")
            dialog.setMessage("Internet Connection Not Found.")

            dialog.setPositiveButton("OpenSetting") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                this.finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this)
            }
            dialog.create()
            dialog.show()
        }
        proceedToCart()
    }

    private fun proceedToCart() {
        butAddToCart.setOnClickListener {
            val gson = Gson()
            val foodItems = gson.toJson(orderList)
            val addFoodToCart = CartAsyncTask(this, resId, foodItems, 1).execute()
            val foodCart = addFoodToCart.get()
            if (foodCart) {
                val intent = Intent(this, CartActivity::class.java)
                intent.putExtra("resName", resName)
                intent.putExtra("resId", resId)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Some error Occurred!!", Toast.LENGTH_SHORT)
                    .show()
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
    override fun onBackPressed() {
        if (MenuRecyclerAdapter.isCartEmpty) {
            super.onBackPressed()

        } else {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Confirmation")
            dialog.setMessage("Going back will reset cart items. Do you still want to proceed")
            dialog.setPositiveButton("OK") { _, _ ->

                /*Deleting cart items*/
                CartAsyncTask(
                    this,
                    resId,
                    "null",
                    2
                ).execute()
                super.onBackPressed()
                MenuRecyclerAdapter.isCartEmpty = true
            }
            dialog.setNegativeButton("Cancel") { dialog2, which ->
                dialog2.dismiss()
            }
            dialog.show()
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
            if (MenuRecyclerAdapter.isCartEmpty) {
                super.onBackPressed()

            } else {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("Confirmation")
                dialog.setMessage("Going back will reset cart items. Do you still want to proceed")
                dialog.setPositiveButton("OK") { _, _ ->

                    /*Deleting cart items*/
                    CartAsyncTask(
                        this,
                        resId,
                        "null",
                        2
                    ).execute()
                    super.onBackPressed()
                    MenuRecyclerAdapter.isCartEmpty = true
                }
                dialog.setNegativeButton("Cancel") { dialog2, which ->
                    dialog2.dismiss()
                }
                dialog.show()
            }
        return true
    }
}