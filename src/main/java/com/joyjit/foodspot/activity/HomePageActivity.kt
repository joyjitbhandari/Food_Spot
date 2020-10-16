package com.joyjit.foodspot.activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.provider.Settings.Global.getString
import android.provider.Settings.Secure.getString
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.joyjit.foodspot.R
import com.joyjit.foodspot.fragment.*
import java.util.jar.Attributes

class HomePage : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolBar: androidx.appcompat.widget.Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView
    var previousMenuItem: MenuItem? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolBar = findViewById(R.id.toolBar)
        frameLayout = findViewById(R.id.frameLayout)
        navigationView = findViewById(R.id.navigationView)


        sharedPreferences = getSharedPreferences(getString(R.string.profile_data), MODE_PRIVATE)

        setUpToolbar()
        homePage()

        val actionBarDrawerToggle = ActionBarDrawerToggle(this,drawerLayout,R.string.open_drawer,R.string.close_drawer)

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        val header = navigationView.getHeaderView(0)
        val username = header.findViewById<TextView>(R.id.txtProfileName)
        val phoneNumber = header.findViewById<TextView>(R.id.txtNumber)

        username.text = sharedPreferences.getString("username", "N/A")
        phoneNumber.text = "+91-${sharedPreferences.getString("mobileNumber", "N/A")}"



        navigationView.setNavigationItemSelectedListener(){
            if(previousMenuItem!=null){
                previousMenuItem?.isChecked = false
            }
            it.isCheckable
            it.isChecked
            previousMenuItem = it
            when(it.itemId){
               R.id.home->{
                   homePage()
                   drawerLayout.closeDrawers()
               }
                R.id.profile->{
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayout,ProfileFragment()).commit()
                    supportActionBar?.title = "Profile"
                    drawerLayout.closeDrawers()
                }
                R.id.favourite->{
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayout,FavouriteFragment()).commit()
                    supportActionBar?.title = "Favourite Restaurants"
                    drawerLayout.closeDrawers()
                }
                R.id.orderHistory->{
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayout,OrderHistoryFragment()).commit()
                    supportActionBar?.title = "Order History"
                    drawerLayout.closeDrawers()
                }
                R.id.faq->{
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayout,
                        FAQFragment()
                    ).commit()
                    supportActionBar?.title = "FAQ"
                    drawerLayout.closeDrawers()
                }
                R.id.logOut->{
                    val dialog = AlertDialog.Builder(this,)
                    dialog.setTitle("Log Out")
                    dialog.setMessage("Are you sure...")

                    dialog.setPositiveButton("Ok"){text, listener->
                        sharedPreferences.edit().clear().apply()
                        startActivity(Intent(this,LoginPage::class.java))
                        finishAffinity()
                    }
                    dialog.setNegativeButton("Cancel"){text, listener->
                        drawerLayout.closeDrawers()

                    }
                    dialog.create()
                    dialog.show()
                }
            }
            return@setNavigationItemSelectedListener true
        }
    }
    private fun setUpToolbar() {
        setSupportActionBar(toolBar)
        supportActionBar?.title = "Restaurant Spot"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if(id == android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun homePage(){
        supportFragmentManager.beginTransaction().replace(R.id.frameLayout,HomeFragment()).commit()
        supportActionBar?.title = "Home"
        navigationView.setCheckedItem(R.id.home)
    }

    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawers()
        }else{
            val frag =supportFragmentManager.findFragmentById(R.id.frameLayout)
            when(frag) {
                !is HomeFragment -> homePage()
                else -> {
                    super.onBackPressed()
                }
            }
        }
    }
}