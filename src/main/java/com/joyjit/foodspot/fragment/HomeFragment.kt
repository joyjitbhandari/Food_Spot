package com.joyjit.foodspot.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.joyjit.foodspot.R
import com.joyjit.foodspot.adapter.HomeRecyclerAdapter
import com.joyjit.foodspot.model.Restuarant
import com.joyjit.foodspot.utility.ConnectionManager
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

class HomeFragment : Fragment() {
    lateinit var recyclerHome: RecyclerView
    lateinit var recyclerAdapter: HomeRecyclerAdapter
    lateinit var progressLayout : RelativeLayout

    val foodInfoList = kotlin.collections.arrayListOf<Restuarant>()

    val ratingComparator = Comparator<Restuarant>{ food1, food2->
      if(food1.resRating.compareTo(food2.resRating,true) == 0){
          food1.resName.compareTo(food2.resName,true)
      }else{
          food1.resRating.compareTo(food2.resRating,true)
      }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_home, container, false)

        setHasOptionsMenu(true)

        recyclerHome = view.findViewById(R.id.recyclerHome)

        progressLayout = view.findViewById(R.id.progressLayout)
        progressLayout.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(activity as Context)
        val url ="http://13.235.250.119/v2/restaurants/fetch_result/"

        if(ConnectionManager().checkConnectivity(activity as Context)){
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
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val restaurant = Restuarant(
                                jsonObject.getString("id"),
                                jsonObject.getString("name"),
                                jsonObject.getString("rating"),
                                jsonObject.getString("cost_for_one"),
                                jsonObject.getString("image_url")
                            )
                            foodInfoList.add(restaurant)
                            val layoutManager = LinearLayoutManager(activity as Context)
                            recyclerAdapter =
                                HomeRecyclerAdapter(activity as Context, foodInfoList)
                            recyclerHome.adapter = recyclerAdapter
                            recyclerHome.layoutManager = layoutManager
                        }
                    } else {
                        Toast.makeText(activity as Context, "Data fetching failed", Toast.LENGTH_SHORT).show()
                    }
                },
                Response.ErrorListener {
                    progressLayout.visibility = View.GONE
                    if (activity != null) {
                        Toast.makeText(
                            activity,
                            "Some Unexpected error occurred!!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "9bf534118365f1"
                    return headers
                }

            }
            queue.add(jsonObjectRequest)
    }else{
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Connection Error")
            dialog.setMessage("Internet Connection Not Found.")

            dialog.setPositiveButton("OpenSetting"){text, listener->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit"){text, listener->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
      inflater?.inflate(R.menu.menu_home,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item?.itemId
        if(id == R.id.action_sort){
            Collections.sort(foodInfoList,ratingComparator)
            foodInfoList.reverse()
        }
        recyclerAdapter.notifyDataSetChanged()

        return super.onOptionsItemSelected(item)
    }
}