package com.htetarkarlinn.itvisionhub.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.htetarkarlinn.itvisionhub.models.CampModel
import com.htetarkarlinn.itvisionhub.models.User
import com.htetarkarlinn.itvisionhub.R
import com.htetarkarlinn.itvisionhub.adapter.StudentInfoAdapter
import com.htetarkarlinn.itvisionhub.databinding.ActivityAddCampBinding
import com.htetarkarlinn.itvisionhub.databinding.StudentInfoBinding
import java.util.*
import kotlin.collections.ArrayList

class AddCampActivity : AppCompatActivity() {
    private var images: ArrayList<Uri?>? = null
    private var position = 0
    private lateinit var b : StudentInfoBinding
    private lateinit var binding: ActivityAddCampBinding
    private var start =true
    private var students : MutableList<User> = ArrayList()
    private var end=true
    private var idList : MutableList<String> = arrayListOf()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAddCampBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        images = ArrayList()

        collectStudentInfo()

        //setup image switcher
        binding.imageSwitch.setFactory { ImageView(applicationContext) }

        binding.sPick.setOnClickListener {
            if (start){
            binding.startDatePicker.visibility= View.VISIBLE
            binding.endPick.visibility=View.INVISIBLE
            binding.sPick.setImageDrawable(getDrawable(R.drawable.drop_up))
                binding.ePick.setImageDrawable(getDrawable(R.drawable.drop_down))
                start=false
                end=true
            }else{
                binding.startDatePicker.visibility=View.INVISIBLE
                binding.sPick.setImageDrawable(getDrawable(R.drawable.drop_down))
                /*binding.sdatePick.setOnClickListener {

                }*/
                binding.sDateTxt.text =binding.sdatePick.dayOfMonth.toString() + "/" + "${binding.sdatePick.month+1}" + "/" + binding.sdatePick.year
                start=true
            }
        }

        binding.moveRight.setOnClickListener {
            if (position<images!!.size-1){
                position++
                binding.imageSwitch.setImageURI(images!![position])
            }else{
                Toast.makeText(this, "No More Image", Toast.LENGTH_SHORT).show()
            }
        }
        binding.moveLeft.setOnClickListener {
            if (position>0){
                position--
                binding.imageSwitch.setImageURI(images!![position])
            }else{
                Toast.makeText(this, "No More image", Toast.LENGTH_SHORT).show()
            }
        }

        binding.ePick.setOnClickListener {
            if (end==true) {
                binding.startDatePicker.visibility = View.INVISIBLE
                binding.sPick.setImageDrawable(getDrawable(R.drawable.drop_down))
                binding.endPick.visibility = View.VISIBLE
                binding.ePick.setImageDrawable(getDrawable(R.drawable.drop_up))
                end=false
                start=true
            }else{
                binding.endPick.visibility=View.INVISIBLE
                binding.ePick.setImageDrawable(getDrawable(R.drawable.drop_down))
                binding.eDateTxt.text =binding.endDatePick.dayOfMonth.toString() + "/" + "${binding.endDatePick.month+1}" + "/" + binding.endDatePick.year

                end=true
            }
        }
        binding.addPhoto.setOnClickListener {
            pickImage()
        }

        binding.cancelCamp.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()


        }
        binding.addCamp.setOnClickListener {
            val campName = binding.campName.text.toString()
            val startDate=binding.sDateTxt.text.toString()
            val endDate=binding.eDateTxt.text.toString()
            val time=binding.campTime.text.toString()
            val description=binding.descriptionCamp.text.toString()
            if (startDate == "Select here" && endDate == "Select here"){
                Toast.makeText(this, "Please enter both of start and end date", Toast.LENGTH_SHORT).show()
            }else{
                if (campName == "" && time == ""){
                    Toast.makeText(this, "Please enter full information", Toast.LENGTH_SHORT).show()
                }else{
                    binding.addCamp.isEnabled=false
                    binding.addCamp.text="Adding"
                    binding.cancelCamp.isEnabled=false
                    val camp=CampModel(startDate,endDate,campName,time,description,images)
                    uploadCampToDatabase(camp)
                }
            }

        }

    }

    private fun uploadCampToDatabase(camp: CampModel) {
        val img : ArrayList<Uri?>? = camp.images
        val imgLink : ArrayList<Uri?> = ArrayList()
        if (img?.size==0){
            uploadData(camp)
        }else {
            for (c in img!!) {
                val filename = UUID.randomUUID().toString()
                val ref = FirebaseStorage.getInstance().getReference(("/camp_images/$filename"))
                ref.putFile(c!!)
                    .addOnSuccessListener {
                        ref.downloadUrl.addOnSuccessListener {
                            imgLink.add(it)
                            if (img.size == imgLink.size) {
                                // Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
                                camp.images = imgLink
                                uploadData(camp)
                            }
                        }
                    }
                    .addOnFailureListener {

                    }
            }
        }
    }

    private fun uploadData(camp: CampModel) {
        val db = Firebase.firestore

        db.collection("camp")
            .add(camp)
            .addOnSuccessListener { _ ->

                Log.d(
                    ContentValues.TAG,
                    "DocumentSnapshot added with ID: ${camp.camp_name}"
                )
                onBackPressedDispatcher.onBackPressed()

            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
            }
    }

    private fun collectStudentInfo() {
        val db= Firebase.firestore
        db.collection("users")
            .get().addOnSuccessListener {
                for (doc in it){
                    if (doc["role"].toString() != "Admin"){
                        idList.add(doc.id)
                        val user = User(doc["name"].toString(),doc["phone"].toString(),doc["email"].toString(),doc["password"].toString()
                            ,doc["img"].toString(),doc["role"].toString(),doc["degree"].toString(),doc["camp"].toString(),doc["request"].toString(),doc["noti"].toString())
                        // val user : User =doc.toObject(User::class.java)
                        students.add(user)
                    }
                }

            }
            .addOnFailureListener{

            }
    }


    private fun pickImage() {
        val intent= Intent(Intent.EXTRA_ALLOW_MULTIPLE)
        intent.type="image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
        intent.action= Intent.ACTION_GET_CONTENT
        activityResult.launch(Intent.createChooser(intent,"Choose Image"))
    }
    private val activityResult=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->

        if (result.resultCode== RESULT_OK && result.data!= null){
            images!!.clear()
            removeMove()
            binding.previewImg.visibility=View.INVISIBLE
            if (result.data!!.clipData!=null) {
                showMove()
                val count = result.data!!.clipData!!.itemCount
                for (i in 0 until count) {
                    val imageUrl = result.data!!.clipData!!.getItemAt(i)
                    images!!.add(imageUrl.uri)
                }
                if (images!!.size!=0)
                {
                    showMove()

                }
                binding.imageSwitch.setImageURI(images!![0])
                position=0
            }else{
                removeMove()
                val imageUrl =result.data!!.data
                images!!.add(imageUrl)
                binding.imageSwitch.setImageURI(imageUrl)
                position=0
            }
        }
    }

    private fun removeMove() {

        binding.moveLeft.visibility=View.INVISIBLE
        binding.moveRight.visibility=View.INVISIBLE
    }

    private fun showMove() {
        binding.moveLeft.visibility=View.VISIBLE
        binding.moveRight.visibility=View.VISIBLE
    }

}