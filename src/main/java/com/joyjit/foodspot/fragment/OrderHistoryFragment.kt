package com.joyjit.foodspot.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.joyjit.foodspot.R
import com.joyjit.foodspot.adapter.OrderHistoryParentAdapter
import com.joyjit.foodspot.model.ResOrderHistory
import com.joyjit.foodspot.utility.ConnectionManager

lateinit var  orderHistoryRecyclerView: RecyclerView
lateinit var orderHistoryRecyclerAdapter: OrderHistoryParentAdapter
lateinit var sharedPreferences: SharedPreferences
lateinit var progressLayout: RelativeLayout
var orderHistory = ArrayList<ResOrderHistory>()
class OrderHistoryFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)
        orderHistoryRecyclerView = view.findViewById(R.id.orderHistParentRecyclerView)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressLayout.visibility = View.VISIBLE


        sharedPreferences = (activity as AppCompatActivity).getSharedPreferences(
            getString(R.string.profile_data),
            AppCompatActivity.MODE_PRIVATE
        )
        val userId = sharedPreferences.getString("userId",null)

        val queue = Volley.newRequestQueue(activity as Context)
        val url ="http://13.235.250.119/v2/orders/fetch_result/$userId"
        if (ConnectionManager().checkConnectivity(activity as Context)){
            val jsonObjectRequest = object : JsonObjectRequest(
                Method.GET,
                url,
                null,
                Response.Listener {
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if (success) {
                        progressLayout.visibility = View.GONE
                        val jsonArray = data.getJSONArray("data")
                         for (i in 0 until jsonArray.length()){
                             val jsonObject = jsonArray.getJSONObject(i)
                             val resOrderHistory = ResOrderHistory(
                                 jsonObject.getString("order_id").toInt(),
                                 jsonObject.getString("restaurant_name"),
                                 jsonObject.getString("total_cost"),
                                 jsonObject.getString("order_placed_at"),
                                 jsonObject.getJSONArray("food_items")
                             )
                             orderHistory.add(resOrderHistory)
                             val layoutMethod = LinearLayoutManager(activity as Context)
                             orderHistoryRecyclerView.layoutManager = layoutMethod
                             orderHistoryRecyclerAdapter =  OrderHistoryParentAdapter(activity as Context, orderHistory)
                             orderHistoryRecyclerView.adapter = orderHistoryRecyclerAdapter

                         }
                    } else {
                        Toast.makeText(activity as Context, "Data fetching failed", Toast.LENGTH_SHORT).show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(
                        activity,
                        "Some Unexpected error occurred!!!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }){
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
        return view
    }
}