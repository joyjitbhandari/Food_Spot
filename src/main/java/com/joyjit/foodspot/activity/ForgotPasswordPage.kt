package com.joyjit.foodspot.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.JsonObject
import com.joyjit.foodspot.R
import com.joyjit.foodspot.utility.ConnectionManager
import org.json.JSONObject
import java.nio.file.Paths.get

class ForgotPasswordPage : AppCompatActivity() {
    lateinit var imgBack: ImageView
    lateinit var etMobileNum: EditText
    lateinit var etEmail: EditText
    lateinit var butNext: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_forgot_password)

        etMobileNum = findViewById(R.id.etMobileNum)
        etEmail = findViewById(R.id.etEmail)
        imgBack = findViewById(R.id.imgBack)
        butNext = findViewById(R.id.butNext)

        imgBack.setOnClickListener{
            val intent = Intent(this@ForgotPasswordPage,LoginPage::class.java)
            startActivity(intent)
            ActivityCompat.finishAffinity(this@ForgotPasswordPage)
        }

        butNext.setOnClickListener {
            val mobileNum = etMobileNum.text.toString()
            val email = etEmail.text.toString()

            if(mobileNum.isNotBlank() &&  email.isNotBlank()){
                val queue = Volley.newRequestQueue(this)
                val url = "http://13.235.250.119/v2/forgot_password/fetch_result"

                val jsonParams = JSONObject()
                jsonParams.put("mobile_number",mobileNum)
                jsonParams.put("email",email)

                if (ConnectionManager().checkConnectivity(this)){
                    val jsonObjectRequest =
                        object : JsonObjectRequest(
                            Method.POST, url, jsonParams,
                            Response.Listener {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                if (success) {
                                    val intent = Intent(this, ResetActivity::class.java)
                                    intent.putExtra("mobileNum", mobileNum)
                                    startActivity(intent)
                                    Toast.makeText(this, "OTP sent to your register email & mobile number", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this, "Data fetching failed", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }, Response.ErrorListener {
                                Toast.makeText(
                                    this,
                                    "Some Error occurred!!!",
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
                }else{
                    val dialog = AlertDialog.Builder(this,)
                    dialog.setTitle("Connection Error")
                    dialog.setMessage("Internet Connection Not Found.")

                    dialog.setPositiveButton("OpenSetting"){text, listener->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        this?.finish()
                    }
                    dialog.setNegativeButton("Exit"){text, listener->
                        ActivityCompat.finishAffinity(this)
                    }
                    dialog.create()
                    dialog.show()
                }

            }else{
                Toast.makeText(this@ForgotPasswordPage, "Fill all boxes", Toast.LENGTH_SHORT).show()
            }


        }
    }
}