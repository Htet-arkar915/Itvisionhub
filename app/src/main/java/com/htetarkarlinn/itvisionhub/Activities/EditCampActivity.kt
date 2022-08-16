package com.htetarkarlinn.itvisionhub.Activities

import android.content.ContentValues
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.htetarkarlinn.itvisionhub.Models.CampModel
import com.htetarkarlinn.itvisionhub.R
import com.htetarkarlinn.itvisionhub.databinding.ActivityEditCampBinding
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList

class EditCampActivity : AppCompatActivity(){
    var id=""
    var name=""
    var strt_d=""
    var end_d=""
    var time=""
    var des=""
    var default_img=true
    private var position = 0
    private var pick_position= 0
    var image_list : MutableList<String> = arrayListOf()
    private var start =true
    private var end=true
    lateinit var camp: CampModel
    private var images: ArrayList<Uri?>? = arrayListOf()
    private lateinit var binding : ActivityEditCampBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityEditCampBinding.inflate(layoutInflater)
        setContentView(binding.root)
        id=intent.getStringExtra("id").toString()
        camp=intent.getSerializableExtra("camp") as CampModel
        //Toast.makeText(this, camp.camp_name, Toast.LENGTH_SHORT).show()
        // binding.imageSwitch.setFactory { ImageView(applicationContext) }
        image_list=camp.images!! as MutableList<String>
        name=camp.camp_name
        strt_d=camp.start_date
        end_d=camp.end_date
        time=camp.time
        des=camp.description
        binding.campName.append(name)
        binding.campTime.append(time)
        binding.descriptionCamp.append(des)
        binding.sDateTxt.text=strt_d
        binding.eDateTxt.text=end_d

        Toast.makeText(this, id, Toast.LENGTH_SHORT).show()
        if (image_list.size==1){
            binding.previewImg.visibility=View.INVISIBLE
            RemoveMove()
            Glide.with(this).load(image_list[0]).into(binding.imageSwitch)
        }else if (image_list.size> 0){
            ShowMove()
            binding.previewImg.visibility=View.INVISIBLE
            Glide.with(this).load(image_list[0]).into(binding.imageSwitch)
        }

        binding.moveRight.setOnClickListener {
            if (default_img){
                if (position<image_list.size-1){
                    position++
                    Glide.with(this).load(image_list[position]).into(binding.imageSwitch)
                }else{
                    Toast.makeText(this, "No More Image", Toast.LENGTH_SHORT).show()
                }
            }else{
                if (pick_position<images!!.size-1){
                    pick_position++
                    val inputStr : InputStream = contentResolver.openInputStream(images!![pick_position].toString().toUri())!!
                    val bitmap = BitmapFactory.decodeStream(inputStr)
                    binding.imageSwitch.setImageBitmap(bitmap)
                }else{
                    Toast.makeText(this, "No More Image", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.moveLeft.setOnClickListener {
            if (default_img){
                if (position>0){
                    position--
                    Glide.with(this).load(image_list[position]).into(binding.imageSwitch)
                }else{
                    Toast.makeText(this, "No More image", Toast.LENGTH_SHORT).show()
                }
            }else{
                if (pick_position>0) {
                    pick_position--
                    val inputStr: InputStream =
                        contentResolver.openInputStream(images!![pick_position].toString().toUri())!!
                    val bitmap = BitmapFactory.decodeStream(inputStr)
                    binding.imageSwitch.setImageBitmap(bitmap)
                }else{
                    Toast.makeText(this, "No More image", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.sPick.setOnClickListener {
            if (start==true){
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
        binding.cancelCamp.setOnClickListener {
            onBackPressed()
        }


        binding.addPhoto.setOnClickListener {
            pickImage()
        }
        binding.editCamp.setOnClickListener {
            binding.editCamp.isEnabled=false
            binding.editCamp.text="Editing"
            binding.cancelCamp.isEnabled=false
            name=binding.campName.text.toString()
            strt_d=binding.sDateTxt.text.toString()
            end_d=binding.eDateTxt.text.toString()
            time=binding.campTime.text.toString()
            des=binding.descriptionCamp.text.toString()
            if (default_img){
                var campModel=CampModel(strt_d,end_d,name,time,des,image_list as ArrayList<Uri?>)
                UpdateNoImage(campModel)
            }else{
                var campModel=CampModel(strt_d,end_d,name,time,des,images)
                UploadCamptoDatebase(campModel)
            }

        }
    }

    private fun UploadCamptoDatebase(camp: CampModel) {
        val img: ArrayList<Uri?>? = camp.images
        val img_link: ArrayList<Uri?> = ArrayList()
        if (img?.size == 0) {
            UploadData(camp)
        } else {
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
    }
    private fun UploadData(camp: CampModel) {
        val db = Firebase.firestore

        db.collection("camp")
            .document(id)
            .set(camp)
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

    private fun UpdateNoImage(campModel: CampModel) {
        val db=Firebase.firestore
        db.collection("camp")
            .document(id)
            .set(campModel)
            .addOnSuccessListener {
                onBackPressed()
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
            default_img=false
            if (images!!.size!=0){
                images!!.clear()
            }

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
                val inputStr : InputStream = contentResolver.openInputStream(images!![0].toString().toUri())!!
                val bitmap = BitmapFactory.decodeStream(inputStr)
                binding.imageSwitch.setImageBitmap(bitmap)
                pick_position=0
                // position=0
            }else{
                RemoveMove()
                val imageurl =result.data!!.data
                images!!.add(imageurl)
                val inputstr : InputStream? =contentResolver.openInputStream(imageurl!!)
                val bitmap=BitmapFactory.decodeStream(inputstr)
                binding.imageSwitch.setImageBitmap(bitmap)
                pick_position=0
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