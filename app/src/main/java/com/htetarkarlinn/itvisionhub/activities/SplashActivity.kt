package com.htetarkarlinn.itvisionhub.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.htetarkarlinn.itvisionhub.MainActivity
import com.htetarkarlinn.itvisionhub.`object`.Config
import com.htetarkarlinn.itvisionhub.`object`.Mailer
import com.htetarkarlinn.itvisionhub.databinding.ActivitySplashBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SplashActivity : AppCompatActivity() {
    private lateinit var binding :ActivitySplashBinding
    private var todayDateTime=""

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        todayDateTime=getCurrentDate()

        //window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        if (Config.isNetworkConnected(this)) {
            sendRecommendMail()
        }else{
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }



    }

    private fun goLogin() {
        startActivity(Intent(this,LoginActivity::class.java))
    }

    private fun goMain() {
        startActivity(Intent(this,MainActivity::class.java))
    }
    @SuppressLint("CheckResult", "SuspiciousIndentation")
    private fun sendRecommendMail() {
        lifecycleScope.launch(Dispatchers.IO) {
        var deleteId: String
        val fb= Firebase.firestore
            fb.collection("VerifyUser")
            .get()
            .addOnSuccessListener {
                //Toast.makeText(applicationContext, "Checking", Toast.LENGTH_SHORT).show()
                for (u in it){
                    deleteId=u.id
                    val dateTime=u.get("dateTime").toString()
                    val date= dateTime.substringBefore("T")

                    //  Toast.makeText(itemView.context, min_sec, Toast.LENGTH_SHORT).show()
                    val year=date.substringBefore("-").toInt()
                    val monthDay=date.substringAfter("-")
                    val month=monthDay.substringBefore("-").toInt()
                    val day=monthDay.substringAfter("-").toInt()

                    val todayDate= todayDateTime.substringBefore("T")
                    val todayTime=todayDateTime.substringAfter("T")
                    val todayHr=todayTime.substringBefore(":")
                    //  Toast.makeText(itemView.context, min_sec, Toast.LENGTH_SHORT).show()
                    val todayYear=todayDate.substringBefore("-").toInt()
                    val todayMonthDay=todayDate.substringAfter("-")
                    val todayMonth=todayMonthDay.substringBefore("-").toInt()
                    val todayDay=todayMonthDay.substringAfter("-").toInt()
                    //Toast.makeText(this, "${todayHr} ${hr}", Toast.LENGTH_SHORT).show()
                    if (todayYear==year && todayMonth==month && todayDay <= day+3 && todayDay > day && todayHr.toInt() >=8){
                        // Toast.makeText(this, "condition true", Toast.LENGTH_SHORT).show()
                        Mailer.sendMail(applicationContext,u.get("email").toString(),"Verify Email Recommendation","Your verification on $date is not successful. So we recommend to verify your email")
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({

                            },{

                            })
                        fb.collection("VerifyUser")
                            .document(deleteId)
                            .delete()
                    }else if (todayYear>=year && todayMonth>=month && todayDay > day+3 && todayDay > day){
                        // Toast.makeText(this, "wrong", Toast.LENGTH_SHORT).show()
                        fb.collection("VerifyUser")
                            .document(deleteId)
                            .delete()
                    }
                }
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
        }
    }
    @SuppressLint("SimpleDateFormat")
    private fun getCurrentDate() = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(Date())


}