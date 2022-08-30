package com.htetarkarlinn.itvisionhub

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.htetarkarlinn.itvisionhub.Models.User
import com.htetarkarlinn.itvisionhub.`object`.Mailer
import com.htetarkarlinn.itvisionhub.databinding.ActivityMainBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
     var email=""
     var password=""
    var role=""
    var userid=""
    var todayDateTime=""
    var idlist : MutableList<String> = arrayListOf()
    var user_list : MutableList<User> = arrayListOf()
    lateinit var notificationChannel: NotificationChannel
    lateinit var notificationManager: NotificationManager
    lateinit var builder: Notification.Builder
    private val channelId = "12345"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager
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
        todayDateTime=getCurrentDate()
        //Toast.makeText(this, todayDateTime, Toast.LENGTH_SHORT).show()
        sendRecommendMail()
        if (role.equals("Admin") ){
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
            //Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show()
            // ShowNoti(user_list,idlist)
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

        val db= Firebase.firestore
        db.collection("users")
            .get().addOnSuccessListener{
                for (doc in it){
                    val user = User(doc["name"].toString(),doc["phone"].toString(),doc["email"].toString(),doc["password"].toString()
                        ,doc["img"].toString(),doc["role"].toString(),doc["degree"].toString(),doc["camp"].toString(),doc["request"].toString(),doc["noti"].toString())

                    if (!user.role.toString().equals("Admin")){
                        user_list.add(user)
                        idlist.add(doc.id.toString())
                    }
                    if (doc["email"]?.equals(email)!! && doc["password"]?.equals(password)!!){
                        StoreInShareUser(doc["role"].toString(),doc.id.toString(),doc["camp"].toString())


                    }
                }

                val p : SharedPreferences =getSharedPreferences("Role",
                    MODE_PRIVATE)
                if (p.getString("role","").equals("Admin")){
                    ShowNoti(user_list,idlist)
                    //Toast.makeText(this, user_list.size.toString(), Toast.LENGTH_SHORT).show()
                }

            }

            .addOnFailureListener {
                Toast.makeText(this, "Found error $it", Toast.LENGTH_SHORT).show()
            }

        //Toast.makeText(this, role, Toast.LENGTH_SHORT).show()

    }

    private fun sendRecommendMail() {
        var delete_id=""
        val fb= Firebase.firestore
        fb.collection("VerifyUser")
            .get()
            .addOnSuccessListener {
                for (u in it){
                    delete_id=u.id
                    val date_time=u.get("dateTime").toString()
                    val date= date_time.substringBefore("T")
                    //  Toast.makeText(itemView.context, min_sec, Toast.LENGTH_SHORT).show()
                    val year=date.substringBefore("-").toInt()
                    val month_day=date.substringAfter("-")
                    val month=month_day.substringBefore("-").toInt()
                    val day=month_day.substringAfter("-").toInt()

                    val today_date= todayDateTime.substringBefore("T")
                    val today_time=todayDateTime.substringAfter("T")
                    val today_hr=today_time.substringBefore(":")
                    //  Toast.makeText(itemView.context, min_sec, Toast.LENGTH_SHORT).show()
                    val today_year=today_date.substringBefore("-").toInt()
                    val today_month_day=today_date.substringAfter("-")
                    val today_month=today_month_day.substringBefore("-").toInt()
                    val today_day=today_month_day.substringAfter("-").toInt()
                    //Toast.makeText(this, "${today_hr} ${hr}", Toast.LENGTH_SHORT).show()
                    if (today_year==year && today_month==month && today_day <= day+3 && today_day > day && today_hr.toInt() >=8){
                       // Toast.makeText(this, "condition true", Toast.LENGTH_SHORT).show()
                        Mailer.sendMail(this,u.get("email").toString(),"Verify Email Recommendation","Your verification on ${date} is not successful. So we recommend to verify your email")
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({

                            },{

                            })
                        fb.collection("VerifyUser")
                            .document(delete_id)
                            .delete()
                    }else if (today_year>=year && today_month>=month && today_day > day+3 && today_day > day){
                       // Toast.makeText(this, "wrong", Toast.LENGTH_SHORT).show()
                        fb.collection("VerifyUser")
                            .document(delete_id)
                            .delete()
                    }
                }
            }
    }

    private fun ShowNoti(userList: MutableList<User>, idlist: MutableList<String>) {
        for (user in userList){
            //Toast.makeText(this, user.noti.toString(), Toast.LENGTH_SHORT).show()
            if (user.noti.toString().lowercase().equals("yes")){
                val intent = Intent(this, LauncherActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                var userid=idlist[user_list.indexOf(user)]
                var alarm=RingtoneManager.getActualDefaultRingtoneUri(this,RingtoneManager.TYPE_NOTIFICATION)
                val description="${user.name} want to attend in ${user.request}"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
                    notificationChannel.lightColor = Color.BLUE
                    notificationChannel.enableVibration(true)
                    notificationManager.createNotificationChannel(notificationChannel)
                    builder = Notification.Builder(this, channelId).setContentTitle("Student Request")
                        .setContentText(description)
                        .setColor(R.color.colorPrimary)
                        .setSmallIcon(R.drawable.acc_profile)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setSound(alarm)
                        .setContentIntent(pendingIntent)
                    //builder.setVibrate(new long[0]);
                }
                notificationManager.notify(12345, builder.build())
                UpdateForNoti(userid)

            }
        }
    }

    private fun UpdateForNoti(userid: String) {
        val db= Firebase.firestore
        db.collection("users")
            .document(userid)
            .update("noti","")
            .addOnSuccessListener {
                //Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
               // Toast.makeText(this, "Fail", Toast.LENGTH_SHORT).show()
            }
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
                R.id.nav_home ->{
                    supportActionBar?.hide()
                  //  supportActionBar?.title="ITVision Hub Lessons"
                   // window.decorView.systemUiVisibility=View.SYSTEM_UI_FLAG_FULLSCREEN
                }
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
    private fun StoreInShareUser(role: String, id: String,camp : String) {

        val preferences : SharedPreferences =getSharedPreferences("Role",
            MODE_PRIVATE)
        val editor : SharedPreferences.Editor= preferences.edit()
        editor.putString("role",role)
        editor.putString("id",id)
        editor.putString("camp",camp)
        editor.apply()
    }

   /* @SuppressLint("SimpleDateFormat")
    fun getCurrentDate():String{
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        return sdf.format(Date())
    }*/
   private fun getCurrentDate() = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(Date())


}
