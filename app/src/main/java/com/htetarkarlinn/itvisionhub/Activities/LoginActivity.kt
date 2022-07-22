package com.htetarkarlinn.itvisionhub.Activities
import android.Manifest
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.edit
import androidx.core.view.isVisible
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.htetarkarlinn.itvisionhub.MainActivity
import com.htetarkarlinn.itvisionhub.Models.User
import com.htetarkarlinn.itvisionhub.R
import com.htetarkarlinn.itvisionhub.databinding.ActivityLoginBinding
import com.htetarkarlinn.itvisionhub.databinding.AdminRegisterLayoutBinding
import java.io.InputStream
import java.net.URI
import java.util.*

class LoginActivity : AppCompatActivity()  {
    var uri : Uri? = null
    private lateinit var b :AdminRegisterLayoutBinding
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        binding.loginBtn.setOnClickListener{
            val email=binding.loginName.text.toString()
            val password=binding.loginPass.text.toString()
            val auth = Firebase.auth
            // Toast.makeText(this, binding.loginName.text, Toast.LENGTH_SHORT).show()
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        var role=""
                        val fs=Firebase.firestore
                        fs.collection("users")
                            .get().addOnSuccessListener {
                                for (doc in it){
                                    if (doc["email"].toString().equals(email) && doc["password"].toString().equals(password)){
                                        role=doc["role"].toString()
                                    }
                                }
                            }
                        storeRole(role)
                        StoreInShareUser(email,password)
                        startActivity(Intent(this, MainActivity::class.java))
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        val user = auth.currentUser
                        // updateUI(user)
                        finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                        //updateUI(null)
                    }
                }

        }
        binding.signupAdmin.setOnClickListener{
            val builder = AlertDialog.Builder(this,R.style.CustomAlertDialog)
                .create()
            val view = layoutInflater.inflate(R.layout.admin_register_layout,null)
            b=AdminRegisterLayoutBinding.bind(view)
            b.registerDegree.isVisible=false
            // val  button = view.findViewById<Button>(R.id.dialogDismiss_button)
            builder.setView(view)
            /*    button.setOnClickListener {
                    builder.dismiss()
                }*/
            b.profilePhoto.setOnClickListener{
                requestPermit()
            }
            b.Register.setOnClickListener {
                // if (uri==null) Toast.makeText(this, "Please Select Your Profile", Toast.LENGTH_SHORT).show()
                val role="Admin"
                val name=b.rName.text.toString()
                val phone=b.rPhone.text.toString()
                val email=b.registerEmail.text.toString()
                val password= b.registerPassword.text.toString()
                val degree ="Admin"
                val camp=""
                if (password.length<6){
                    Toast.makeText(this, "Password must be 6 digit", Toast.LENGTH_SHORT).show()
                }else {
                    if (name == "" || phone == "" || email == "" || password == "") {
                        Toast.makeText(this, "Please Enter Your Information", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        val auth = Firebase.auth

                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {

                                    val imgurl = "hello this is image"
                                    val user = User(
                                        name,
                                        phone,
                                        email,
                                        password,
                                        imgurl,
                                        role,
                                        degree,
                                        camp
                                    )
                                    if (uri != null) {
                                        ImageUploadAndDataSave(user)
                                    } else {
                                        DataSaveNoImage(user)
                                    }
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success")

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                                    Toast.makeText(
                                        baseContext, "Authentication failed.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    // updateUI(null)
                                }
                            }
                        builder.dismiss()
                    }
                }
            }
            builder.setCanceledOnTouchOutside(false)
            builder.show()

        }
        binding.signupStudent.setOnClickListener{
            val builder = AlertDialog.Builder(this,R.style.CustomAlertDialog)
                .create()
            val view = layoutInflater.inflate(R.layout.admin_register_layout,null)
            // val  button = view.findViewById<Button>(R.id.dialogDismiss_button)
            b=AdminRegisterLayoutBinding.bind(view)
            builder.setView(view)
            /*    button.setOnClickListener {
                    builder.dismiss()
                }*/
            b.profilePhoto.setOnClickListener{
                requestPermit()
            }
            b.Register.setOnClickListener {
                val role="Student"
                val email=b.registerEmail.text.toString()
                val password= b.registerPassword.text.toString()
                val name=b.rName.text.toString()
                val phone=b.rPhone.text.toString()
                val degree=b.registerDegree.text.toString()
                val camp=""
                if (password.length<6){
                    Toast.makeText(this, "Password must be 6 digit", Toast.LENGTH_SHORT).show()
                }else{
                if (name=="" && phone== "" && email == "" && password=="" && degree==""){
                    Toast.makeText(this, "Please Enter Your Information", Toast.LENGTH_SHORT).show()
                }else {
                    val auth = Firebase.auth

                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {

                                val imgurl = "hello this is image"
                                val user = User(name, phone, email, password, imgurl, role,degree,camp)
                                if (uri != null) {
                                    ImageUploadAndDataSave(user)
                                }else {
                                    DataSaveNoImage(user)
                                }
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success")

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                                Toast.makeText(
                                    baseContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                // updateUI(null)
                            }
                        }
                    builder.dismiss()
                }
            }

            }
            builder.setCanceledOnTouchOutside(false)
            builder.show()

        }
    }

    private fun DataSaveNoImage(user: User) {
        val db = Firebase.firestore

        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->

                StoreInShareUser(user.email,user.password)
                storeRole(user.role)
                startActivity(Intent(this, MainActivity::class.java))
                Log.d(
                    TAG,
                    "DocumentSnapshot added with ID: ${documentReference.id}"
                )
                finish()

            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    private fun requestPermit() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),101
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            101 ->{
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permission Deny", Toast.LENGTH_SHORT).show()
                }else{
                    startImagePick()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun startImagePick() {
        val intent= Intent(Intent.ACTION_PICK)
        intent.type="image/*"
        startActivityForResult(Intent.createChooser(intent,"Title Of Choose"),100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode== 100 && resultCode ==Activity.RESULT_OK && data!= null){
            uri=data.data
            val inputStr : InputStream? =contentResolver.openInputStream(uri!!)
            val bitmap =BitmapFactory.decodeStream(inputStr)
            b.profilePhoto.setImageBitmap(bitmap)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun ImageUploadAndDataSave(user: User) {
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference(("/images/$filename"))
        ref.putFile(uri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    user.img=it.toString()
                    val db = Firebase.firestore

                    db.collection("users")
                        .add(user)
                        .addOnSuccessListener { documentReference ->

                            StoreInShareUser(user.email,user.password)
                            storeRole(user.role)
                            startActivity(Intent(this, MainActivity::class.java))
                            Log.d(
                                TAG,
                                "DocumentSnapshot added with ID: ${documentReference.id}"
                            )
                            finish()

                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error adding document", e)
                        }
                }
            }
            .addOnFailureListener{
                Toast.makeText(this, "Image Upload Fail ${it.message}", Toast.LENGTH_SHORT).show()
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
}

