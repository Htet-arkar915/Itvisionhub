package com.htetarkarlinn.itvisionhub.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.htetarkarlinn.itvisionhub.models.User
import com.htetarkarlinn.itvisionhub.R
import com.htetarkarlinn.itvisionhub.databinding.StudentAddRemoveBinding
import com.squareup.picasso.Picasso

class StudentInfoAdapter(val students : MutableList<User>,val id_list: MutableList<String>,val camp_name: String,val addCampActivity: Context?): RecyclerView.Adapter<StudentInfoAdapter.MyViewHolder>() {
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val binding = StudentAddRemoveBinding.bind(view)
        fun bind(binding: StudentAddRemoveBinding, it: User?,doc :String?, camp_name: String) {
            var check=true
           // Toast.makeText(itemView.context, doc, Toast.LENGTH_SHORT).show()
            binding.userListName.text=it?.name.toString()
            binding.userListPhone.text=it?.phone.toString()
            binding.userListEmail.text=it?.email.toString()
            val stu=User(it?.name.toString(),it?.phone.toString(),it?.email.toString(),
            it?.password.toString(),it?.img.toString(),it?.role.toString(),it?.degree.toString(),camp_name,it?.request.toString(),it?.noti.toString())
            val stu_remove=User(it?.name.toString(),it?.phone.toString(),it?.email.toString(),
                it?.password.toString(),it?.img.toString(),it?.role.toString(),it?.degree.toString(),"","","")
            val first_camp_name=camp_name.replace(" ","").lowercase()
            val second_camp_name=it?.camp.toString().replace(" ","").lowercase()
            if (first_camp_name.equals(second_camp_name)){
                binding.addRemoveBtn.text="Remove"
                binding.addRemoveBtn.setTextColor(Color.RED)
                check= false
            }
            binding.addRemoveBtn.setOnClickListener {
                if (check){
                    binding.addRemoveBtn.text="Remove"
                    binding.addRemoveBtn.setTextColor(Color.RED)
                    AddStudent(doc,stu,camp_name)
                    check=false
                }else{
                    binding.addRemoveBtn.text="Accept"
                    binding.addRemoveBtn.setTextColor(Color.BLACK)
                    RemoveStudent(doc,stu_remove)
                    check=true
                }
            }
            Picasso.get().load(it?.img.toString()).placeholder(R.drawable.person).fit().into(binding.studentImg)
        }

        private fun RemoveStudent(doc: String?, stuRemove: User?) {
            val db=Firebase.firestore
            db.collection("users")
                .document(doc!!)
                .update("camp","","request","")
                .addOnSuccessListener {
                    Toast.makeText(itemView.context, "Removed", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(itemView.context, "Retry", Toast.LENGTH_SHORT).show()
                }
        }

        private fun AddStudent(d: String?, stu: User?,camp_name: String) {
            
            val db=Firebase.firestore
            db.collection("users")
                .document(d!!)
                .update("camp",camp_name,"request",camp_name)
                .addOnSuccessListener {
                    Toast.makeText(itemView.context, "Added", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(itemView.context, "Retry", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.student_add_remove,parent,false)
        return StudentInfoAdapter.MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        students[position].let { holder.bind(holder.binding,it,id_list[position],camp_name) }
    }

    override fun getItemCount(): Int {
        return students.size
    }

}
