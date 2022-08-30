package com.htetarkarlinn.itvisionhub.Activities

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.net.toUri
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.htetarkarlinn.itvisionhub.MainActivity
import com.htetarkarlinn.itvisionhub.Models.CampModel
import com.htetarkarlinn.itvisionhub.Models.User
import com.htetarkarlinn.itvisionhub.Models.VeryfyUser
import com.htetarkarlinn.itvisionhub.R
import com.htetarkarlinn.itvisionhub.`object`.Mailer
import com.htetarkarlinn.itvisionhub.databinding.ActivityVerifyEmailBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class VerifyEmailActivity : AppCompatActivity() {
    private lateinit var binding :ActivityVerifyEmailBinding
    lateinit var user: User
    var code=0
    var img : Uri? =null
    var enterCode=""
    var verifyId=""
    lateinit var timer: CountDownTimer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityVerifyEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        user=intent.getSerializableExtra("user") as User
        code=intent.getIntExtra("code",1)
        img= intent.getStringExtra("img")?.toUri()
        binding.email.text=user.email
        val dbFb =Firebase.firestore
        val verifyUser=VeryfyUser(user.email,getCurrentDate())
        dbFb.collection("VerifyUser")
            .add(verifyUser)
            .addOnSuccessListener {
                verifyId=it.id
                Log.d("SS","Success")
            }.addOnFailureListener {
                Log.d("FF",it.message.toString())
            }
        timer = object:CountDownTimer(180000,1000){
            override fun onTick(millisUntilFinished: Long) {
                binding.count.text="Time remaining "+(millisUntilFinished/1000)/60 + ":" + (millisUntilFinished/1000)%60
            }

            override fun onFinish() {
                binding.count.visibility=View.INVISIBLE
                binding.resend.setTextColor(Color.RED)
                binding.resend.isEnabled=true
                code=1
            }
        }
        timer.start()
        binding.resend.setOnClickListener {
            timer.start()
            binding.resend.isEnabled=false
            binding.resend.setTextColor(getColor(R.color.colorPrimary))
            code = (100000..999999).shuffled().last()
            Mailer.sendMail(
                this,
                user.email,
                "IT Visionhub Email Verify",
                "Hi ${user.name}! Your Verification code is \n : ${code}. \n Thank For Enjoying Us"
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        /*Toast.makeText(
                            this,
                            "Mail send check e-mail",
                            Toast.LENGTH_SHORT
                        ).show()*/
                    },
                    {
                        Toast.makeText(this, "{${it.message}}", Toast.LENGTH_SHORT)
                            .show()
                    }
                )
        }
        binding.verifyLayout.setOnClickListener {
            //Toast.makeText(this, "${img}", Toast.LENGTH_SHORT).show()
            enterCode=binding.enterCode.text.toString()
            if (enterCode.equals("")&&enterCode.length<6) {
               /* Handler(Looper.getMainLooper()).postDelayed({
                                                            Mailer.sendMail(this,user.email,"Testing","No Code Click")
                },10000)*/
                Toast.makeText(this, "Please Enter Code", Toast.LENGTH_SHORT).show()
            } else {
            val param = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            binding.progressBar.layoutParams = param
            binding.progressBar.visibility = View.VISIBLE
            binding.verifyBtn.text = "Verifying"
            binding.verifyLayout.isEnabled = false
            if (enterCode.toInt()==code) {
                
                val auth=Firebase.auth
                auth.createUserWithEmailAndPassword(user.email,user.password)
                    .addOnCompleteListener { 
                        if (it.isSuccessful){

                            if (!img.toString().equals("null")){
                                ImageUploadAndDataSave(user)

                            }else{
                                DataSaveNoImage(user)
                            }
                        }else{
                            binding.progressBar.layoutParams = LinearLayout.LayoutParams(0,0)
                            binding.progressBar.visibility = View.INVISIBLE
                            binding.verifyBtn.text = "Verify"
                            binding.verifyLayout.isEnabled = true

                            CheckNework()
                        }
                    }
               
            }else{
                Toast.makeText(this, "Code invalid", Toast.LENGTH_SHORT).show()
                binding.progressBar.layoutParams = LinearLayout.LayoutParams(0,0)
                binding.progressBar.visibility = View.INVISIBLE
                binding.verifyBtn.text = "Verify"
                binding.verifyLayout.isEnabled = true
            }
        }
        }
        

    }

    private fun ImageUploadAndDataSave(user: User) {
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference(("/images/$filename"))
        ref.putFile(img!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    user.img=it.toString()
                    val db = Firebase.firestore

                    db.collection("users")
                        .add(user)
                        .addOnSuccessListener { documentReference ->

                            StoreInShareUser(user.email,user.password)
                            storeRole(user.role)
                            DeleteVerify(verifyId)

                        }
                        .addOnFailureListener { e ->
                            Log.w(ContentValues.TAG, "Error adding document", e)
                            binding.progressBar.layoutParams = LinearLayout.LayoutParams(0,0)
                            binding.progressBar.visibility = View.INVISIBLE
                            binding.verifyBtn.text = "Verify"
                            binding.verifyLayout.isEnabled = true
                            CheckNework()
                        }
                }
            }
            .addOnFailureListener{
                binding.progressBar.layoutParams = LinearLayout.LayoutParams(0,0)
                binding.progressBar.visibility = View.INVISIBLE
                binding.verifyBtn.text = "Verify"
                binding.verifyLayout.isEnabled = true
                CheckNework()
            }

    }

    private fun DataSaveNoImage(user: User) {
        val db = Firebase.firestore

        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->

                StoreInShareUser(user.email,user.password)
                storeRole(user.role)
                DeleteVerify(verifyId)

            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
                binding.progressBar.layoutParams = LinearLayout.LayoutParams(0,0)
                binding.progressBar.visibility = View.INVISIBLE
                binding.verifyBtn.text = "Verify"
                binding.verifyLayout.isEnabled = true
                Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show()
                CheckNework()
            }
    }

    private fun CheckNework() {
        val connectivityManager=this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifi=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobile=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        if (wifi!!.isConnectedOrConnecting){
            //binding.loginName.setError("")
        }else if (mobile!!.isConnectedOrConnecting){
        }else{
            Toast.makeText(this, "No Connection", Toast.LENGTH_SHORT).show()
        }
    }

    private fun storeRole(role: String) {
        val sp: SharedPreferences=getSharedPreferences("Role", MODE_PRIVATE)
        val edt : SharedPreferences.Editor=sp.edit()
        edt.putString("role",role)
        edt.apply()
    }

    private fun StoreInShareUser(email: String, password: String) {
        val preferences : SharedPreferences =getSharedPreferences("User",
            MODE_PRIVATE)
        val editor : SharedPreferences.Editor= preferences.edit()
        editor.putString("email",email)
        editor.putString("password",password)
        editor.apply()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    @SuppressLint("SimpleDateFormat")
    fun getCurrentDate():String{
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        return sdf.format(Date())
    }
    private fun DeleteVerify(userId : String){
        val fb=Firebase.firestore
        fb.collection("VerifyUser")
            .document(userId)
            .delete()
            .addOnSuccessListener {
                startActivity(Intent(this, MainActivity::class.java))
                Log.d(
                    ContentValues.TAG,
                    "DocumentSnapshot added with ID: ${it.toString()}"
                )
                finish()
            }
            .addOnFailureListener {

            }

    }
}