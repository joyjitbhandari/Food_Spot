package com.joyjit.foodspot.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
import com.joyjit.foodspot.adapter.CartRecyclerAdapter
import com.joyjit.foodspot.database.CartEntity
import com.joyjit.foodspot.database.FoodDatabase
import com.joyjit.foodspot.model.RestaurantMenu
import com.joyjit.foodspot.utility.ConnectionManager
import kotlinx.android.synthetic.main.activity_cart.*
import org.json.JSONArray
import org.json.JSONObject

class CartActivity : AppCompatActivity() {
    lateinit var recyclerOrder: RecyclerView
    lateinit var recyclerAdapter: CartRecyclerAdapter
    lateinit var btnPlaceOrder: Button
    lateinit var txtOrderedFrom: TextView
    lateinit var sharedPreferences: SharedPreferences
    lateinit var rlLoading: RelativeLayout
    lateinit var toolBar: Toolbar
    private var sum = 0
    private var userId = ""
    private var resId = ""
    private var resName = ""

    var orderList = arrayListOf<RestaurantMenu>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        recyclerOrder = findViewById(R.id.recyclerOrder)
        btnPlaceOrder = findViewById(R.id.butPlaceOrder)
        txtOrderedFrom = findViewById(R.id.txtOrderFrom)
        rlLoading = findViewById(R.id.rlLoading)

        toolBar = findViewById(R.id.toolBar)

        sharedPreferences = getSharedPreferences(getString(R.string.profile_data), MODE_PRIVATE)
        userId = sharedPreferences.getString("userId", null).toString()
        resId = intent?.getStringExtra("resId").toString()
        resName = intent?.getStringExtra("resName").toString()

        txtOrderedFrom.text = "Ordered From : ${resName}"
        setSupportActionBar(toolBar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val get = GetData(applicationContext).execute().get()
        for (i in get) {
            orderList.addAll(
                Gson().fromJson(i.foodItems, Array<RestaurantMenu>::class.java).asList()
            )
            val layoutManager = LinearLayoutManager(this)
            recyclerAdapter = CartRecyclerAdapter(this, orderList)
            recyclerOrder.adapter = recyclerAdapter
            recyclerOrder.layoutManager = layoutManager
        }

        for (i in 0 until orderList.size) {
            sum += orderList[i].price.toInt()
        }
        butPlaceOrder.text = "Placed Order (${sum})"

        btnPlaceOrder.setOnClickListener {
            val queue = Volley.newRequestQueue(this)
            val url = "http://13.235.250.119/v2/place_order/fetch_result/"

            val jsonArray = JSONArray()
            for (i in 0 until orderList.size) {
                val foodId = JSONObject()
                foodId.put("foodId", orderList[i].id)
                jsonArray.put(i, foodId)
            }

            val jsonParams = JSONObject()
            jsonParams.put("user_id", userId)
            jsonParams.put("restaurant_id", resId)
            jsonParams.put("total_cost", sum)
            jsonParams.put("food", jsonArray)

            if (ConnectionManager().checkConnectivity(this)) {
                val jsonObjectRequest =
                    object : JsonObjectRequest(
                        Method.POST, url, jsonParams,
                        Response.Listener {
                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")
                            if (success) {
                                val dialog = Dialog(
                                    this,
                                    android.R.style.Theme_Black_NoTitleBar_Fullscreen
                                )
                                dialog.setContentView(R.layout.dilaog_order_confirmation)
                                dialog.show()
                                dialog.setCancelable(false)
                                val btnOk = dialog.findViewById<Button>(R.id.butOk)
                                btnOk.setOnClickListener {
                                    dialog.dismiss()
                                    super.onBackPressed()
                                    MenuRecyclerAdapter.isCartEmpty = true
                                     rlLoading.visibility = View.VISIBLE
                                    btnPlaceOrder.visibility = View.GONE

                                    /*After a successful order open a HomePage Activity*/
                                    startActivity(
                                        Intent(this, HomePage::class.java)
                                    )
                                    ActivityCompat.finishAffinity(this)
                                }
                            } else {
                                Toast.makeText(
                                    this,
                                    data.getString("errorMessage"),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }, Response.ErrorListener {
                            Toast.makeText(
                                this,
                                "error $it",
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
        }
    }

    class GetData(context: Context) : AsyncTask<Void, Void, List<CartEntity>>() {
        val db = Room.databaseBuilder(context, FoodDatabase::class.java, "foods_db").build()
        override fun doInBackground(vararg params: Void?): List<CartEntity> {
            return db.cartDao().getAllCart()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
            if (MenuRecyclerAdapter.isCartEmpty && item.itemId == android.R.id.home) {
                super.onBackPressed()
            } else {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("Confirmation")
                dialog.setMessage("Going back will reset cart items. Do you still want to proceed")
                dialog.setPositiveButton("OK") { _, _ ->

                    /*Deleting cart items*/
                    ResMenuActivity.CartAsyncTask(
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