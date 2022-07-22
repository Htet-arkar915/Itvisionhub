package com.htetarkarlinn.itvisionhub.Activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import com.htetarkarlinn.itvisionhub.MainActivity
import com.htetarkarlinn.itvisionhub.databinding.ActivitySplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private lateinit var binding :ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        //window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val preferences : SharedPreferences =getSharedPreferences("Login", MODE_PRIVATE)
        lifecycleScope.launch {
            delay(2500)
            if (preferences.contains("LoginSuccess")){
                // Toast.makeText(this, "Login success", Toast.LENGTH_SHORT).show()
                goMain()
                finish()
            }else{
                goLogin()
                finish()
            }
        }


    }

    private fun goLogin() {
        startActivity(Intent(this,LoginActivity::class.java))
    }

    private fun goMain() {
        startActivity(Intent(this,MainActivity::class.java))
    }
}