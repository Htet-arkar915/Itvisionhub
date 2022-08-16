package com.htetarkarlinn.itvisionhub.Fragment

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.htetarkarlinn.itvisionhub.Activities.LoginActivity
import com.htetarkarlinn.itvisionhub.MainActivity
import com.htetarkarlinn.itvisionhub.Models.User
import com.htetarkarlinn.itvisionhub.R
import com.htetarkarlinn.itvisionhub.databinding.FragmentProfileBinding
import com.squareup.picasso.Picasso

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    private lateinit var profileViewModel:  ProfileViewModel
    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!
//    val user : User = TODO()
    var email : String =""
    var pass : String =""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sharedPreferences :SharedPreferences =this.activity?.getSharedPreferences("User",Context.MODE_PRIVATE)!!
        email=sharedPreferences.getString("email","name").toString()
        pass=sharedPreferences.getString("password","password").toString()
        profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root=binding.root
        
        val d = Log.d("HH", "HH")

        val db=Firebase.firestore

        db.collection("users")
            .get().addOnSuccessListener{
                for (doc in it){
                    if (doc["email"]?.equals(email)!! && doc["password"]?.equals(pass)!!){
                        //Toast.makeText(this.activity, doc["name"].toString(), Toast.LENGTH_SHORT).show()
                        binding.profileName.text=doc["name"].toString()
                        val img=doc["img"].toString()
                        Picasso.get().load(img).placeholder(R.drawable.person).fit().into(binding.circleImageView)
                        binding.pEmail.text=doc["email"].toString()
                        binding.phone.text=doc["phone"].toString()
                        if (doc["role"].toString().equals("Admin")){
                            binding.degree.visibility=View.INVISIBLE
                            binding.degree.layoutParams=LinearLayout.LayoutParams(0,0)
                        }
                        binding.pUniversity.text=doc["degree"].toString()
                        binding.userRole.text=doc["role"].toString()
                    }
                }
            }

            .addOnFailureListener {
                Toast.makeText(this.activity, "Found error $it", Toast.LENGTH_SHORT).show()
            }
        binding.logOut.setOnClickListener{

            val dialog=AlertDialog.Builder(this.context!!)
            dialog.setTitle("LogOut")
            dialog.setMessage("Are You Sure Want To LogOut?")
            dialog.setPositiveButton("Yes"){d , which ->
                val sh : SharedPreferences =this.activity?.getSharedPreferences("Login",Context.MODE_PRIVATE)!!
                val editor: SharedPreferences.Editor = sh.edit()
                editor.remove("LoginSuccess")
                editor.apply()
                val sh_role : SharedPreferences= this.activity?.getSharedPreferences("Role", MODE_PRIVATE)!!
                val edt : SharedPreferences.Editor= sh_role.edit()
                edt.remove("role")
                edt.apply()
                startActivity(Intent(this.activity,LoginActivity::class.java))
                this.activity?.finish()
            }
            dialog.setNegativeButton("No"){d , which ->
                d.dismiss()
               // Toast.makeText(this.activity, "LogOut", Toast.LENGTH_SHORT).show()
            }
            dialog.show()
        }

        //val textView: TextView = binding.textl
      /*  profileViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })*/


        return root
    }

}