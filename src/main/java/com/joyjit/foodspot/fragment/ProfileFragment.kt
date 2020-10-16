package com.joyjit.foodspot.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.joyjit.foodspot.R
import java.lang.NumberFormatException
import java.util.jar.Attributes

class ProfileFragment : Fragment() {
    lateinit var imgProfilePhoto: ImageView
    lateinit var txtProfileName: TextView
    lateinit var txtNumber:TextView
    lateinit var txtEmail:TextView
    lateinit var txtAddress:TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view= inflater.inflate(R.layout.fragment_profile, container, false)
        val sharedPreferences = (activity as Context).getSharedPreferences(getString(R.string.profile_data),
            AppCompatActivity.MODE_PRIVATE)
        imgProfilePhoto = view.findViewById(R.id.imgProfilePhoto)
        txtProfileName = view.findViewById(R.id.txtProfileName)
        txtProfileName.text = sharedPreferences.getString("username", null)
        txtNumber = view.findViewById(R.id.txtNumber)
        val mobileNum = sharedPreferences.getString("mobileNumber",null)
        txtNumber.text = "+91-${mobileNum}"
        txtEmail = view.findViewById(R.id.txtEmail)
        txtEmail.text= sharedPreferences.getString("userEmail",null)
        txtAddress = view.findViewById(R.id.txtAddress)
        txtAddress.text = sharedPreferences.getString("address",null)
        return view
    }
}