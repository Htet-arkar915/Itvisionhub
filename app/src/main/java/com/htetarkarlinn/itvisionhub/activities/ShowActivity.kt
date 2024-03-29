package com.htetarkarlinn.itvisionhub.activities

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.htetarkarlinn.itvisionhub.models.VideoCategory
import com.htetarkarlinn.itvisionhub.models.VideoModel
import com.htetarkarlinn.itvisionhub.R
import com.htetarkarlinn.itvisionhub.adapter.ShowVideoAdapter
import com.htetarkarlinn.itvisionhub.databinding.ActivityShowBinding
import com.htetarkarlinn.itvisionhub.databinding.AddVideoDialogBinding

class ShowActivity : AppCompatActivity() {
    private lateinit var binding : ActivityShowBinding
    private lateinit var videoCategory : VideoCategory
    private lateinit var catId : String
    var role=""
    private var showVideoAdapter : ShowVideoAdapter?=null
    private var videoList : MutableList<VideoModel> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityShowBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sh :SharedPreferences=getSharedPreferences("Role", MODE_PRIVATE)
        role=sh.getString("role","role").toString()
        videoCategory= intent.getParcelableExtra<VideoCategory>("category")!!
        catId=intent.getStringExtra("catId").toString()
        supportActionBar?.title=videoCategory.name.toString()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        videoList=videoCategory.videoList as MutableList<VideoModel>
        //Toast.makeText(this, "${videoList[0].title}", Toast.LENGTH_SHORT).show()
        binding.videoRecycle.layoutManager=LinearLayoutManager(this)
        showVideoAdapter= ShowVideoAdapter(this, videoCategory)
        binding.videoRecycle.adapter=showVideoAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (role == "Admin") {
            menuInflater.inflate(R.menu.addmenu, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                return true
            }
            R.id.add_video ->{
                Toast.makeText(this, "AddVideo", Toast.LENGTH_SHORT).show()
                showAddVideoDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAddVideoDialog() {

        var videoId=""
        var videoTitle=""
        val builder= AlertDialog.Builder(this, R.style.CustomAlertDialog).create()
        val view=layoutInflater.inflate(R.layout.add_video_dialog,null)
        val b= AddVideoDialogBinding.bind(view)
        builder.setView(view)

        b.btnCancel.setOnClickListener {
            builder.dismiss()
        }
        b.btnAdd.setOnClickListener {
            videoId=b.videoId.text.toString()
            videoTitle=b.videoTitle.text.toString()
            if (videoId == "" && videoTitle == ""){
                Toast.makeText(this, "Enter both of Video ID and Name", Toast.LENGTH_SHORT).show()
            }else{
                val videoModel=VideoModel(videoId,videoTitle)
                videoList.add(videoModel)
                updateVideoList(b,builder, videoList)
            }
        }
        builder.setCancelable(false)
        builder.show()
    }

    private fun updateVideoList(
        b:AddVideoDialogBinding,
        builder: AlertDialog,
        videoList: MutableList<VideoModel>
    ) {
        //val video_list : ArrayList<HashMap<String,String>> =videoList as ArrayList<HashMap<String, String> /* = java.util.HashMap<kotlin.String, kotlin.String> */> /* = java.util.ArrayList<java.util.HashMap<kotlin.String, kotlin.String>> */
        val db=Firebase.firestore
        db.collection("Video")
            .document(catId)
            .update("videoList",videoList)
            .addOnSuccessListener {
                builder.dismiss()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Check Connection", Toast.LENGTH_SHORT).show()
            }
    }
}