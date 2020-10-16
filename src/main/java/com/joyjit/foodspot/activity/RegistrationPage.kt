package com.joyjit.foodspot.activity

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.joyjit.foodspot.R
import com.joyjit.foodspot.utility.ConnectionManager
import org.json.JSONObject

class RegistrationPage : AppCompatActivity() {
    lateinit var imgBack: ImageView
    lateinit var etName: EditText
    lateinit var etEmail: EditText
    lateinit var etMobileNum: EditText
    lateinit var etDeliveryAdd: EditText
    lateinit var etNewPassword: EditText
    lateinit var etConPassword: EditText
    lateinit var butRegister: Button
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_register)
        imgBack = findViewById(R.id.imgBack)
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etMobileNum = findViewById(R.id.etMobileNum)
        etDeliveryAdd = findViewById(R.id.etDeliveryAdd)
        etNewPassword = findViewById(R.id.etNewPassword)
        etConPassword = findViewById(R.id.etConPassword)
        butRegister = findViewById(R.id.butRegister)

        sharedPreferences = getSharedPreferences(getString(R.string.profile_data), MODE_PRIVATE)


        imgBack.setOnClickListener {
            val intent = Intent(this@RegistrationPage, LoginPage::class.java)
            startActivity(intent)
            ActivityCompat.finishAffinity(this@RegistrationPage)
        }

        butRegister.setOnClickListener {
            val mobileNum = etMobileNum.text.toString()
            val newPassword = etNewPassword.text.toString()
            val conPassword = etConPassword.text.toString()
            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val address = etDeliveryAdd.text.toString()




            if (mobileNum.isNotBlank() && newPassword.isNotBlank() && conPassword.isNotBlank() && name.isNotBlank()) {
                if (newPassword == conPassword) {
                    val queue = Volley.newRequestQueue(this@RegistrationPage)
                    val url = "http://13.235.250.119/v2/register/fetch_result"
                    val jsonParams = JSONObject()
                    jsonParams.put("name", name)
                    jsonParams.put("mobile_number", mobileNum)
                    jsonParams.put("password", conPassword)
                    jsonParams.put("address", address)
                    jsonParams.put("email", email)

                    if (ConnectionManager().checkConnectivity(this)) {
                        val jsonObjectRequest =
                            object : JsonObjectRequest(
                                Method.POST,
                                url,
                                jsonParams,
                                Response.Listener {
                                    val data = it.getJSONObject("data")
                                    val success = data.getBoolean("success")
                                    if (success) {
                                        val jsonObject = data.getJSONObject("data")


                                        sharedPreferences.edit().putString("userId",jsonObject.getString("user_id")).apply()
                                        sharedPreferences.edit().putString("username",jsonObject.getString("name")).apply()
                                        sharedPreferences.edit().putString("userEmail",jsonObject.getString("email")).apply()
                                        sharedPreferences.edit().putString("mobileNumber",jsonObject.getString("mobile_number")).apply()
                                        sharedPreferences.edit().putString("address",jsonObject.getString("address")).apply()


                                        val intent = Intent(this, HomePage::class.java)
                                        startActivity(intent)
                                        ActivityCompat.finishAffinity(this)

                                        Toast.makeText(
                                            this,
                                            "Registration successful",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }else {
                                        Toast.makeText(this, "$it", Toast.LENGTH_SHORT).show()
                                    }
                                }, Response.ErrorListener {
                                    Toast.makeText(
                                        this,
                                        "Registration failed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }) {
                                override fun getHeaders(): MutableMap<String, String> {
                                    val headers = HashMap<String, String>()
                                    headers["Content-type"]="application/json"
                                    headers["token"] = "9bf534118365f1"
                                    return headers
                                }
                            }
                        queue.add(jsonObjectRequest)
                    } else {
                        val dialog = AlertDialog.Builder(this)
                        dialog.setTitle("Connection Error")
                        dialog.setMessage("Internet Connection Not Found.")

                        dialog.setPositiveButton("Open Setting") { text, listener ->
                            val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                            startActivity(settingIntent)
                            this?.finish()
                        }
                        dialog.setNegativeButton("Exit") { text, listener ->
                            ActivityCompat.finishAffinity(this)
                        }
                        dialog.create()
                        dialog.show()
                    }

                } else {
                    Toast.makeText(this, "Password not matched", Toast.LENGTH_LONG)
                        .show()
                }
            } else {
                Toast.makeText(this, "Fill all the boxes.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}