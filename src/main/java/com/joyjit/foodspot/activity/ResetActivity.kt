package com.joyjit.foodspot.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.finishAffinity
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.joyjit.foodspot.R
import com.joyjit.foodspot.utility.ConnectionManager
import org.json.JSONObject

class ResetActivity : AppCompatActivity() {
    lateinit var etOtp : EditText
    lateinit var etConfirmPassword : EditText
    lateinit var btnSubmit : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset)
        etOtp = findViewById(R.id.etOtp)
        etConfirmPassword = findViewById(R.id.etConPassword)
        btnSubmit = findViewById(R.id.butSubmit)


        btnSubmit.setOnClickListener {
            val otp = etOtp.text.toString()
            val password = etConfirmPassword.text.toString()
            val mobileNum = intent?.getStringExtra("mobileNum").toString()

            if(mobileNum.isNotBlank() &&  password.isNotBlank()){
                val queue = Volley.newRequestQueue(this)
                val url = "http://13.235.250.119/v2/reset_password/fetch_result"

                val jsonParams = JSONObject()
                jsonParams.put("mobile_number",mobileNum)
                jsonParams.put("password",password)
                jsonParams.put("otp",otp)

                if (ConnectionManager().checkConnectivity(this)){
                    val jsonObjectRequest =
                        object : JsonObjectRequest(
                            Method.POST, url, jsonParams,
                            Response.Listener {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                if (success) {
                                    val intent = Intent(this, LoginPage::class.java)
                                    startActivity(intent)
                                    Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show()
                                    finishAffinity(this)
                                }else {
                                    Toast.makeText(this, "Password changing failed", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, "Fill all boxes", Toast.LENGTH_SHORT).show()
            }
        }
    }
}