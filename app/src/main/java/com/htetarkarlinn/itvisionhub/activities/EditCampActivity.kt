package com.htetarkarlinn.itvisionhub.activities

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.htetarkarlinn.itvisionhub.models.CampModel
import com.htetarkarlinn.itvisionhub.R
import com.htetarkarlinn.itvisionhub.adapter.CampAdapter
import com.htetarkarlinn.itvisionhub.databinding.ActivityEditCampBinding
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList

class EditCampActivity : AppCompatActivity(){
    var id=""
    var name=""
    private var startD=""
    private var endD=""
    var time=""
    private var des=""
    private var defaultImg=true
    private var position = 0
    private var pickPosition= 0
    private var imageList : MutableList<String> = arrayListOf()
    private var start =true
    private var end=true
    lateinit var camp: CampModel
    private var images: ArrayList<Uri?>? = arrayListOf()
    private lateinit var binding : ActivityEditCampBinding
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityEditCampBinding.inflate(layoutInflater)
        setContentView(binding.root)
        id=intent.getStringExtra("id").toString()
        /*val bundle= intent.extras
        camp= bundle!!.getSerializable("camp",intent. )!!
        Toast.makeText(this, camp.camp_name, Toast.LENGTH_SHORT).show()*/
        camp= intent.getSerializableExtra("camp") as CampModel
        //Toast.makeText(this, camp.camp_name, Toast.LENGTH_SHORT).show()
        // binding.imageSwitch.setFactory { ImageView(applicationContext) }
        imageList=camp.images!! as MutableList<String>
        name=camp.camp_name
        startD=camp.start_date
        endD=camp.end_date
        time=camp.time
        des=camp.description
        binding.campName.append(name)
        binding.campTime.append(time)
        binding.descriptionCamp.append(des)
        binding.sDateTxt.text=startD
        binding.eDateTxt.text=endD

        //Toast.makeText(this, id, Toast.LENGTH_SHORT).show()
        if (imageList.size==1){
            binding.previewImg.visibility=View.INVISIBLE
            removeMove()
            Glide.with(this).load(imageList[0]).into(binding.imageSwitch)
        }else if (imageList.size> 0){
            showMove()
            binding.previewImg.visibility=View.INVISIBLE
            Glide.with(this).load(imageList[0]).into(binding.imageSwitch)
        }

        binding.moveRight.setOnClickListener {
            if (defaultImg){
                if (position<imageList.size-1){
                    position++
                    Glide.with(this).load(imageList[position]).into(binding.imageSwitch)
                }else{
                    Toast.makeText(this, "No More Image", Toast.LENGTH_SHORT).show()
                }
            }else{
                if (pickPosition<images!!.size-1){
                    pickPosition++
                    val inputStr : InputStream = contentResolver.openInputStream(images!![pickPosition].toString().toUri())!!
                    val bitmap = BitmapFactory.decodeStream(inputStr)
                    binding.imageSwitch.setImageBitmap(bitmap)
                }else{
                    Toast.makeText(this, "No More Image", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.moveLeft.setOnClickListener {
            if (defaultImg){
                if (position>0){
                    position--
                    Glide.with(this).load(imageList[position]).into(binding.imageSwitch)
                }else{
                    Toast.makeText(this, "No More image", Toast.LENGTH_SHORT).show()
                }
            }else{
                if (pickPosition>0) {
                    pickPosition--
                    val inputStr: InputStream =
                        contentResolver.openInputStream(images!![pickPosition].toString().toUri())!!
                    val bitmap = BitmapFactory.decodeStream(inputStr)
                    binding.imageSwitch.setImageBitmap(bitmap)
                }else{
                    Toast.makeText(this, "No More image", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.sPick.setOnClickListener {
            if (start){
                binding.startDatePicker.visibility= View.VISIBLE
                binding.endPick.visibility= View.INVISIBLE
                binding.sPick.setImageDrawable(getDrawable(R.drawable.drop_up))
                binding.ePick.setImageDrawable(getDrawable(R.drawable.drop_down))
                start=false
                end=true
            }else{
                binding.startDatePicker.visibility= View.INVISIBLE
                binding.sPick.setImageDrawable(getDrawable(R.drawable.drop_down))
                /*binding.sdatePick.setOnClickListener {

                }*/
                binding.sDateTxt.text =binding.sdatePick.dayOfMonth.toString() + "/" + "${binding.sdatePick.month+1}" + "/" + binding.sdatePick.year
                start=true
            }
        }

        binding.ePick.setOnClickListener {
            if (end) {
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
        binding.cancelCamp.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        binding.addPhoto.setOnClickListener {
            pickImage()
        }
        binding.editCamp.setOnClickListener {
            binding.editCamp.isEnabled=false
            binding.editCamp.text="Editing"
            binding.cancelCamp.isEnabled=false
            name=binding.campName.text.toString()
            startD=binding.sDateTxt.text.toString()
            endD=binding.eDateTxt.text.toString()
            time=binding.campTime.text.toString()
            des=binding.descriptionCamp.text.toString()
            if (defaultImg){
                val campModel=CampModel(startD,endD,name,time,des,imageList as ArrayList<Uri?>)
                updateNoImage(campModel)
            }else{
                val campModel=CampModel(startD,endD,name,time,des,images)
                uploadCampToDatabase(campModel)
            }

        }
    }

    private fun uploadCampToDatabase(camp: CampModel) {
        val img: ArrayList<Uri?>? = camp.images
        val imgLink: ArrayList<Uri?> = ArrayList()
        if (img?.size == 0) {
            uploadData(camp)
        } else {
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
            .document(id)
            .set(camp)
            .addOnSuccessListener { documentReference ->

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

    private fun updateNoImage(campModel: CampModel) {
        val db=Firebase.firestore
        db.collection("camp")
            .document(id)
            .set(campModel)
            .addOnSuccessListener {
                onBackPressedDispatcher.onBackPressed()
            }
            .addOnFailureListener {

            }
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
            defaultImg=false
            if (images!!.size!=0){
                images!!.clear()
            }

            removeMove()
            binding.previewImg.visibility=View.INVISIBLE
            if (result.data!!.clipData!=null) {
                showMove()
                val count = result.data!!.clipData!!.itemCount
                for (i in 0 until count) {
                    val imgUrl = result.data!!.clipData!!.getItemAt(i)
                    images!!.add(imgUrl.uri)
                }
                if (images!!.size!=0)
                {
                    showMove()

                }
                val inputStr : InputStream = contentResolver.openInputStream(images!![0].toString().toUri())!!
                val bitmap = BitmapFactory.decodeStream(inputStr)
                binding.imageSwitch.setImageBitmap(bitmap)
                pickPosition=0
                // position=0
            }else{
                removeMove()
                val imageUrl =result.data!!.data
                images!!.add(imageUrl)
                val inputStr : InputStream? =contentResolver.openInputStream(imageUrl!!)
                val bitmap=BitmapFactory.decodeStream(inputStr)
                binding.imageSwitch.setImageBitmap(bitmap)
                pickPosition=0
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