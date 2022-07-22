package com.htetarkarlinn.itvisionhub

import android.app.Fragment
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.contextaware.ContextAware
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.htetarkarlinn.itvisionhub.Fragment.HomeFragment
import com.htetarkarlinn.itvisionhub.databinding.ActivityLoginBinding
import com.htetarkarlinn.itvisionhub.databinding.ActivityMainBinding
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
     var email=""
     var password=""
    var role=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val preferences: SharedPreferences = getSharedPreferences(
            "Login",
            MODE_PRIVATE
        )
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putBoolean("LoginSuccess", true)
        editor.apply()
        // supportActionBar?.hide()
        val pre: SharedPreferences = getSharedPreferences("User", MODE_PRIVATE)
         password=pre.getString("password","string").toString()
         email=pre.getString("email","string").toString()

        val prefer :SharedPreferences=getSharedPreferences("Role", MODE_PRIVATE)
        role=prefer.getString("role","string").toString()
        //Toast.makeText(this, pre.getString("email","string") , Toast.LENGTH_SHORT).show()

/*
        if (role.equals("Admin")){
            binding.navView.visibility=View.VISIBLE
            binding.stunavView.visibility= View.INVISIBLE
            var navView: BottomNavigationView = binding.navView
            val navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main)
            navController.setGraph(R.navigation.mobile_navigation)
            val appBarConfiguration = AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_class, R.id.nav_list, R.id.nav_profile
            ).build()
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
            NavigationUI.setupWithNavController(navView, navController)
            navView.isItemHorizontalTranslationEnabled
            removeToolbar(navController)
        }else{
            binding.navView.visibility=View.INVISIBLE
            binding.stunavView.visibility= View.VISIBLE
            val navView :BottomNavigationView =binding.stunavView
            val navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main)
            val appBarConfiguration = AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_class, R.id.nav_profile
            ).build()
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
            NavigationUI.setupWithNavController(navView, navController)
            navView.isItemHorizontalTranslationEnabled
            removeToolbar(navController)
        }
*/

        val db= Firebase.firestore

        db.collection("users")
            .get().addOnSuccessListener{
                for (doc in it){
                    if (doc["email"]?.equals(email)!! && doc["password"]?.equals(password)!!){
                        StoreInShareUser(doc["role"].toString())
                        if (doc["role"].toString().equals("Admin") ){
                            binding.navView.visibility=View.VISIBLE
                            binding.stunavView.visibility= View.INVISIBLE
                            var navView: BottomNavigationView = binding.navView
                            val navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main)
                            navController.setGraph(R.navigation.mobile_navigation)
                            val appBarConfiguration = AppBarConfiguration.Builder(
                                R.id.nav_home, R.id.nav_class, R.id.nav_list, R.id.nav_profile
                            ).build()
                            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
                            NavigationUI.setupWithNavController(navView, navController)
                            navView.isItemHorizontalTranslationEnabled
                            removeToolbar(navController)
                        }else{
                            binding.navView.visibility=View.INVISIBLE
                            binding.stunavView.visibility= View.VISIBLE
                            val navView :BottomNavigationView =binding.stunavView
                            val navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main)
                            val appBarConfiguration = AppBarConfiguration.Builder(
                                R.id.nav_home, R.id.nav_class, R.id.nav_profile
                            ).build()
                            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
                            NavigationUI.setupWithNavController(navView, navController)
                            navView.isItemHorizontalTranslationEnabled
                            removeToolbar(navController)
                        }
                    }
                }
            }

            .addOnFailureListener {
                Toast.makeText(this, "Found error $it", Toast.LENGTH_SHORT).show()
            }

        //Toast.makeText(this, role, Toast.LENGTH_SHORT).show()

    }
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
    private  fun loadFragment(fragment: androidx.fragment.app.Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment_activity_main,fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    private fun removeToolbar(navController: NavController) {
        navController.addOnDestinationChangedListener{ _,destination ,_ ->
            when(destination.id){
                R.id.nav_home ->{supportActionBar?.show()}
                R.id.nav_profile->{supportActionBar?.hide()
               /* val window=this.window
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    window.statusBarColor=ContextCompat.getColor(this,R.color.black)*/
                }
                R.id.nav_list->{supportActionBar?.show()}
                R.id.nav_class->{supportActionBar?.show()}
            }
        }
    }
    private fun StoreInShareUser(role : String) {

        val preferences : SharedPreferences =getSharedPreferences("Role",
            MODE_PRIVATE)
        val editor : SharedPreferences.Editor= preferences.edit()
        editor.putString("role",role)
        editor.apply()
    }



}
