package com.htetarkarlinn.itvisionhub.fragment

import android.content.Context
import android.content.Context.MODE_PRIVATE
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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.htetarkarlinn.itvisionhub.activities.LoginActivity
import com.htetarkarlinn.itvisionhub.R
import com.htetarkarlinn.itvisionhub.databinding.FragmentProfileBinding
import com.squareup.picasso.Picasso
class ProfileFragment : Fragment() {
    private lateinit var profileViewModel:  ProfileViewModel
    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!
    var email : String =""
    var pass : String =""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val sharedPreferences :SharedPreferences =this.requireActivity().getSharedPreferences("User",Context.MODE_PRIVATE)!!
        email=sharedPreferences.getString("email","name").toString()
        pass=sharedPreferences.getString("password","password").toString()
        profileViewModel =
            ViewModelProvider(this)[ProfileViewModel::class.java]
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root=binding.root

        Log.d("HH", "HH")

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
                        if (doc["role"].toString() == "Admin"){
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

            val dialog=AlertDialog.Builder(this.requireContext())
            dialog.setTitle("LogOut")
            dialog.setMessage("Are You Sure Want To LogOut?")
            dialog.setPositiveButton("Yes"){ _, _ ->
                val sh : SharedPreferences =this.requireActivity().getSharedPreferences("Login",Context.MODE_PRIVATE)!!
                val editor: SharedPreferences.Editor = sh.edit()
                editor.remove("LoginSuccess")
                editor.apply()
                val shRole : SharedPreferences= this.requireActivity().getSharedPreferences("Role", MODE_PRIVATE)!!
                val edt : SharedPreferences.Editor= shRole.edit()
                edt.remove("role")
                edt.apply()
                startActivity(Intent(this.activity,LoginActivity::class.java))
                this.activity?.finish()
            }
            dialog.setNegativeButton("No"){ d, _ ->
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