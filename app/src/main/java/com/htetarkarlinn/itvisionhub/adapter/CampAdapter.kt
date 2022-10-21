package com.htetarkarlinn.itvisionhub.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.htetarkarlinn.itvisionhub.activities.EditCampActivity
import com.htetarkarlinn.itvisionhub.models.CampModel
import com.htetarkarlinn.itvisionhub.models.User
import com.htetarkarlinn.itvisionhub.R
import com.htetarkarlinn.itvisionhub.databinding.ShowCampBinding
import com.htetarkarlinn.itvisionhub.databinding.StudentInfoBinding
import com.squareup.picasso.Picasso
import java.io.Serializable

class CampAdapter(
    private val campList: MutableList<CampModel>,
    private val camps_id: MutableList<String>,
    val student: MutableList<User>,
    private val id_list: MutableList<String>,
    private val con: Context?,
    val role: String
) : RecyclerView.Adapter<CampAdapter.MyViewHolder>() {
     private lateinit var b : StudentInfoBinding
    private var studentInfoAdapter: StudentInfoAdapter? = null
    private val defImg="https://firebasestorage.googleapis.com/v0/b/itvisionhub-6346c.appspot.com/o/camp_images%2F74119bf9-0b37-4f1d-89b1-53aeb2769f30?alt=media&token=25e037cc-539e-4ea5-98de-3323bee31b4d"
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.show_camp,parent,false)
        return CampAdapter.MyViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var request =true
        var des : String=""
        var requestBtn=""
        var yourCamp=""
        val imageCheck : ArrayList<Boolean> = arrayListOf()
        var n : String = ""
        val imageList : MutableList<Uri> = arrayListOf()
        val binding : ShowCampBinding = ShowCampBinding.bind(holder.itemView)
        for (c in campList){
            imageCheck.add(true)
        }
        n=campList[position].camp_name.toString()
        holder.setIsRecyclable(false)
        val sharedPreferences :SharedPreferences =con!!.getSharedPreferences("User",Context.MODE_PRIVATE)!!
        val email=sharedPreferences.getString("email","name").toString()
        val pass=sharedPreferences.getString("password","password").toString()
        for (s in student){
            if (email == s.email && pass == s.password) {
                yourCamp=s.camp
                if (s.camp.lowercase().replace(" ", "") == n.lowercase().replace(" ", "") && s.request.lowercase()
                        .replace(" ", "") == n.lowercase().replace(" ", "")
                ) {
                    requestBtn = "Accepted"
                    binding.stuRequest.setTextColor(Color.GREEN)
                } else if ((s.camp.lowercase().replace(" ", "") != n.lowercase().replace(" ", "")) && (s.request.lowercase()
                        .replace(" ", "") == n.lowercase().replace(" ", ""))
                ) {
                    requestBtn = "Requested"
                    binding.stuRequest.setTextColor(Color.RED)
                    request = false
                } else {
                    request=true
                    binding.stuRequest.setTextColor(Color.BLACK)
                    requestBtn = "Request"
                }
                binding.stuRequest.text = requestBtn
            }
        }


        if (role == "Student") {
            updateView(binding)
        }
        binding.name.text=campList[position].camp_name.capitalize().replace(" ","")
        binding.campPeroid.text="Peroid - ${campList[position].start_date} to ${campList[position].end_date}"
        binding.dailyTime.text="Time - ${campList[position].time}"

        for(img in campList[position].images!!.toArray()){
            imageList.add(img!!.toString().toUri())
        }
        checkImage(binding,imageList)

        des=campList[position].description.toString()
        if (des.length >100) {
            binding.campDescription.text =
                des.toString().substring(0, 100) + "...see more"
        }else{
            binding.campDescription.text=des.toString()
        }
        var check =true
        binding.campDescription.setOnClickListener {
            if (check) {
                binding.campDescription.text = des
                check = false
            }else{
                if (des.length>100){
                    binding.campDescription.text=des.toString().substring(0,100)+"...see more"
                    check= true
                }
            }
        }
        binding.imageCamp.setOnClickListener {
           // Toast.makeText(con, "${position}", Toast.LENGTH_SHORT).show()
            val bindImgList : ArrayList<Uri> = arrayListOf()
            for (img in imageList){
                bindImgList.add(img)
            }
            if (imageCheck[position]){
                if (imageList.size>0){
                    val recParam = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    binding.imageCampRecycle.layoutParams=recParam

                    binding.secondImgLayout.visibility=View.INVISIBLE
                    binding.firstImg.visibility=View.INVISIBLE
                    binding.imageCampRecycle.visibility=View.VISIBLE
                    showImageRecycle(binding,bindImgList)
                }else {
                    imageList.add(defImg.toUri())
                      noImage(binding,imageList)
                }
                imageCheck[position]=false
            }

        }
        binding.layoutMain.setOnClickListener {
            if (!imageCheck[position]){
                // Toast.makeText(itemView.context, "click", Toast.LENGTH_SHORT).show()
                binding.imageCampRecycle.layoutParams=LinearLayout.LayoutParams(0,0)
                binding.imageCampRecycle.visibility=View.INVISIBLE
                binding.secondImgLayout.visibility=View.VISIBLE
                binding.firstImg.visibility=View.VISIBLE
                checkImage(binding,imageList)
                imageCheck[position]=true
            }
        }

        binding.stuAddRemove.setOnClickListener {
            collectStuInfo(id_list,n)
        }

        binding.stuRequest.text=requestBtn
        binding.stuRequest.setOnClickListener {
        
            if (yourCamp != ""){
                Toast.makeText(con, "You already in ${yourCamp}", Toast.LENGTH_SHORT).show()
            } else if (request){
                binding.stuRequest.text="Requested"
                binding.stuRequest.setTextColor(Color.RED)
                addRequestToDatabase(campList[position].camp_name)
                request=false
            }else{
                binding.stuRequest.text="Request"
                binding.stuRequest.setTextColor(Color.BLACK)
                cancelRequest()
                request=true
            }

        }
        binding.delete.setOnClickListener {
            showDeleteDialog(camps_id[position],campList[position].camp_name.toString())
        }
        binding.edit.setOnClickListener {
            val intent=Intent(con,EditCampActivity::class.java)
           /* val bundle = Bundle()
            bundle.putSerializable("camp",campList[position] as Serializable)*/
            intent.putExtra("id",camps_id[position])
                .putExtra("camp",campList[position] as Serializable)
            //intent.putExtras(bundle)
            con.startActivity(intent)
        }

       // campList?.get(position).let { holder.bind(holder,it,student,id_list,role) }
    }

    private fun showDeleteDialog(s: String, name: String) {
        val dialog=AlertDialog.Builder(con!!)
        dialog.setIcon(R.drawable.delete)
        dialog.setTitle("Delete")
        dialog.setMessage("Are You Sure Want To Delete $name")
        dialog.setPositiveButton("Yes"){ _, _ ->
            val db =Firebase.firestore
            db.collection("camp")
                .document(s)
                .delete()
                .addOnSuccessListener {
                    db.collection("users")
                        .get()
                        .addOnSuccessListener {
                            for (u in it){
                                if (u["camp"]==(name) && u["request"]==name){
                                    db.collection("users")
                                        .document(u.id)
                                        .update("camp","","request","")
                                        .addOnSuccessListener {

                                        }
                                        .addOnFailureListener {  }
                                }
                            }
                        }
                }
                .addOnFailureListener {  }
        }
        dialog.setNegativeButton("No"){ d, _ ->
            d.dismiss()
            // Toast.makeText(this.activity, "LogOut", Toast.LENGTH_SHORT).show()
        }
        dialog.show()


    }

    private fun cancelRequest() {
        val pre: SharedPreferences =
            con!!.getSharedPreferences("Role", AppCompatActivity.MODE_PRIVATE)
        val id = pre.getString("id", "string").toString()
        val db =Firebase.firestore
        db.collection("users")
            .document(id)
            .update("request","")
            .addOnSuccessListener {
                Toast.makeText(con, "Canceled Request", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(con, "Request Fail", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addRequestToDatabase(c_name : String) {
        val pre: SharedPreferences =
            con!!.getSharedPreferences("Role", AppCompatActivity.MODE_PRIVATE)
        val id = pre.getString("id", "string").toString()
        val db =Firebase.firestore
        db.collection("users")
            .document(id)
            .update("request",c_name,"noti","yes")
            .addOnSuccessListener {
                Toast.makeText(con, "Requested", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(con, "Request Fail", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateView(binding: ShowCampBinding) {
        binding.stu.visibility =View.INVISIBLE
        binding.stu.layoutParams= RelativeLayout.LayoutParams(5, 10)
        binding.edit.visibility=View.INVISIBLE
        binding.delete.visibility=View.INVISIBLE
        binding.stuReq.visibility=View.VISIBLE
    }

    private fun collectStuInfo(idList: MutableList<String>, n: String) {
        val db= Firebase.firestore
        idList.clear()
        val stuList :MutableList<User> = arrayListOf()
        db.collection("users")
            .get().addOnSuccessListener {
                for (doc in it){
                    if (doc["role"].toString() != "Admin"){
                         idList.add(doc.id)
                        val user = User(doc["name"].toString(),doc["phone"].toString(),doc["email"].toString(),doc["password"].toString()
                            ,doc["img"].toString(),doc["role"].toString(),doc["degree"].toString(),doc["camp"].toString(),doc["request"].toString(),doc["noti"].toString())
                        // val user : User =doc.toObject(User::class.java)
                        stuList.add(user)
                    }
                }
                showDialog(stuList,idList,n)
            }
            .addOnFailureListener{

            }
    }

    private fun showDialog(stuList: MutableList<User>, idList: MutableList<String>, n: String) {

        val builder = AlertDialog.Builder(con!!,R.style.CustomAlertDialogTran).create()
        val view=LayoutInflater.from(con).inflate(R.layout.student_info,null)
        b= StudentInfoBinding.bind(view)
        view.background=getDrawable(con,R.drawable.transparent_bg)
        builder.setView(view)
        builder.setCancelable(false)
        b.closeDialog.setOnClickListener {
            stuList.clear()
            builder.dismiss()
        }
        showRecycler(stuList,idList,n)
        builder.show()
    }

    private fun showRecycler(stu: MutableList<User>, idList: MutableList<String>, n: String) {
        val stud : ArrayList<User> = arrayListOf()
        val idNewList : MutableList<String> = arrayListOf()
        for (i in stu){
            if (i.request.lowercase().replace(" ","") == n.lowercase().replace(" ","") && (i.camp == "") || i.camp.lowercase().replace(" ","") == n.lowercase().replace(" ","")){
                stud.add(i)
                idNewList.add(idList[stu.indexOf(i)])

            }
        }
        b.recycleStudent.layoutManager= LinearLayoutManager(con)
        studentInfoAdapter= StudentInfoAdapter(stud,idNewList, n,
            con
        )
        b.recycleStudent.adapter=studentInfoAdapter
    }

    private fun showImageRecycle(binding: ShowCampBinding, imageList: MutableList<Uri>) {
        var campImageAdapter : CampImageAdapter? =null
        binding.imageCampRecycle.visibility=View.VISIBLE
        binding.imageCampRecycle.layoutManager = LinearLayoutManager(con)
        campImageAdapter = CampImageAdapter(con!!,imageList)
        binding.imageCampRecycle.adapter=campImageAdapter
    }

    private fun checkImage(binding: ShowCampBinding, imageList: MutableList<Uri>) {
        when (imageList.size) {
            0 -> {
                imageList.add(defImg.toUri())
                noImage(binding,imageList)
            }
            1 -> {
                Picasso.get().load(imageList[0].toString()).placeholder(R.drawable.cover).into(binding.firstImg)
                binding.secondImgLayout.visibility=View.INVISIBLE
                val param = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                binding.firstImg.layoutParams=param
                binding.imageCamp.isEnabled=false
            }
            2 -> {
                Picasso.get().load(imageList[0].toString()).placeholder(R.drawable.cover).into(binding.firstImg)
                Picasso.get().load(imageList[1].toString()).placeholder(R.drawable.cover).into(binding.secondImg)
                binding.thirdImg.visibility=View.INVISIBLE
                val param=LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                binding.secondImg.layoutParams=param
            }
            3 -> {
                Picasso.get().load(imageList[0].toString()).placeholder(R.drawable.cover).into(binding.firstImg)
                Picasso.get().load(imageList[1].toString()).placeholder(R.drawable.cover).into(binding.secondImg)
                Picasso.get().load(imageList[2].toString()).placeholder(R.drawable.cover).into(binding.thirdImg)
                binding.plus.visibility=View.INVISIBLE
            }
            else -> {
                Picasso.get().load(imageList[0].toString()).placeholder(R.drawable.cover).into(binding.firstImg)
                Picasso.get().load(imageList[1].toString()).placeholder(R.drawable.cover).into(binding.secondImg)
                Picasso.get().load(imageList[2].toString()).placeholder(R.drawable.cover).into(binding.thirdImg)
                binding.plus.visibility=View.VISIBLE
            }
        }
    }
    private fun noImage(binding: ShowCampBinding,imageList: MutableList<Uri>) {
        binding.imageCamp.isEnabled=false
        binding.secondImgLayout.visibility=View.INVISIBLE
        Picasso.get().load(imageList[0].toString()).placeholder(R.drawable.cover).into(binding.firstImg)
        binding.secondImgLayout.layoutParams=LinearLayout.LayoutParams(1,1)
        binding.secondImg.layoutParams=LinearLayout.LayoutParams(1,1)
        binding.thirdImg.layoutParams=RelativeLayout.LayoutParams(1,1)
        val param = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        binding.firstImg.layoutParams=param
    }

    override fun getItemCount(): Int =campList.size

}

