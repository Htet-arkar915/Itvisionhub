package com.htetarkarlinn.itvisionhub.adapter

import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil.inflate
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.htetarkarlinn.itvisionhub.Models.AddClass
import com.htetarkarlinn.itvisionhub.R
import com.htetarkarlinn.itvisionhub.databinding.ActivityLoginBinding.inflate
import com.htetarkarlinn.itvisionhub.databinding.AddPostBinding
import com.htetarkarlinn.itvisionhub.databinding.ClassCustomBinding
import com.squareup.picasso.Picasso
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*

class ClassAdapter(val classList: MutableList<AddClass>, val activity: FragmentActivity?,val role : String):
    RecyclerView.Adapter<ClassAdapter.MyViewHolder>() {

    var r=role
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ClassCustomBinding.bind(view)

        fun bind(binding: ClassCustomBinding, it: AddClass?, r: Any) {

            if (r.equals("Student")){
                binding.editClass.visibility=View.INVISIBLE
            }
            var id= it?.id.toString()
            binding.className.text=it?.class_name
            binding.teacherN.text="Teacher -"+it?.teacher_name
            binding.classPh.text="Phone -"+it?.phone
            binding.classDescrip.text=it?.description
            Picasso.get().load(it?.image_url.toString()).placeholder(R.drawable.add_class).into(binding.classImage)
            binding.classImage.setOnClickListener {
                val param= LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                binding.classImage.layoutParams=param
            }
            binding.classImage.adjustViewBounds
            val current_date_And_time= getCurrentDate()
            val c_date=current_date_And_time.substringBefore("T")
            val c_time=current_date_And_time.substringAfter("T")
            val c_hr=c_time.substringBefore(":")
            val c_min_sec=c_time.substringAfter(":")
            val c_min=c_min_sec.substringBefore(":")

            val date= it?.date_time?.substringBefore("T")
            val time=it?.date_time?.substringAfter("T")
            val hr=time?.substringBefore(":")
            val min_sec=time?.substringAfter(":")
            //  Toast.makeText(itemView.context, min_sec, Toast.LENGTH_SHORT).show()
            val min=min_sec?.substringBefore(":")

            /* val t=Time.valueOf(time)
             val c_t=Time.valueOf(c_time)
             val dd : Long=c_t.time-t.time
             Toast.makeText(itemView.context, dd.toString(), Toast.LENGTH_SHORT).show()*/

            if (c_date==date){
                if (c_hr==hr){
                    if (c_min==min){
                        binding.dateTime.text="Just now"
                    }else{
                        val set_time=c_min.toString().toInt() - min.toString().toInt()
                        binding.dateTime.text="${set_time} min"
                    }

                } else if (c_hr.toString().toInt()==hr.toString().toInt()+1){
                    val set_min= c_min.toString().toInt()+(60-min.toString().toInt())
                    if (set_min>60){
                        binding.dateTime.text="1 hr ${set_min-60} min"
                    }
                    binding.dateTime.text=set_min.toString()+" min"
                }
                else{
                    val set_hr = c_hr.toString().toInt() - hr.toString().toInt()
                    binding.dateTime.text = "${set_hr} hr"

                }

            }else{
                binding.dateTime.text=date
            }
            // Toast.makeText(itemView.context, time, Toast.LENGTH_SHORT).show()

            //Glide.with(itemView.context).load()

            var addClass= AddClass(it?.id.toString(),it?.class_name.toString(),it?.teacher_name.toString(),it?.phone.toString(),it?.description.toString(),it?.image_url.toString(),it?.date_time.toString())
            binding.editClass.setOnClickListener {
                // Toast.makeText(itemView.context, addClass.class_name, Toast.LENGTH_SHORT).show()
                var edt_imageUrl=addClass.image_url
                val builder = AlertDialog.Builder(itemView.context,R.style.CustomAlertDialog)
                    .create()
                val view = LayoutInflater.from(itemView.context).inflate(R.layout.add_post,null)
                // val  button = view.findViewById<Button>(R.id.dialogDismiss_button)
                var b :AddPostBinding = AddPostBinding.bind(view)
                builder.setView(view)
                b.addClass.text="Update"
                b.edtName.hint=addClass.class_name
                val name : Editable =addClass.class_name as Editable

                b.edtName.text=name
                b.teacherName.hint=addClass.teacher_name
                b.classPhone.hint=addClass.phone
                b.description.hint=addClass.description
                Picasso.get().load(addClass.image_url).into(b.classImg)

                b.classImg.setOnClickListener {
                    // requestPermit()
                    //startImagePick()
                }
                b.cancelPost.setOnClickListener {
                    builder.dismiss()
                }
                b.addClass.setOnClickListener {
                    var class_name=b.edtName.text.toString()
                    var teacher_name=b.teacherName.text.toString()
                    var phone_no=b.classPhone.text.toString()
                    var descrip=b.description.text.toString()

                    if(class_name==""){
                        class_name=b.edtName.hint.toString()
                    }
                    if (teacher_name==""){
                        teacher_name=b.teacherName.hint.toString()
                    }
                    if (phone_no==""){
                        phone_no=b.classPhone.hint.toString()
                    }
                    if (descrip==""){
                        descrip=b.description.hint.toString()
                    }

                    var addclass =AddClass(id,class_name,teacher_name,phone_no,descrip,edt_imageUrl,addClass.date_time)

                    updateClass(addclass)
                    /* if (class_name=="" && teacher_name=="" && phone_no=="" && descrip==""){
                         Toast.makeText(itemView.context, "Please Enter Full Information", Toast.LENGTH_SHORT).show()
                     }else{*/
                    /*  if (uri!=null) {
                          ImageUploadAndDataSave(addclass)
                          uri= null
                      }else{
                          DataSaveNoImage(addclass)
                      }*/
                    builder.dismiss()


                }
                builder.setCanceledOnTouchOutside(false)
                builder.show()
            }


        }

        private fun updateClass(addclass: AddClass) {

            val fs=Firebase.firestore
            fs.collection("class")
                .document(addclass.id)
                .set(addclass)
                .addOnSuccessListener {
                    Toast.makeText(itemView.context, "Updated", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(itemView.context, "Error", Toast.LENGTH_SHORT).show()
                }

        }

        private fun startImagePick() {
            val intent= Intent(Intent.EXTRA_ALLOW_MULTIPLE)
            intent.type="image/*"
            /*intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.setAction(Intent.ACTION_GET_CONTENT)*/
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
            intent.action= Intent.ACTION_GET_CONTENT
            // activityResult.launch(Intent.createChooser(intent,"Choose Image"))
        }

        /*private val activityResult=(ActivityResultContracts.StartActivityForResult()){ result ->

            if (result.resultCode== Activity.RESULT_OK && result.data!= null){
                updateProfileImage(result.data!!)
            }
        }*/

        private fun getCurrentDate(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            return sdf.format(Date())
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.class_custom, parent, false)

        return ClassAdapter.MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        classList?.get(position).let {
            holder.bind(holder.binding, it,r)
        }
    }
    override fun getItemCount(): Int {
        return classList.size
    }


}

