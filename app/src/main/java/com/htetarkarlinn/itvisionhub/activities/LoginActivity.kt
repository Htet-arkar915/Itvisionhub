package com.htetarkarlinn.itvisionhub.activities
import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.htetarkarlinn.itvisionhub.MainActivity
import com.htetarkarlinn.itvisionhub.models.User
import com.htetarkarlinn.itvisionhub.R
import com.htetarkarlinn.itvisionhub.`object`.Mailer
import com.htetarkarlinn.itvisionhub.databinding.ActivityLoginBinding
import com.htetarkarlinn.itvisionhub.databinding.AdminRegisterLayoutBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.InputStream
import java.io.Serializable
import java.util.*

class LoginActivity : AppCompatActivity()  {
    var uri : Uri? = null
    private lateinit var b :AdminRegisterLayoutBinding
    private lateinit var binding: ActivityLoginBinding
    var emailList : MutableList<String> =ArrayList()
    var role = ""
    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        //window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        //window.addFlags(WindowManager.LayoutParams.MATCH_PARENT)
        if (isNetworkConnected(this)){

            collectUserEmail()
        }else{
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }
        binding.loginLayout.setOnClickListener{
            if (isNetworkConnected(this)) {
                val email = binding.loginName.text.toString()
                val password = binding.loginPass.text.toString()
                if (email == "" && password == "") {
                    Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val param = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    binding.progressBar.layoutParams = param
                    binding.progressBar.visibility = View.VISIBLE
                    binding.loginBtn.text = "Checking"
                    binding.loginLayout.isEnabled = false
                    val auth = Firebase.auth
                    if (isNetworkConnected(this)) {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {

                                    val fs = Firebase.firestore
                                    fs.collection("users")
                                        .get().addOnSuccessListener {
                                            for (doc in it) {
                                                if (doc["email"].toString() == email && doc["password"].toString() == password
                                                ) {
                                                    role = doc["role"].toString()
                                                    storeRole(role)
                                                    storeInShareUser(email, password)
                                                    startActivity(
                                                        Intent(
                                                            this,
                                                            MainActivity::class.java
                                                        )
                                                    )
                                                    // Sign in success, update UI with the signed-in user's information
                                                    Log.d(TAG, "signInWithEmail:success")
                                                    val user = auth.currentUser
                                                    // updateUI(user)
                                                    finish()
                                                }
                                            }
                                        }

                                } else {
                                    // If sign in fails, display a message to the user.
                                    binding.progressBar.visibility = View.INVISIBLE
                                    binding.loginBtn.text = "Login"
                                    binding.loginLayout.isEnabled = true
                                    binding.progressBar.layoutParams =
                                        LinearLayout.LayoutParams(0, 0)
                                    Log.w(TAG, "signInWithEmail:failure", task.exception)

                                }
                            }
                        //Toast.makeText(this, role, Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.INVISIBLE
                        binding.loginBtn.text = "Login"
                        binding.loginLayout.isEnabled = true
                        binding.progressBar.layoutParams =
                            LinearLayout.LayoutParams(0, 0)
                    }
                }
            }else{
                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
            }
        }
        binding.signupAdmin.setOnClickListener{
            val builder = AlertDialog.Builder(this,R.style.CustomAlertDialog)
                .create()
            val view = layoutInflater.inflate(R.layout.admin_register_layout,null)
            b=AdminRegisterLayoutBinding.bind(view)
            b.registerDegree.visibility=View.INVISIBLE
            b.dCard.visibility=View.INVISIBLE
            b.dCard.layoutParams=LinearLayout.LayoutParams(0,0)
            b.registerDegree.layoutParams=FrameLayout.LayoutParams(0,0)
            // val  button = view.findViewById<Button>(R.id.dialogDismiss_button)
            builder.setView(view)
            b.closeDialog.setOnClickListener {
                builder.dismiss()
            }
            /*    button.setOnClickListener {
                    builder.dismiss()
                }*/
            b.profilePhoto.setOnClickListener{
                requestPermit()
            }
            b.registerLay.setOnClickListener {
                // if (uri==null) Toast.makeText(this, "Please Select Your Profile", Toast.LENGTH_SHORT).show()
                val emailPattern = "(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"
                val role="Admin"
                val name=b.rName.text.toString()
                val phone=b.rPhone.text.toString()
                val email=b.registerEmail.text.toString()
                val password= b.registerPassword.text.toString()
                val degree ="Admin"
                val camp=""
                val request=""
                val noti=""
                if (password.length<6){
                    Toast.makeText(this, "Password must be 6 digit", Toast.LENGTH_SHORT).show()
                }else {
                    if (name == "" || phone == "" || email == "") {
                        Toast.makeText(this, "Please Enter Your Information", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        val param = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        b.pgb.layoutParams=param
                        b.pgb.visibility = View.VISIBLE
                        b.Register.text = "Registering"
                        b.registerLay.isEnabled = false

                        val auth = Firebase.auth


                        if (emailList.contains(email)){
                            Toast.makeText(
                                baseContext, "Your email already exit",
                                Toast.LENGTH_SHORT
                            ).show()
                            b.pgb.layoutParams=LinearLayout.LayoutParams(0,0)
                            b.pgb.visibility = View.INVISIBLE
                            b.Register.text = "SignUp"
                            b.registerLay.isEnabled = true
                        }else{
                            if (email.matches(emailPattern.toRegex())) {
                                val code = (100000..999999).shuffled().last()
                                Mailer.sendMail(
                                    this,
                                    email,
                                    "IT Visionhub Email Verify",
                                    "Hi ${name}! Your Verification code is \n : ${code}. \n Thank For Enjoying Us"
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
                                val imgurl = "This is img"
                                val user = User(
                                    name,
                                    phone,
                                    email,
                                    password,
                                    imgurl,
                                    role,
                                    degree,
                                    camp,
                                    request,
                                    noti
                                )
                                val intent = Intent(this, VerifyEmailActivity::class.java)
                                intent.putExtra("user", user as Serializable)
                                intent.putExtra("img", uri.toString())
                                intent.putExtra("code",code)
                                startActivity(intent)
                                builder.dismiss()
                            }else{
                                Toast.makeText(this, "Your email is not valid", Toast.LENGTH_SHORT).show()
                                b.pgb.layoutParams=LinearLayout.LayoutParams(0,0)
                                b.pgb.visibility = View.INVISIBLE
                                b.Register.text = "SignUp"
                                b.registerLay.isEnabled = true
                            }
                        }

                    }
                }
            }
            builder.setCanceledOnTouchOutside(false)
            builder.show()

        }
        binding.signupStudent.setOnClickListener{
            if (isNetworkConnected(this)){
            val builder = AlertDialog.Builder(this,R.style.CustomAlertDialog)
                .create()
            val view = layoutInflater.inflate(R.layout.admin_register_layout,null)
            // val  button = view.findViewById<Button>(R.id.dialogDismiss_button)
            b=AdminRegisterLayoutBinding.bind(view)
            builder.setView(view)
            b.closeDialog.setOnClickListener {
                builder.dismiss()
            }
            b.profilePhoto.setOnClickListener{
                requestPermit()
            }
            b.registerLay.setOnClickListener {
                val emailPattern = "(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"
                val role="Student"
                val email=b.registerEmail.text.toString()
                val password= b.registerPassword.text.toString()
                val name=b.rName.text.toString()
                val phone=b.rPhone.text.toString()
                val degree=b.registerDegree.text.toString()
                val confirmPassword=b.confirmPassword.text.toString()
                val camp=""
                val req=""
                val noti=""
                if (password.length<6){
                    Toast.makeText(this, "Password must be 6 digit", Toast.LENGTH_SHORT).show()
                }else if(password != confirmPassword){
                    Toast.makeText(this, "Password are not the same", Toast.LENGTH_SHORT).show()
                }else{
                    if (name=="" || phone== "" || email == "" || degree==""){
                        Toast.makeText(this, "Please Enter Your Information", Toast.LENGTH_SHORT).show()
                    }else {
                        val param = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        b.pgb.layoutParams=param
                        b.pgb.visibility = View.VISIBLE
                        b.Register.text = "Registering"
                        b.registerLay.isEnabled = false

                        if (emailList.contains(email)){
                            Toast.makeText(
                                baseContext, "Your email already exit",
                                Toast.LENGTH_SHORT
                            ).show()
                            b.pgb.layoutParams=LinearLayout.LayoutParams(0,0)
                            b.pgb.visibility = View.INVISIBLE
                            b.Register.text = "SignUp"
                            b.registerLay.isEnabled = true
                        }else{
                            if (email.matches(emailPattern.toRegex())) {
                                val code = (100000..999999).shuffled().last()
                                Mailer.sendMail(
                                    this,
                                    email,
                                    "IT VisionHub Email Verify",
                                    "Hi ${name}! Your Verification code is \n : ${code}. \n Thank For Enjoying Us"
                                )
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                        {
                                            Toast.makeText(
                                                this,
                                                "Mail send check e-mail",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        },
                                        {
                                            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                    )
                                val imgurl = "This is img"
                                val user = User(
                                    name,
                                    phone,
                                    email,
                                    password,
                                    imgurl,
                                    role,
                                    degree,
                                    camp,
                                    req,
                                    noti
                                )
                                val intent = Intent(this, VerifyEmailActivity::class.java)
                                intent.putExtra("user", user as Serializable)
                                intent.putExtra("img", uri.toString())
                                intent.putExtra("code",code)
                                startActivity(intent)
                                builder.dismiss()
                            }else{
                                Toast.makeText(this, "Your email is not valid", Toast.LENGTH_SHORT).show()
                                b.pgb.layoutParams=LinearLayout.LayoutParams(0,0)
                                b.pgb.visibility = View.INVISIBLE
                                b.Register.text = "SignUp"
                                b.registerLay.isEnabled = true
                            }
                        }

                    }
                }

            }
            builder.setCanceledOnTouchOutside(false)
            builder.show()

        }else{
                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }
        }
    }

    private fun collectUserEmail() {
        val db=Firebase.firestore
        db.collection("users")
            .get()
            .addOnSuccessListener {
                for (doc in it){
                    emailList.add(doc["email"].toString())
                }
            }
            .addOnFailureListener {

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
        //startActivityForResult(Intent.createChooser(intent,"Please Choose Image"),100)
        activityResult.launch(Intent.createChooser(intent,"Choose Image"))
    }

    private val activityResult=
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
           // Toast.makeText(this, "${result.resultCode}", Toast.LENGTH_SHORT).show()

            if (result.resultCode== RESULT_OK && result.data!= null){
                //Toast.makeText(this, "$uri", Toast.LENGTH_SHORT).show()

                result.data!!.data.let {
                    uri=it
                    val inputStr : InputStream? =contentResolver.openInputStream(uri!!)
                    val bitmap =BitmapFactory.decodeStream(inputStr)
                    b.profilePhoto.setImageBitmap(bitmap)
                }
            }
        }


    private fun storeRole(role: String) {
       // Toast.makeText(this, role, Toast.LENGTH_SHORT).show()
        val sp: SharedPreferences=getSharedPreferences("Role", MODE_PRIVATE)
        val edt : SharedPreferences.Editor=sp.edit()
        edt.putString("role",role)
        edt.apply()
    }

    private fun storeInShareUser(email: String, password: String) {

        val preferences : SharedPreferences =getSharedPreferences("User",
            MODE_PRIVATE)
        val editor : SharedPreferences.Editor= preferences.edit()
        editor.putString("email",email)
        editor.putString("password",password)
        editor.apply()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun isNetworkConnected(context: Context): Boolean {
        //1
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        //2
        val activeNetwork = connectivityManager.activeNetwork
        //3
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        //Toast.makeText(context, networkCapabilities.toString(), Toast.LENGTH_SHORT).show()
        //4
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}

