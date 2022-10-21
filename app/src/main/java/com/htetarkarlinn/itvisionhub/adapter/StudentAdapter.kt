package com.htetarkarlinn.itvisionhub.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.htetarkarlinn.itvisionhub.activities.StudentRemainder
import com.htetarkarlinn.itvisionhub.models.User
import com.htetarkarlinn.itvisionhub.R
import com.htetarkarlinn.itvisionhub.databinding.UserCustomBinding
import com.htetarkarlinn.itvisionhub.databinding.UserDetailBinding
import com.squareup.picasso.Picasso

class StudentAdapter(val userList: MutableList<User>,val activity: FragmentActivity?):
    RecyclerView.Adapter<StudentAdapter.MyViewHolder>() {
    class MyViewHolder(view: View):RecyclerView.ViewHolder(view) {
        private lateinit var b : UserDetailBinding
        val binding =UserCustomBinding.bind(view)
        var student : User? = null
        fun bind(binding: UserCustomBinding, it: User?) {
            binding.userListName.text=it?.name.toString()
            binding.userListPhone.text=it?.phone.toString()
            binding.userListEmail.text=it?.email.toString()
            student=it
            if (it?.camp==""){
                binding.userListCamp.text="No camp Attending"
            }else {
                binding.userListCamp.text = "Attend in ${it?.camp.toString()}"
            }
            binding.wholeLayout.setOnClickListener {
                ShowDialog(student)
            }
            Picasso.get().load(it?.img.toString()).placeholder(R.drawable.person).fit().into(binding.studentImg)
        }

        private fun ShowDialog(student: User?) {
            val builder = AlertDialog.Builder(itemView.context,R.style.CustomAlertDialogTran).create()
            val view=LayoutInflater.from(itemView.context).inflate(R.layout.user_detail,null)
            b= UserDetailBinding.bind(view)
            view.background=getDrawable(itemView.context, R.drawable.profile_bg)
            builder.setView(view)
            builder.setCancelable(false)
            b.closeDialog.setOnClickListener {
                builder.dismiss()
            }
            Picasso.get().load(student?.img.toString()).placeholder(R.drawable.person).into(b.profileImage)
            b.phoneNum.text=student?.phone
            b.gmail.text=student?.email
            b.userName.text=student?.name
            b.degree.text=student?.degree
            if (student?.camp.equals("")){
                b.camp.text="No attending camp"
            }else {
                b.camp.text = student?.camp
            }
            b.callPhone.setOnClickListener {
                val dailIntent=Intent(Intent.ACTION_DIAL)
                dailIntent.data= Uri.parse("tel:"+"${student?.phone}")
                itemView.context.startActivity(dailIntent)
            }
            b.gmailSend.setOnClickListener {
                sendEmail()
            }
            b.sendReminder.setOnClickListener {
                var intent=Intent(itemView.context,StudentRemainder::class.java)
                intent.putExtra("email",student?.email )
                itemView.context.startActivity(intent)
            }
            builder.show()
        }

        private fun sendEmail() {
                val mIntent = Intent(Intent.ACTION_SEND)
                mIntent.data = Uri.parse("mailto:"+"${student?.email}")
                mIntent.type = "text/plain"

                try {
                    //start email intent
                    itemView.context.startActivity(Intent.createChooser(mIntent, "Choose Email Client..."))
                }
                catch (e: Exception){
                    Toast.makeText(itemView.context, e.message, Toast.LENGTH_LONG).show()
                }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.user_custom,parent,false)
        return MyViewHolder(view)
        /*val view= LayoutInflater.from(parent.context).inflate(R.layout.menu_histry,parent,false)
        return MyViewHolder(view)*/
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        userList[position].let { holder.bind(holder.binding,it) }
    }
    override fun getItemCount(): Int {
        return userList.size
    }
}
