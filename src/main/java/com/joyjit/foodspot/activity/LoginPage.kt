package com.joyjit.foodspot.activity

import android.content.Intent
import android.content.SharedPreferences
import android.media.audiofx.BassBoost
import android.os.Build.VERSION_CODES.P
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.joyjit.foodspot.R
import com.joyjit.foodspot.utility.ConnectionManager
import org.json.JSONException
import org.json.JSONObject
import javax.xml.transform.OutputKeys.METHOD

class LoginPage : AppCompatActivity() {
    lateinit var etUserId: EditText
    lateinit var etPassword: EditText
    lateinit var butLogin: Button
    lateinit var butSingUp: Button
    lateinit var txtForgotPassword: TextView
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_login)

        etUserId = findViewById(R.id.etUserId)
        etPassword = findViewById(R.id.etPassword)
        butLogin = findViewById(R.id.butLogin)
        butSingUp = findViewById(R.id.butSingUp)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)

        sharedPreferences = getSharedPreferences(getString(R.string.profile_data), MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn",false)

        if(isLoggedIn){
            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)
            ActivityCompat.finishAffinity(this)
        }


        butLogin.setOnClickListener {
            val mobileNum = etUserId.text.toString()
            val password = etPassword.text.toString()

            if (mobileNum.isNotBlank() && password.isNotBlank()) {
                val queue = Volley.newRequestQueue(this)
                val url = "http://13.235.250.119/v2/login/fetch_result"

                val jsonParams = JSONObject()
                jsonParams.put("mobile_number",mobileNum)
                jsonParams.put("password",password)

                if (ConnectionManager().checkConnectivity(this)){
                        val jsonObjectRequest =
                            object : JsonObjectRequest(Method.POST, url, jsonParams,
                                Response.Listener {
                                    val data = it.getJSONObject("data")
                                    val success = data.getBoolean("success")
                                    if (success) {
                                        val jsonObject = data.getJSONObject("data")
                                        sharedPreferences.edit()
                                            .putString("userId", jsonObject.getString("user_id"))
                                            .apply()
                                        sharedPreferences.edit()
                                            .putString("username", jsonObject.getString("name"))
                                            .apply()
                                        sharedPreferences.edit()
                                            .putString("userEmail", jsonObject.getString("email"))
                                            .apply()
                                        sharedPreferences.edit().putString(
                                            "mobileNumber",
                                            jsonObject.getString("mobile_number")
                                        ).apply()
                                        sharedPreferences.edit()
                                            .putString("address", jsonObject.getString("address"))
                                            .apply()

                                        val intent = Intent(this, HomePage::class.java)
                                        startActivity(intent)
                                        ActivityCompat.finishAffinity(this)
                                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT)
                                            .show()


                                        sharedPreferences.edit().putBoolean("isLoggedIn", true)
                                            .apply()
                                    }else {
                                        Toast.makeText(this, "Data fetching failed", Toast.LENGTH_SHORT).show()
                                    }
                                }, Response.ErrorListener {
                                    Toast.makeText(
                                        this,
                                        "Login failed!!!",
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
                Toast.makeText(this, "Fill the boxes.", Toast.LENGTH_SHORT).show()
            }
        }


        butSingUp.setOnClickListener {
            val intent = Intent(this,RegistrationPage::class.java)
            startActivity(intent)
        }
        txtForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordPage::class.java)
            startActivity(intent)
        }
    }
}