package com.htetarkarlinn.itvisionhub.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.htetarkarlinn.itvisionhub.Models.CampModel
import com.htetarkarlinn.itvisionhub.Models.User
import com.htetarkarlinn.itvisionhub.R
import com.htetarkarlinn.itvisionhub.databinding.ShowCampBinding
import com.htetarkarlinn.itvisionhub.databinding.StudentInfoBinding
import com.squareup.picasso.Picasso

class CampAdapter(val campList: MutableList<CampModel>,val student : MutableList<User>,val id_list : MutableList<String>,val con: Context?,val role : String) : RecyclerView.Adapter<CampAdapter.MyViewHolder>() {
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var StudentInfoAdapter: StudentInfoAdapter? = null
        private var campImageAdapter : CampImageAdapter? =null
        val binding = ShowCampBinding.bind(view)
        private lateinit var b : StudentInfoBinding
        var n : String = ""
        var des : String=""
        var image_check=true
        var image_list : MutableList<Uri> = arrayListOf()
        var images : MutableList<Uri> = arrayListOf()
        var stu_list :MutableList<User> = arrayListOf()
        fun bind(binding: ShowCampBinding, it: CampModel?, student: MutableList<User>, id_list: MutableList<String>,role: String) {
            binding.name.text=it?.camp_name
            n=it?.camp_name.toString()
            for(img in it?.images!!.toArray()){
                image_list.add(img!!.toString().toUri())
            }
            images= image_list.distinct().toMutableList()
            CheckImage(images)
            binding.imageCamp.setOnClickListener {
                if (image_check){
                if (images.size>0){
                    val rec_param = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    binding.imageCampRecycle.layoutParams=rec_param

                binding.secondImgLayout.visibility=View.INVISIBLE
                binding.firstImg.visibility=View.INVISIBLE
                binding.imageCampRecycle.visibility=View.VISIBLE
                ShowImageRecycle(images)
                }else{
                   NoImage()
                }
                    image_check=false
                }
            }
            binding.layoutMain.setOnClickListener {
                if (!image_check){
                   // Toast.makeText(itemView.context, "click", Toast.LENGTH_SHORT).show()
                    binding.imageCampRecycle.layoutParams=LinearLayout.LayoutParams(0,0)
                    binding.imageCampRecycle.visibility=View.INVISIBLE
                    binding.secondImgLayout.visibility=View.VISIBLE
                    binding.firstImg.visibility=View.VISIBLE
                    //CheckImage(image_list)
                    image_check=true
                }
            }
            binding.campPeroid.text="Peroid - ${it?.start_date.toString()} to ${it?.end_date.toString()}"
            binding.dailyTime.text="Time - ${it?.time.toString()}"
            des=it?.description.toString()
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

            if (role.equals("Student")) {
                UpdateView()
            }
            binding.stuAddRemove.setOnClickListener {
                val progressBar : ProgressBar
                CollectStuInfo(id_list,n)

                 }
            binding.delete.setOnClickListener {
               // ShowDeleteDialog(student,id_list)
            }
        }

        private fun NoImage() {
            binding.imageCamp.isEnabled=false
            binding.secondImgLayout.visibility=View.INVISIBLE
            binding.secondImgLayout.layoutParams=LinearLayout.LayoutParams(1,1)
            binding.secondImg.layoutParams=LinearLayout.LayoutParams(1,1)
            binding.thirdImg.layoutParams=RelativeLayout.LayoutParams(1,1)
            val param = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            binding.firstImg.layoutParams=param
        }

        private fun CheckImage(imageList: MutableList<Uri>) {
            if (imageList.size==1){
                Picasso.get().load(imageList[0].toString()).placeholder(R.drawable.cover).into(binding.firstImg)
                binding.secondImgLayout.visibility=View.INVISIBLE
                val param = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                binding.firstImg.layoutParams=param
                binding.imageCamp.isEnabled=false
            }else if (imageList.size==2){
                Picasso.get().load(imageList[0].toString()).placeholder(R.drawable.cover).into(binding.firstImg)
                Picasso.get().load(imageList[1].toString()).placeholder(R.drawable.cover).into(binding.secondImg)
                binding.thirdImg.visibility=View.INVISIBLE
                val param=LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                binding.secondImg.layoutParams=param
            }else if (imageList.size==3){
                Picasso.get().load(imageList[0].toString()).placeholder(R.drawable.cover).into(binding.firstImg)
                Picasso.get().load(imageList[1].toString()).placeholder(R.drawable.cover).into(binding.secondImg)
                Picasso.get().load(imageList[2].toString()).placeholder(R.drawable.cover).into(binding.thirdImg)
            }else if (imageList.size==0){
                NoImage()

            }else{
                Picasso.get().load(imageList[0].toString()).placeholder(R.drawable.cover).into(binding.firstImg)
                Picasso.get().load(imageList[1].toString()).placeholder(R.drawable.cover).into(binding.secondImg)
                Picasso.get().load(imageList[2].toString()).placeholder(R.drawable.cover).into(binding.thirdImg)
                binding.plus.visibility=View.VISIBLE
            }
        }

        private fun ShowImageRecycle(image_list: MutableList<Uri>) {
            //Toast.makeText(itemView.context, image_list.size.toString(), Toast.LENGTH_SHORT).show()
            binding.imageCampRecycle.visibility=View.VISIBLE
            binding.imageCampRecycle.layoutManager = LinearLayoutManager(itemView.context)
            campImageAdapter = CampImageAdapter(itemView.context,image_list)
            binding.imageCampRecycle.adapter=campImageAdapter
        }

        private fun CollectStuInfo(id_list: MutableList<String>,n: String) {
            val db= Firebase.firestore
            db.collection("users")
                .get().addOnSuccessListener {
                    for (doc in it){
                        if (!doc["role"].toString().equals("Admin")){
                           // id_list.add(doc.id)
                            val user = User(doc["name"].toString(),doc["phone"].toString(),doc["email"].toString(),doc["password"].toString()
                                ,doc["img"].toString(),doc["role"].toString(),doc["degree"].toString(),doc["camp"].toString())
                            // val user : User =doc.toObject(User::class.java)
                            stu_list.add(user)
                        }
                    }
                    ShowDialog(stu_list,id_list,n)
                }
                .addOnFailureListener{

                }
        }

        private fun UpdateView() {
            binding.stu.visibility =View.INVISIBLE
            binding.stu.layoutParams= RelativeLayout.LayoutParams(5, 10)
            binding.edit.visibility=View.INVISIBLE
            binding.delete.visibility=View.INVISIBLE
        }

        private fun ShowDialog(student: MutableList<User>, id_list: MutableList<String>, n: String) {
            val builder = AlertDialog.Builder(itemView.context,R.style.CustomAlertDialogTran).create()
            val view=LayoutInflater.from(itemView.context).inflate(R.layout.student_info,null)
            b= StudentInfoBinding.bind(view)
            view.background=getDrawable(itemView.context,R.drawable.transparent_bg)
            builder.setView(view)
            builder.setCancelable(false)
            b.closeDialog.setOnClickListener {
                student.clear()
                builder.dismiss()
            }
            ShowRecycler(student,id_list,n)
            builder.show()
        }

        private fun ShowRecycler(student: MutableList<User>, idList: MutableList<String>, n: String) {
            val stud : ArrayList<User> = arrayListOf()
            val id_new_list : MutableList<String> = arrayListOf()
            for (i in student){
                if (i.camp.toLowerCase().replace(" ","").equals(n.toLowerCase().replace(" ","")) || i.camp.equals("")){
                    stud.add(i)
                    id_new_list.add(idList[student.indexOf(i)])

                }
            }
            b.recycleStudent.layoutManager= LinearLayoutManager(itemView.context)
            StudentInfoAdapter= StudentInfoAdapter(stud,id_new_list, n,
                itemView.context
            )
            b.recycleStudent.adapter=StudentInfoAdapter
        }

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.show_camp,parent,false)
        return CampAdapter.MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        campList?.get(position).let { holder.bind(holder.binding,it,student,id_list,role) }
    }
    override fun getItemCount(): Int {
        return campList.size
    }

}
