package com.htetarkarlinn.itvisionhub.Activities

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.htetarkarlinn.itvisionhub.Models.CampModel
import com.htetarkarlinn.itvisionhub.Models.User
import com.htetarkarlinn.itvisionhub.R
import com.htetarkarlinn.itvisionhub.adapter.StudentInfoAdapter
import com.htetarkarlinn.itvisionhub.databinding.ActivityAddCampBinding
import com.htetarkarlinn.itvisionhub.databinding.StudentInfoBinding
import java.util.*
import kotlin.collections.ArrayList

class AddCampActivity : AppCompatActivity() {
    private var images: ArrayList<Uri?>? = null
    private var position = 0
    private val PICK_IMAGES_CODE = 0
    private lateinit var b : StudentInfoBinding
    private lateinit var binding: ActivityAddCampBinding
    private var start =true
    private var students : MutableList<User> = ArrayList()
    private var end=true
    private var id_list : MutableList<String> = arrayListOf()
    private var StudentInfoAdapter: StudentInfoAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAddCampBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        images = ArrayList()

        CollectStudentInfo()

        //setup image switcher
        binding.imageSwitch.setFactory { ImageView(applicationContext) }

        binding.sPick.setOnClickListener {
            if (start==true){
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
        /*binding.stuInfo.setOnClickListener {
            val name= binding.campName.text.toString()
                
                if (students.size == 0) {
                    Toast.makeText(this, "wait", Toast.LENGTH_SHORT).show()
                } else {
                    if (name.equals("")){
                        Toast.makeText(this, "Enter camp name", Toast.LENGTH_SHORT).show()
                    }else {
                        ShowDialog(name)
                    }
                }

        }*/
        binding.cancelCamp.setOnClickListener {
            onBackPressed()
        }
        binding.addCamp.setOnClickListener {
            val camp_name = binding.campName.text.toString()
            val start_date=binding.sDateTxt.text.toString()
            val end_date=binding.eDateTxt.text.toString()
            val time=binding.campTime.text.toString()
            val description=binding.descriptionCamp.text.toString()
            if (start_date.equals("Select here") && end_date.equals("Select here")){
                Toast.makeText(this, "Please enter both of start and end date", Toast.LENGTH_SHORT).show()
            }else{
                if (camp_name.equals("")&& time.equals("")){
                    Toast.makeText(this, "Please enter full information", Toast.LENGTH_SHORT).show()
                }else{
                    binding.addCamp.isEnabled=false
                    binding.addCamp.text="Adding"
                    binding.cancelCamp.isEnabled=false
                    val camp=CampModel(start_date,end_date,camp_name,time,description,images)
                    UploadCamptoDatebase(camp)
                }
            }

        }

    }

    private fun UploadCamptoDatebase(camp: CampModel) {
        val img : ArrayList<Uri?>? = camp.images
        val img_link : ArrayList<Uri?> = ArrayList()
        if (img?.size==0){
            UploadData(camp)
        }else {
            for (c in img!!) {
                val filename = UUID.randomUUID().toString()
                val ref = FirebaseStorage.getInstance().getReference(("/camp_images/$filename"))
                ref.putFile(c!!)
                    .addOnSuccessListener {
                        ref.downloadUrl.addOnSuccessListener {
                            img_link.add(it)
                            if (img.size == img_link.size) {
                                // Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
                                camp.images = img_link
                                UploadData(camp)
                            }
                        }
                    }
                    .addOnFailureListener {

                    }
            }
        }
       /* camp.images=img_link
        if (img.size!=img_link.size){
            Toast.makeText(this, "wait", Toast.LENGTH_SHORT).show()
        }else {
            Toast.makeText(this, img_link.size.toString(), Toast.LENGTH_SHORT).show()
            UploadData(camp)
        }*/
    }

    private fun UploadData(camp: CampModel) {
        val db = Firebase.firestore

        db.collection("camp")
            .add(camp)
            .addOnSuccessListener { documentReference ->

                Log.d(
                    ContentValues.TAG,
                    "DocumentSnapshot added with ID: ${camp.camp_name}"
                )
                onBackPressed()

            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
            }
    }

    private fun CollectStudentInfo() {
        val db= Firebase.firestore
        db.collection("users")
            .get().addOnSuccessListener {
                for (doc in it){
                    if (!doc["role"].toString().equals("Admin")){
                        id_list.add(doc.id)
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

    private fun ShowDialog(name : String) {
        val builder =AlertDialog.Builder(this,R.style.CustomAlertDialogTran).create()
        val view=layoutInflater.inflate(R.layout.student_info,null)
        b= StudentInfoBinding.bind(view)
        view.background=getDrawable(R.drawable.transparent_bg)
        builder.setView(view)
        builder.setCancelable(false)
        b.closeDialog.setOnClickListener {
            students.clear()
            CollectStudentInfo()
            builder.dismiss()
        }
        ShowRecycler(name)
        builder.show()
    }

    private fun ShowRecycler(name : String) {
        //Toast.makeText(this, "Total Users is ${students.size}", Toast.LENGTH_SHORT).show()
        val stud : ArrayList<User> = arrayListOf()
        val id_new_list : MutableList<String> = arrayListOf()
        for (i in students){
            if (i.camp.toLowerCase().replace(" ","").equals(name.toLowerCase().replace(" ","")) || i.camp.equals("")){
               stud.add(i)
                id_new_list.add(id_list[students.indexOf(i)])

            }
        }
        b.recycleStudent.layoutManager=LinearLayoutManager(this)
        StudentInfoAdapter= StudentInfoAdapter(stud,id_new_list, name,this)
        b.recycleStudent.adapter=StudentInfoAdapter
    }

    private fun pickImage() {
        val intent= Intent(Intent.EXTRA_ALLOW_MULTIPLE)
        intent.type="image/*"
        /*intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.setAction(Intent.ACTION_GET_CONTENT)*/
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
        intent.action= Intent.ACTION_GET_CONTENT
        activityResult.launch(Intent.createChooser(intent,"Choose Image"))
    }
    private val activityResult=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->

        if (result.resultCode== RESULT_OK && result.data!= null){
            images!!.clear()
            RemoveMove()
            binding.previewImg.visibility=View.INVISIBLE
            if (result.data!!.clipData!=null) {
                ShowMove()
                val count = result.data!!.clipData!!.itemCount
                for (i in 0 until count) {
                    val image_url = result.data!!.clipData!!.getItemAt(i)
                    images!!.add(image_url.uri)
                }
                if (images!!.size!=0)
                {
                    ShowMove()

                }
                binding.imageSwitch.setImageURI(images!![0])
                position=0
            }else{
                RemoveMove()
                val imageurl =result.data!!.data
                images!!.add(imageurl)
                binding.imageSwitch.setImageURI(imageurl)
                position=0
            }
        }
    }

    private fun RemoveMove() {

        binding.moveLeft.visibility=View.INVISIBLE
        binding.moveRight.visibility=View.INVISIBLE
    }

    private fun ShowMove() {
        binding.moveLeft.visibility=View.VISIBLE
        binding.moveRight.visibility=View.VISIBLE
    }

}