package com.htetarkarlinn.itvisionhub.adapter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
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
import com.htetarkarlinn.itvisionhub.Activities.EditCampActivity
import com.htetarkarlinn.itvisionhub.Models.CampModel
import com.htetarkarlinn.itvisionhub.Models.User
import com.htetarkarlinn.itvisionhub.R
import com.htetarkarlinn.itvisionhub.databinding.ShowCampBinding
import com.htetarkarlinn.itvisionhub.databinding.StudentInfoBinding
import com.squareup.picasso.Picasso
import java.io.Serializable

class CampAdapter(
    val campList: MutableList<CampModel>,
    val camps_id: MutableList<String>,
    val student: MutableList<User>,
    val id_list: MutableList<String>,
    val con: Context?,
    val role: String
) : RecyclerView.Adapter<CampAdapter.MyViewHolder>() {
     private lateinit var b : StudentInfoBinding
    private var StudentInfoAdapter: StudentInfoAdapter? = null
    val def_img="https://firebasestorage.googleapis.com/v0/b/itvisionhub-6346c.appspot.com/o/camp_images%2F74119bf9-0b37-4f1d-89b1-53aeb2769f30?alt=media&token=25e037cc-539e-4ea5-98de-3323bee31b4d"
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.show_camp,parent,false)
        return CampAdapter.MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var request =true
        var des : String=""
        var request_btn=""
        var your_camp=""
        var image_check : ArrayList<Boolean>? = arrayListOf()
        var n : String = ""
        var image_list : MutableList<Uri> = arrayListOf()
        val binding : ShowCampBinding = ShowCampBinding.bind(holder.itemView)
        for (c in campList){
            image_check!!.add(true)
        }
        n=campList[position].camp_name.toString()
        holder.setIsRecyclable(false)
        val sharedPreferences :SharedPreferences =con!!.getSharedPreferences("User",Context.MODE_PRIVATE)!!
        var email=sharedPreferences.getString("email","name").toString()
        var pass=sharedPreferences.getString("password","password").toString()
        for (s in student){
            if (email.equals(s.email) && pass.equals(s.password)) {
                your_camp=s.camp
                if (s.camp.lowercase().replace(" ", "")
                        .equals(n.lowercase().replace(" ", "")) && s.request.lowercase()
                        .replace(" ", "").equals(n.lowercase().replace(" ", ""))
                ) {
                    request_btn = "Accepted"
                    binding.stuRequest.setTextColor(Color.GREEN)
                } else if ((!s.camp.lowercase().replace(" ", "")
                        .equals(n.lowercase().replace(" ", ""))) && (s.request.lowercase()
                        .replace(" ", "").equals(n.lowercase().replace(" ", "")))
                ) {
                    request_btn = "Requested"
                    binding.stuRequest.setTextColor(Color.RED)
                    request = false
                } else {
                    request=true
                    binding.stuRequest.setTextColor(Color.BLACK)
                    request_btn = "Request"
                }
                binding.stuRequest.text = request_btn
            }
        }


        if (role.equals("Student")) {
            UpdateView(binding)
        }
        binding.name.text=campList[position].camp_name.capitalize().replace(" ","")
        binding.campPeroid.text="Peroid - ${campList[position].start_date} to ${campList[position].end_date}"
        binding.dailyTime.text="Time - ${campList[position].time}"

        for(img in campList[position].images!!.toArray()){
            image_list.add(img!!.toString().toUri())
        }
        CheckImage(binding,image_list)

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
            val bind_img_list : ArrayList<Uri> = arrayListOf()
            for (img in image_list){
                bind_img_list.add(img)
            }
            if (image_check!![position]){
                if (image_list.size>0){
                    val rec_param = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    binding.imageCampRecycle.layoutParams=rec_param

                    binding.secondImgLayout.visibility=View.INVISIBLE
                    binding.firstImg.visibility=View.INVISIBLE
                    binding.imageCampRecycle.visibility=View.VISIBLE
                    ShowImageRecycle(binding,bind_img_list)
                }else if (image_list.size==0){
                    image_list.add(def_img.toUri())
                      NoImage(binding,image_list)
                }
                image_check[position]=false
            }

        }
        binding.layoutMain.setOnClickListener {
            if (!image_check!![position]){
                // Toast.makeText(itemView.context, "click", Toast.LENGTH_SHORT).show()
                binding.imageCampRecycle.layoutParams=LinearLayout.LayoutParams(0,0)
                binding.imageCampRecycle.visibility=View.INVISIBLE
                binding.secondImgLayout.visibility=View.VISIBLE
                binding.firstImg.visibility=View.VISIBLE
                CheckImage(binding,image_list)
                image_check[position]=true
            }
        }

        binding.stuAddRemove.setOnClickListener {
            CollectStuInfo(id_list,n)
        }

        binding.stuRequest.text=request_btn
        binding.stuRequest.setOnClickListener {
        
            if (!your_camp.equals("")){
                Toast.makeText(con, "You already in ${your_camp}", Toast.LENGTH_SHORT).show()
            } else if (request){
                binding.stuRequest.text="Requested"
                binding.stuRequest.setTextColor(Color.RED)
                AddRequestToDatabase(campList[position].camp_name)
                request=false
            }else{
                binding.stuRequest.text="Request"
                binding.stuRequest.setTextColor(Color.BLACK)
                CancelRequest()
                request=true
            }

        }
        binding.delete.setOnClickListener {
            ShowDeleteDialog(camps_id[position],campList[position].camp_name.toString())
        }
        binding.edit.setOnClickListener {
            val intent=Intent(con,EditCampActivity::class.java)
            intent.putExtra("id",camps_id[position])
                .putExtra("camp",campList[position] as Serializable)
            con.startActivity(intent)
        }

       // campList?.get(position).let { holder.bind(holder,it,student,id_list,role) }
    }

    private fun ShowDeleteDialog(s: String, name: String) {
        val dialog=AlertDialog.Builder(con!!)
        dialog.setIcon(R.drawable.delete)
        dialog.setTitle("Delete")
        dialog.setMessage("Are You Sure Want To Delete ${name}")
        dialog.setPositiveButton("Yes"){d , which ->
            val db =Firebase.firestore
            db.collection("camp")
                .document(s)
                .delete()
                .addOnSuccessListener {  }
                .addOnFailureListener {  }
        }
        dialog.setNegativeButton("No"){d , which ->
            d.dismiss()
            // Toast.makeText(this.activity, "LogOut", Toast.LENGTH_SHORT).show()
        }
        dialog.show()


    }

    private fun CancelRequest() {
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

    private fun AddRequestToDatabase(c_name : String) {
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

    private fun UpdateView(binding: ShowCampBinding) {
        binding.stu.visibility =View.INVISIBLE
        binding.stu.layoutParams= RelativeLayout.LayoutParams(5, 10)
        binding.edit.visibility=View.INVISIBLE
        binding.delete.visibility=View.INVISIBLE
        binding.stuReq.visibility=View.VISIBLE
    }

    private fun CollectStuInfo(idList: MutableList<String>, n: String) {
        val db= Firebase.firestore
        idList.clear()
        var stu_list :MutableList<User> = arrayListOf()
        db.collection("users")
            .get().addOnSuccessListener {
                for (doc in it){
                    if (!doc["role"].toString().equals("Admin")){
                         idList.add(doc.id)
                        val user = User(doc["name"].toString(),doc["phone"].toString(),doc["email"].toString(),doc["password"].toString()
                            ,doc["img"].toString(),doc["role"].toString(),doc["degree"].toString(),doc["camp"].toString(),doc["request"].toString(),doc["noti"].toString())
                        // val user : User =doc.toObject(User::class.java)
                        stu_list.add(user)
                    }
                }
                ShowDialog(stu_list,idList,n)
            }
            .addOnFailureListener{

            }
    }

    private fun ShowDialog(stuList: MutableList<User>, idList: MutableList<String>, n: String) {

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
        ShowRecycler(stuList,idList,n)
        builder.show()
    }

    private fun ShowRecycler(stu: MutableList<User>, idList: MutableList<String>, n: String) {
        val stud : ArrayList<User> = arrayListOf()
        val id_new_list : MutableList<String> = arrayListOf()
        for (i in stu){
            if (i.request.toLowerCase().replace(" ","").equals(n.toLowerCase().replace(" ","")) && (i.camp.equals("")) || i.camp.toLowerCase().replace(" ","").equals(n.lowercase().replace(" ",""))){
                stud.add(i)
                id_new_list.add(idList[stu.indexOf(i)])

            }
        }
        b.recycleStudent.layoutManager= LinearLayoutManager(con)
        StudentInfoAdapter= StudentInfoAdapter(stud,id_new_list, n,
            con
        )
        b.recycleStudent.adapter=StudentInfoAdapter
    }

    private fun ShowImageRecycle(binding: ShowCampBinding, imageList: MutableList<Uri>) {
        var campImageAdapter : CampImageAdapter? =null
        binding.imageCampRecycle.visibility=View.VISIBLE
        binding.imageCampRecycle.layoutManager = LinearLayoutManager(con)
        campImageAdapter = CampImageAdapter(con!!,imageList)
        binding.imageCampRecycle.adapter=campImageAdapter
    }

    private fun CheckImage(binding: ShowCampBinding, image_list: MutableList<Uri>) {
        if (image_list.size == 0){
            image_list.add(def_img.toUri())
            NoImage(binding,image_list)
        }else if (image_list.size==1){
            Picasso.get().load(image_list[0].toString()).placeholder(R.drawable.cover).into(binding.firstImg)
            binding.secondImgLayout.visibility=View.INVISIBLE
            val param = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            binding.firstImg.layoutParams=param
            binding.imageCamp.isEnabled=false
        }else if (image_list.size==2){
            Picasso.get().load(image_list[0].toString()).placeholder(R.drawable.cover).into(binding.firstImg)
            Picasso.get().load(image_list[1].toString()).placeholder(R.drawable.cover).into(binding.secondImg)
            binding.thirdImg.visibility=View.INVISIBLE
            val param=LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            binding.secondImg.layoutParams=param
        }else if (image_list.size==3){
            Picasso.get().load(image_list[0].toString()).placeholder(R.drawable.cover).into(binding.firstImg)
            Picasso.get().load(image_list[1].toString()).placeholder(R.drawable.cover).into(binding.secondImg)
            Picasso.get().load(image_list[2].toString()).placeholder(R.drawable.cover).into(binding.thirdImg)
            binding.plus.visibility=View.INVISIBLE
        }else if (image_list.size > 3){
            Picasso.get().load(image_list[0].toString()).placeholder(R.drawable.cover).into(binding.firstImg)
            Picasso.get().load(image_list[1].toString()).placeholder(R.drawable.cover).into(binding.secondImg)
            Picasso.get().load(image_list[2].toString()).placeholder(R.drawable.cover).into(binding.thirdImg)
            binding.plus.visibility=View.VISIBLE
        }
    }
    private fun NoImage(binding: ShowCampBinding,image_list: MutableList<Uri>) {
        binding.imageCamp.isEnabled=false
        binding.secondImgLayout.visibility=View.INVISIBLE
        Picasso.get().load(image_list[0].toString()).placeholder(R.drawable.cover).into(binding.firstImg)
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

